package duoc.appmoviles.repository

import duoc.appmoviles.model.VersionAndroid
import duoc.appmoviles.network.EndOfLifeApiService
import duoc.appmoviles.network.RetrofitClient

// Obtiene las versiones de Android desde la API externa endoflife.date.
// "open" y el ApiService por constructor permiten reemplazarlo en las pruebas.
open class VersionAndroidRepository(
    private val api: EndOfLifeApiService = RetrofitClient.endOfLifeApi
) {

    // GET: todas las versiones, de la más nueva a la más antigua (así las entrega la API)
    open suspend fun obtenerVersiones(): List<VersionAndroid> = api.obtenerVersionesAndroid()
}
