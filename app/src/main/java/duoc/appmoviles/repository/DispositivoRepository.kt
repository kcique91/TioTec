package duoc.appmoviles.repository

import duoc.appmoviles.model.Dispositivo
import duoc.appmoviles.network.DispositivoApiService
import duoc.appmoviles.network.RetrofitClient

// Se encarga de hablar con el microservicio de dispositivos: el ViewModel le pide todo a él.
// "open" y el ApiService por constructor permiten reemplazarlo en las pruebas.
open class DispositivoRepository(
    private val api: DispositivoApiService = RetrofitClient.dispositivosApi
) {

    // GET: todos los dispositivos
    open suspend fun listar(): List<Dispositivo> = api.listar()

    // POST: registra un dispositivo nuevo
    open suspend fun crear(nombre: String, sistema: String): Dispositivo =
        api.crear(Dispositivo(nombre = nombre, sistema = sistema))

    // PUT: guarda los cambios de un dispositivo que ya existe
    open suspend fun actualizar(dispositivo: Dispositivo): Dispositivo {
        val id = requireNotNull(dispositivo.id) { "El dispositivo no tiene id" }
        return api.actualizar(id, dispositivo)
    }

    // DELETE: true si el servidor lo eliminó
    open suspend fun eliminar(id: Long): Boolean = api.eliminar(id).isSuccessful
}
