package duoc.appmoviles.repository

import duoc.appmoviles.model.Dispositivo
import duoc.appmoviles.network.DispositivoApiService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

// Pruebas del DispositivoRepository con el ApiService simulado (MockK)
class DispositivoRepositoryTest : StringSpec({

    val celular = Dispositivo(id = 1, nombre = "Celular de prueba", sistema = "Android 14")

    "listar() devuelve lo que responde el microservicio" {
        val api = mockk<DispositivoApiService>()
        coEvery { api.listar() } returns listOf(celular)

        DispositivoRepository(api).listar() shouldContainExactly listOf(celular)
    }

    "crear() envía nombre y sistema sin id" {
        val api = mockk<DispositivoApiService>()
        coEvery { api.crear(any()) } returns celular

        DispositivoRepository(api).crear("Celular de prueba", "Android 14") shouldBe celular
        coVerify { api.crear(Dispositivo(nombre = "Celular de prueba", sistema = "Android 14")) }
    }

    "actualizar() usa el id del dispositivo en la dirección del PUT" {
        val api = mockk<DispositivoApiService>()
        coEvery { api.actualizar(1L, any()) } answers { secondArg() }
        val cambiado = celular.copy(sistema = "Android 15")

        DispositivoRepository(api).actualizar(cambiado) shouldBe cambiado
    }

    "actualizar() falla si el dispositivo no tiene id" {
        val api = mockk<DispositivoApiService>()

        shouldThrow<IllegalArgumentException> {
            DispositivoRepository(api).actualizar(celular.copy(id = null))
        }
    }

    "eliminar() devuelve true o false según la respuesta del servidor" {
        val api = mockk<DispositivoApiService>()
        coEvery { api.eliminar(1L) } returns Response.success(Unit)
        coEvery { api.eliminar(2L) } returns Response.error(404, "".toResponseBody(null))

        DispositivoRepository(api).eliminar(1L) shouldBe true
        DispositivoRepository(api).eliminar(2L) shouldBe false
    }
})
