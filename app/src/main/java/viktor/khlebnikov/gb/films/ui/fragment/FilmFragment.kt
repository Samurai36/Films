package viktor.khlebnikov.gb.films.ui.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import viktor.khlebnikov.gb.films.R
import viktor.khlebnikov.gb.films.databinding.FragmentFilmBinding
import viktor.khlebnikov.gb.films.model.Film
import viktor.khlebnikov.gb.films.ui.adapter.FilmAdapter
import viktor.khlebnikov.gb.films.ui.adapter.FilmLoadStateAdapter
import viktor.khlebnikov.gb.films.ui.viewmodel.FilmViewModel

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class FilmFragment : Fragment(R.layout.fragment_film), FilmAdapter.OnItemClickListener  {

    private var _binding: FragmentFilmBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<FilmViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFilmBinding.bind(view)

        val adapter = FilmAdapter(this)

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = FilmLoadStateAdapter { adapter.retry() },
                footer = FilmLoadStateAdapter { adapter.retry() }
            )
            btnRetry.setOnClickListener {
                adapter.retry()
            }
        }

        viewModel.films.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error
                tvErrorLoadData.isVisible = loadState.source.refresh is LoadState.Error
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                    tvFilmNotFound.isVisible = true
                } else {
                    tvFilmNotFound.isVisible = false
                }
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
        val searchItem = menu.findItem(R.id.menuSearch)
        val searchView = searchItem.actionView as SearchView
        var job: Job? = null
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                job?.cancel()
                job = MainScope().launch {
                    newText?.let {
                        delay(500)
                        if (newText.isNotEmpty()) {
                            binding.recyclerView.scrollToPosition(0)
                            viewModel.searchFilms(newText)
                        }
                    }
                }
                return true
            }
        })
    }

    override fun onItemClick(film: Film) {
        findNavController().navigate(
            FilmFragmentDirections.actionMovieFragmentToDetailsFragment(
                film
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}