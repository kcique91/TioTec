package duoc.appmoviles.repository

import duoc.appmoviles.model.VersionAndroid
import duoc.appmoviles.network.EndOfLifeApiService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.mockk

// Prueba del repositorio de la API externa con el ApiService simulado
class VersionAndroidRepositoryTest : StringSpec({

    "obtenerVersiones() devuelve las versiones de la API externa" {
        val versiones = listOf(
            VersionAndroid(cycle = "16", codename = "Baklava", releaseDate = "2025-06-10", eol = false),
            VersionAndroid(cycle = "12", codename = "Snow Cone", releaseDate = "2021-10-04", eol = "2025-03-03")
        )
        val api = mockk<EndOfLifeApiService>()
        coEvery { api.obtenerVersionesAndroid() } returns versiones

        VersionAndroidRepository(api).obtenerVersiones() shouldContainExactly versiones
    }
})
