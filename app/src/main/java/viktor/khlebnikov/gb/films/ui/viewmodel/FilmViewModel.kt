package viktor.khlebnikov.gb.films.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.rxjava3.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import viktor.khlebnikov.gb.films.model.Film
import viktor.khlebnikov.gb.films.repo.FilmRepository
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class FilmViewModel @Inject constructor(
    private val repository: FilmRepository,
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _films = MutableLiveData<PagingData<Film>>()
    val films: LiveData<PagingData<Film>> get() = _films

    init {
        getNowPlayingMovies()
    }

    fun searchFilms(query: String) {
        compositeDisposable.add(
            repository.searchMovies(query)
                .cachedIn(viewModelScope)
                .subscribe {
                    _films.value = it
                }
        )
    }

    private fun getNowPlayingMovies() {
        compositeDisposable.add(
            repository.getNowPlayingMovies()
                .cachedIn(viewModelScope)
                .subscribe {
                    _films.value = it
                }
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}