package viktor.khlebnikov.gb.films.repo


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.rxjava3.flowable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import viktor.khlebnikov.gb.films.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ExperimentalCoroutinesApi
class FilmRepository @Inject constructor(
    private val apiService: ApiService,
) {

    fun getNowPlayingMovies() =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 60,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { FilmPagingSource(apiService, null) }
        ).flowable

    fun searchMovies(query: String) =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 60,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { FilmPagingSource(apiService, query) }
        ).flowable

}