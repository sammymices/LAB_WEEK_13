package com.example.lab_week_13

import android.util.Log
import com.example.lab_week_13.api.MovieService
import com.example.lab_week_13.database.MovieDatabase
import com.example.lab_week_13.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepository(private val movieService: MovieService, private val movieDatabase: MovieDatabase) {

    private val apiKey = "c7413f942adde60a4db0fe94c592fdd5"

    // Flow that emits a list of movies
    fun fetchMovies(): Flow<List<Movie>> {
        return flow {

            val movieDao = movieDatabase.movieDao()
            val savedMovies = movieDao.getMovies()

            if (savedMovies.isEmpty()) {

                val movies = movieService
                    .getPopularMovies(apiKey)
                    .results

                movieDao.addMovies(movies)

                emit(movies)

            } else {

                emit(savedMovies)

            }
        }.flowOn(Dispatchers.IO)
    }
    // fetch movies from the API and save them to the database
    suspend fun fetchMoviesFromNetwork() {

        val movieDao = movieDatabase.movieDao()

        try {
            val popularMovies = movieService.getPopularMovies(apiKey)
            val moviesFetched = popularMovies.results

            // save movies to database
            movieDao.addMovies(moviesFetched)

        } catch (exception: Exception) {
            Log.d(
                "MovieRepository",
                "An error occurred: ${exception.message}"
            )
        }
    }

}
