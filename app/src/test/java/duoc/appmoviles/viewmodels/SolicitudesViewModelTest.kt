package duoc.appmoviles.viewmodels

import duoc.appmoviles.model.Solicitud
import duoc.appmoviles.repository.SolicitudRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

// Pruebas del SolicitudesViewModel.
// El repositorio se reemplaza por un mock (MockK): no se usa el microservicio real.
@OptIn(ExperimentalCoroutinesApi::class)
class SolicitudesViewModelTest : StringSpec({

    // viewModelScope usa el hilo principal (Main); en las pruebas lo reemplazamos
    // por un dispatcher de prueba que ejecuta las corrutinas al instante
    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    val pendiente = Solicitud(
        id = 1, categoria = "Respaldos",
        descripcion = "No se como respaldar mis fotos", estado = "Pendiente", fecha = 1000
    )

    "al crearse carga las solicitudes del microservicio" {
        val repo = mockk<SolicitudRepository>()
        coEvery { repo.listar() } returns listOf(pendiente)

        val vm = SolicitudesViewModel(repo)

        vm.solicitudes.value shouldContainExactly listOf(pendiente)
        vm.cargando.value shouldBe false
        vm.error.value.shouldBeNull()
    }

    "si el microservicio no responde muestra un error" {
        val repo = mockk<SolicitudRepository>()
        coEvery { repo.listar() } throws RuntimeException("sin conexión")

        val vm = SolicitudesViewModel(repo)

        vm.solicitudes.value.shouldBeEmpty()
        vm.error.value.shouldNotBeNull()
        vm.cargando.value shouldBe false
    }

    "no crea la solicitud si la descripción está vacía" {
        val repo = mockk<SolicitudRepository>()
        coEvery { repo.listar() } returns emptyList()
        val vm = SolicitudesViewModel(repo)

        vm.crearSolicitud() shouldBe false
        vm.form.value.errorDescripcion shouldBe "Campo obligatorio"
        coVerify(exactly = 0) { repo.crear(any(), any()) }
    }

    "no crea la solicitud si la descripción tiene menos de 10 caracteres" {
        val repo = mockk<SolicitudRepository>()
        coEvery { repo.listar() } returns emptyList()
        val vm = SolicitudesViewModel(repo)

        vm.onDescripcionChange("corta")

        vm.crearSolicitud() shouldBe false
        vm.form.value.errorDescripcion shouldBe "Describe el problema con al menos 10 caracteres"
    }

    "escribir en la descripción borra el error" {
        val repo = mockk<SolicitudRepository>()
        coEvery { repo.listar() } returns emptyList()
        val vm = SolicitudesViewModel(repo)
        vm.crearSolicitud()

        vm.onDescripcionChange("a")

        vm.form.value.errorDescripcion.shouldBeNull()
    }

    "crea una solicitud válida (POST) y la agrega arriba de la lista" {
        val repo = mockk<SolicitudRepository>()
        val nueva = Solicitud(id = 2, categoria = "Autenticación",
            descripcion = "Quiero activar dos pasos", estado = "Pendiente", fecha = 2000)
        coEvery { repo.listar() } returns listOf(pendiente)
        coEvery { repo.crear("Autenticación", "Quiero activar dos pasos") } returns nueva
        val vm = SolicitudesViewModel(repo)

        vm.onCategoriaChange("Autenticación")
        vm.onDescripcionChange("  Quiero activar dos pasos  ")

        vm.crearSolicitud() shouldBe true
        vm.solicitudes.value shouldContainExactly listOf(nueva, pendiente)
        vm.form.value.descripcion shouldBe ""
        vm.form.value.categoria shouldBe "Autenticación"
        coVerify(exactly = 1) { repo.crear("Autenticación", "Quiero activar dos pasos") }
    }

    "si falla el POST muestra un error" {
        val repo = mockk<SolicitudRepository>()
        coEvery { repo.listar() } returns emptyList()
        coEvery { repo.crear(any(), any()) } throws RuntimeException("caído")
        val vm = SolicitudesViewModel(repo)
        vm.onDescripcionChange("Mi celular no respalda nada")

        vm.crearSolicitud()

        vm.error.value shouldBe "No se pudo crear la solicitud"
    }

    "el estado avanza de Pendiente a En revisión y luego a Resuelta" {
        val repo = mockk<SolicitudRepository>()
        coEvery { repo.listar() } returns emptyList()
        val vm = SolicitudesViewModel(repo)

        vm.siguienteEstado("Pendiente") shouldBe "En revisión"
        vm.siguienteEstado("En revisión") shouldBe "Resuelta"
    }

    "avanzar estado (PUT) envía el nuevo estado y actualiza la tarjeta" {
        val repo = mockk<SolicitudRepository>()
        coEvery { repo.listar() } returns listOf(pendiente)
        // El servidor responde con la misma solicitud que recibió
        coEvery { repo.actualizar(any()) } answers { firstArg() }
        val vm = SolicitudesViewModel(repo)

        vm.avanzarEstado(pendiente)

        vm.solicitudes.value.first().estado shouldBe "En revisión"
        coVerify { repo.actualizar(pendiente.copy(estado = "En revisión")) }
    }

    "si falla el PUT muestra un error" {
        val repo = mockk<SolicitudRepository>()
        coEvery { repo.listar() } returns listOf(pendiente)
        coEvery { repo.actualizar(any()) } throws RuntimeException("caído")
        val vm = SolicitudesViewModel(repo)

        vm.avanzarEstado(pendiente)

        vm.error.value shouldBe "No se pudo actualizar la solicitud"
        vm.solicitudes.value.first().estado shouldBe "Pendiente"
    }

    "eliminar (DELETE) quita la solicitud de la lista" {
        val repo = mockk<SolicitudRepository>()
        coEvery { repo.listar() } returns listOf(pendiente)
        coEvery { repo.eliminar(1L) } returns true
        val vm = SolicitudesViewModel(repo)

        vm.eliminarSolicitud(pendiente)

        vm.solicitudes.value.shouldBeEmpty()
    }

    "si el servidor no elimina la solicitud, se mantiene y se avisa" {
        val repo = mockk<SolicitudRepository>()
        coEvery { repo.listar() } returns listOf(pendiente)
        coEvery { repo.eliminar(1L) } returns false
        val vm = SolicitudesViewModel(repo)

        vm.eliminarSolicitud(pendiente)

        vm.solicitudes.value shouldContainExactly listOf(pendiente)
        vm.error.value shouldBe "No se pudo eliminar la solicitud"
    }

    "una solicitud sin id no se intenta eliminar" {
        val repo = mockk<SolicitudRepository>()
        coEvery { repo.listar() } returns emptyList()
        val vm = SolicitudesViewModel(repo)

        vm.eliminarSolicitud(pendiente.copy(id = null))

        coVerify(exactly = 0) { repo.eliminar(any()) }
    }
})
