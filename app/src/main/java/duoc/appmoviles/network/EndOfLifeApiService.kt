package duoc.appmoviles.network

import duoc.appmoviles.model.VersionAndroid
import retrofit2.http.GET

// API externa pública endoflife.date: fechas de soporte de cada versión de Android
interface EndOfLifeApiService {

    // GET https://endoflife.date/api/android.json
    @GET("api/android.json")
    suspend fun obtenerVersionesAndroid(): List<VersionAndroid>
}
