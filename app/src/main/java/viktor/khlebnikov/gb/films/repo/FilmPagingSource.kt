package viktor.khlebnikov.gb.films.repo

import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import viktor.khlebnikov.gb.films.api.ApiService
import viktor.khlebnikov.gb.films.api.FilmResponse
import viktor.khlebnikov.gb.films.model.Film

class FilmPagingSource(
    private val apiService: ApiService,
    private val query: String?,
) : RxPagingSource<Int, Film>() {

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Film>> {
        val page = params.key ?: 1
        return if (query != null) {
            apiService.searchMovies(query, page)
                .subscribeOn(Schedulers.io())
                .map { toLoadResult(it, page) }
                .onErrorReturn { LoadResult.Error(it) }
        } else {
            apiService.getNowPlayingMovies(page)
                .subscribeOn(Schedulers.io())
                .map { toLoadResult(it, page) }
                .onErrorReturn { LoadResult.Error(it) }
        }
    }

    private fun toLoadResult(
        television: FilmResponse,
        page: Int
    ): LoadResult<Int, Film> {
        return LoadResult.Page(
            data = television.results,
            prevKey = if (page == 1) null else page - 1,
            nextKey = if (television.results.isEmpty()) null else page + 1
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Film>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}