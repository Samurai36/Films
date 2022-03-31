package viktor.khlebnikov.gb.films.api

import viktor.khlebnikov.gb.films.model.Film

data class FilmResponse(
    val results: List<Film>,
)