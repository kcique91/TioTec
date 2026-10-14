package duoc.appmoviles.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Dirección base; al final de ella se agrega la ruta de ApiService
    private const val BASE_URL = "https://raw.githubusercontent.com/"

    // Se crea una sola vez, la primera vez que se usa
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}