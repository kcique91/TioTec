package duoc.appmoviles.network

import duoc.appmoviles.model.Consejo
import retrofit2.http.GET

interface ApiService {

    // Pide la lista de consejos (el archivo consejos.json del repo)
    @GET("kcique91/TioTec/master/consejos.json")
    suspend fun obtenerConsejos(): List<Consejo>
}