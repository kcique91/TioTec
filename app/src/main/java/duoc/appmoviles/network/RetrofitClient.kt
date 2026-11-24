package duoc.appmoviles.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // API de consejos: el archivo consejos.json del repositorio en GitHub
    private const val BASE_URL = "https://raw.githubusercontent.com/"

    // API de publicaciones de prueba (Guía 14)
    private const val POSTS_BASE_URL = "https://jsonplaceholder.typicode.com/"

    // Microservicios propios (Spring Boot). 10.0.2.2 es el computador visto desde el emulador.
    private const val SOLICITUDES_BASE_URL = "http://10.0.2.2:8081/"
    private const val DISPOSITIVOS_BASE_URL = "http://10.0.2.2:8082/"

    // Crea un cliente Retrofit para una dirección base
    private fun crear(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create()) // convierte el JSON en objetos
            .build()

    // Se crea una sola vez, la primera vez que se usa
    val api: ApiService by lazy {
        crear(BASE_URL).create(ApiService::class.java)
    }

    // Segunda API, con otra dirección base
    val postsApi: PostApiService by lazy {
        crear(POSTS_BASE_URL).create(PostApiService::class.java)
    }

    // Microservicio de solicitudes de asistencia
    val solicitudesApi: SolicitudApiService by lazy {
        crear(SOLICITUDES_BASE_URL).create(SolicitudApiService::class.java)
    }

    // Microservicio de dispositivos de prueba
    val dispositivosApi: DispositivoApiService by lazy {
        crear(DISPOSITIVOS_BASE_URL).create(DispositivoApiService::class.java)
    }
}
