package duoc.appmoviles.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // API de consejos: el archivo consejos.json del repositorio en GitHub
    private const val BASE_URL = "https://raw.githubusercontent.com/"

    // API de publicaciones de prueba (Guía 14)
    private const val POSTS_BASE_URL = "https://jsonplaceholder.typicode.com/"

    // Se crea una sola vez, la primera vez que se usa
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // Segunda API, con otra dirección base
    val postsApi: PostApiService by lazy {
        Retrofit.Builder()
            .baseUrl(POSTS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // convierte el JSON en objetos
            .build()
            .create(PostApiService::class.java)
    }
}