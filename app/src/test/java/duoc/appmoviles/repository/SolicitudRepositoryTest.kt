package duoc.appmoviles.repository

import duoc.appmoviles.model.Solicitud
import duoc.appmoviles.network.SolicitudApiService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

// Pruebas del SolicitudRepository: el ApiService (Retrofit) se simula con MockK,
// así se prueba la lógica sin necesitar el microservicio encendido
class SolicitudRepositoryTest : StringSpec({

    val solicitud = Solicitud(id = 3, categoria = "Respaldos",
        descripcion = "No se como respaldar mis fotos", estado = "Pendiente", fecha = 1000)

    "listar() devuelve lo que responde el microservicio" {
        val api = mockk<SolicitudApiService>()
        coEvery { api.listar() } returns listOf(solicitud)

        SolicitudRepository(api).listar() shouldContainExactly listOf(solicitud)
    }

    "crear() envía solo categoría y descripción (el servidor pone id, estado y fecha)" {
        val api = mockk<SolicitudApiService>()
        coEvery { api.crear(any()) } returns solicitud

        val resultado = SolicitudRepository(api).crear("Respaldos", "No se como respaldar mis fotos")

        resultado shouldBe solicitud
        coVerify {
            api.crear(Solicitud(categoria = "Respaldos", descripcion = "No se como respaldar mis fotos"))
        }
    }

    "actualizar() usa el id de la solicitud en la dirección del PUT" {
        val api = mockk<SolicitudApiService>()
        coEvery { api.actualizar(3L, any()) } answers { secondArg() }

        val cambiada = solicitud.copy(estado = "Resuelta")

        SolicitudRepository(api).actualizar(cambiada) shouldBe cambiada
        coVerify { api.actualizar(3L, cambiada) }
    }

    "actualizar() falla si la solicitud no tiene id" {
        val api = mockk<SolicitudApiService>()

        shouldThrow<IllegalArgumentException> {
            SolicitudRepository(api).actualizar(solicitud.copy(id = null))
        }
    }

    "eliminar() devuelve true si el servidor respondió bien" {
        val api = mockk<SolicitudApiService>()
        coEvery { api.eliminar(3L) } returns Response.success(Unit)

        SolicitudRepository(api).eliminar(3L) shouldBe true
    }

    "eliminar() devuelve false si el servidor respondió 404" {
        val api = mockk<SolicitudApiService>()
        coEvery { api.eliminar(9L) } returns Response.error(404, "".toResponseBody(null))

        SolicitudRepository(api).eliminar(9L) shouldBe false
    }
})
