package duoc.appmoviles.repository

import duoc.appmoviles.model.Solicitud
import duoc.appmoviles.network.RetrofitClient
import duoc.appmoviles.network.SolicitudApiService

// Se encarga de hablar con el microservicio de solicitudes: el ViewModel le pide todo a él.
// "open" y el ApiService por constructor permiten reemplazarlo en las pruebas.
open class SolicitudRepository(
    private val api: SolicitudApiService = RetrofitClient.solicitudesApi
) {

    // GET: todas las solicitudes
    open suspend fun listar(): List<Solicitud> = api.listar()

    // POST: crea una solicitud nueva (el servidor le pone id, estado y fecha)
    open suspend fun crear(categoria: String, descripcion: String): Solicitud =
        api.crear(Solicitud(categoria = categoria, descripcion = descripcion))

    // PUT: guarda los cambios de una solicitud que ya existe
    open suspend fun actualizar(solicitud: Solicitud): Solicitud {
        val id = requireNotNull(solicitud.id) { "La solicitud no tiene id" }
        return api.actualizar(id, solicitud)
    }

    // DELETE: true si el servidor la eliminó
    open suspend fun eliminar(id: Long): Boolean = api.eliminar(id).isSuccessful
}
