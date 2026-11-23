package duoc.appmoviles.repository

import duoc.appmoviles.model.Post
import duoc.appmoviles.network.PostApiService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import retrofit2.Response

// Pruebas del PostRepository (Guía 14) con el ApiService simulado (Guía 15)
class PostRepositoryTest : StringSpec({

    val post = Post(userId = 1, id = 1, title = "Titulo 1", body = "Contenido 1")

    "getPosts() devuelve la lista de la API" {
        val api = mockk<PostApiService>()
        coEvery { api.getPosts() } returns listOf(post)

        PostRepository(api).getPosts() shouldContainExactly listOf(post)
    }

    "crearPost(), actualizarPost() y eliminarPost() llaman al endpoint correcto" {
        val api = mockk<PostApiService>()
        coEvery { api.crearPost(post) } returns post
        coEvery { api.actualizarPost(1, post) } returns post
        coEvery { api.eliminarPost(1) } returns Response.success(Unit)
        val repo = PostRepository(api)

        repo.crearPost(post) shouldBe post
        repo.actualizarPost(post) shouldBe post
        repo.eliminarPost(1) shouldBe true
        coVerify { api.actualizarPost(1, post) }
    }
})
