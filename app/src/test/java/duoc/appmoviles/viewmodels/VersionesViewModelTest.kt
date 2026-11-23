package duoc.appmoviles.viewmodels

import duoc.appmoviles.model.VersionAndroid
import duoc.appmoviles.repository.VersionAndroidRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

// Pruebas del VersionesViewModel (API externa) con el repositorio simulado
// y una fecha de "hoy" fija, para que el resultado no dependa del día en que se corre
@OptIn(ExperimentalCoroutinesApi::class)
class VersionesViewModelTest : StringSpec({

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    val hoy = { "2026-11-23" }
    val android17 = VersionAndroid(cycle = "17", codename = "Cinnamon Bun", releaseDate = "2026-06-16", eol = false)
    val android13 = VersionAndroid(cycle = "13", codename = "Tiramisu", releaseDate = "2022-08-15", eol = "2026-03-02")
    val android11 = VersionAndroid(cycle = "11", codename = "Red Velvet Cake", releaseDate = "2020-09-08", eol = "2024-02-05")

    "al crearse carga las versiones desde la API externa" {
        val repo = mockk<VersionAndroidRepository>()
        coEvery { repo.obtenerVersiones() } returns listOf(android17, android13, android11)

        val vm = VersionesViewModel(repo, hoy)

        vm.versiones.value shouldHaveSize 3
        vm.cargando.value shouldBe false
        vm.error.value.shouldBeNull()
    }

    "si la API falla muestra un error" {
        val repo = mockk<VersionAndroidRepository>()
        coEvery { repo.obtenerVersiones() } throws RuntimeException("sin internet")

        val vm = VersionesViewModel(repo, hoy)

        vm.versiones.value.shouldBeEmpty()
        vm.error.value.shouldNotBeNull()
    }

    "cuenta solo las versiones que siguen con soporte" {
        val repo = mockk<VersionAndroidRepository>()
        coEvery { repo.obtenerVersiones() } returns listOf(android17, android13, android11)

        val vm = VersionesViewModel(repo, hoy)

        vm.cantidadConSoporte() shouldBe 1
        vm.tieneSoporte(android17) shouldBe true
        vm.tieneSoporte(android13) shouldBe false
    }

    "describe el estado de soporte de cada versión" {
        val repo = mockk<VersionAndroidRepository>()
        coEvery { repo.obtenerVersiones() } returns emptyList()

        val vm = VersionesViewModel(repo, hoy)

        vm.descripcionSoporte(android17) shouldBe "Con soporte de seguridad"
        vm.descripcionSoporte(android11) shouldBe "Sin soporte desde 2024-02-05"
    }
})
