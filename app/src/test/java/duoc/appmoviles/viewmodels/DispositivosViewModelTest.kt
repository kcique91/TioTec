package duoc.appmoviles.viewmodels

import duoc.appmoviles.model.Dispositivo
import duoc.appmoviles.repository.DispositivoRepository
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

// Pruebas del DispositivosViewModel con el repositorio simulado (MockK)
@OptIn(ExperimentalCoroutinesApi::class)
class DispositivosViewModelTest : StringSpec({

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    val celular = Dispositivo(id = 1, nombre = "Celular de prueba", sistema = "Android 14")

    "al crearse carga los dispositivos del microservicio" {
        val repo = mockk<DispositivoRepository>()
        coEvery { repo.listar() } returns listOf(celular)

        val vm = DispositivosViewModel(repo)

        vm.dispositivos.value shouldContainExactly listOf(celular)
        vm.cargando.value shouldBe false
        vm.error.value.shouldBeNull()
    }

    "si el microservicio no responde muestra un error" {
        val repo = mockk<DispositivoRepository>()
        coEvery { repo.listar() } throws RuntimeException("sin conexión")

        val vm = DispositivosViewModel(repo)

        vm.dispositivos.value.shouldBeEmpty()
        vm.error.value.shouldNotBeNull()
    }

    "no agrega el dispositivo si faltan nombre y sistema" {
        val repo = mockk<DispositivoRepository>()
        coEvery { repo.listar() } returns emptyList()
        val vm = DispositivosViewModel(repo)

        vm.agregarDispositivo() shouldBe false
        vm.formDispositivo.value.errorNombre shouldBe "Campo obligatorio"
        vm.formDispositivo.value.errorSistema shouldBe "Campo obligatorio"
        coVerify(exactly = 0) { repo.crear(any(), any()) }
    }

    "escribir en los campos borra sus errores" {
        val repo = mockk<DispositivoRepository>()
        coEvery { repo.listar() } returns emptyList()
        val vm = DispositivosViewModel(repo)
        vm.agregarDispositivo()

        vm.onNombreDispositivoChange("Tablet")
        vm.onSistemaDispositivoChange("Android 13")

        vm.formDispositivo.value.errorNombre.shouldBeNull()
        vm.formDispositivo.value.errorSistema.shouldBeNull()
    }

    "agrega un dispositivo válido (POST), lo pone arriba y limpia el formulario" {
        val repo = mockk<DispositivoRepository>()
        val tablet = Dispositivo(id = 2, nombre = "Tablet", sistema = "Android 13")
        coEvery { repo.listar() } returns listOf(celular)
        coEvery { repo.crear("Tablet", "Android 13") } returns tablet
        val vm = DispositivosViewModel(repo)

        vm.onNombreDispositivoChange(" Tablet ")
        vm.onSistemaDispositivoChange("Android 13")

        vm.agregarDispositivo() shouldBe true
        vm.dispositivos.value shouldContainExactly listOf(tablet, celular)
        vm.formDispositivo.value.nombre shouldBe ""
        vm.formDispositivo.value.sistema shouldBe ""
    }

    "si falla el POST muestra un error" {
        val repo = mockk<DispositivoRepository>()
        coEvery { repo.listar() } returns emptyList()
        coEvery { repo.crear(any(), any()) } throws RuntimeException("caído")
        val vm = DispositivosViewModel(repo)
        vm.onNombreDispositivoChange("Tablet")
        vm.onSistemaDispositivoChange("Android 13")

        vm.agregarDispositivo()

        vm.error.value shouldBe "No se pudo registrar el dispositivo"
    }

    "eliminar (DELETE) quita el dispositivo de la lista" {
        val repo = mockk<DispositivoRepository>()
        coEvery { repo.listar() } returns listOf(celular)
        coEvery { repo.eliminar(1L) } returns true
        val vm = DispositivosViewModel(repo)

        vm.eliminarDispositivo(celular)

        vm.dispositivos.value.shouldBeEmpty()
    }

    "si el servidor no elimina el dispositivo, se mantiene y se avisa" {
        val repo = mockk<DispositivoRepository>()
        coEvery { repo.listar() } returns listOf(celular)
        coEvery { repo.eliminar(1L) } returns false
        val vm = DispositivosViewModel(repo)

        vm.eliminarDispositivo(celular)

        vm.dispositivos.value shouldContainExactly listOf(celular)
        vm.error.value shouldBe "No se pudo eliminar el dispositivo"
    }

    "si falla el DELETE muestra un error" {
        val repo = mockk<DispositivoRepository>()
        coEvery { repo.listar() } returns listOf(celular)
        coEvery { repo.eliminar(1L) } throws RuntimeException("caído")
        val vm = DispositivosViewModel(repo)

        vm.eliminarDispositivo(celular)

        vm.error.value shouldBe "No se pudo eliminar el dispositivo"
    }
})
