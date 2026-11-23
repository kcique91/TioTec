package duoc.appmoviles.viewmodels

import duoc.appmoviles.model.Post
import duoc.appmoviles.repository.PostRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
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

// Pruebas del PostViewModel (Guía 14) con el repositorio simulado (Guía 15)
@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest : StringSpec({

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    val post1 = Post(userId = 1, id = 1, title = "Titulo 1", body = "Contenido 1")
    val post2 = Post(userId = 2, id = 2, title = "Titulo 2", body = "Contenido 2")

    "postList debe contener los datos esperados después de fetchPosts()" {
        val repo = mockk<PostRepository>()
        coEvery { repo.getPosts() } returns listOf(post1, post2)

        val vm = PostViewModel(repo)

        vm.postList.value shouldContainExactly listOf(post1, post2)
        vm.error.value.shouldBeNull()
    }

    "si la API falla se muestra un error" {
        val repo = mockk<PostRepository>()
        coEvery { repo.getPosts() } throws RuntimeException("sin internet")

        val vm = PostViewModel(repo)

        vm.postList.value.shouldBeEmpty()
        vm.error.value.shouldNotBeNull()
    }

    "crearPost() agrega la publicación arriba de la lista" {
        val repo = mockk<PostRepository>()
        val nuevo = Post(userId = 1, id = 101, title = "Nuevo", body = "Texto")
        coEvery { repo.getPosts() } returns listOf(post1)
        coEvery { repo.crearPost(any()) } returns nuevo
        val vm = PostViewModel(repo)

        vm.crearPost("Nuevo", "Texto")

        vm.postList.value shouldContainExactly listOf(nuevo, post1)
        vm.mensaje.value.shouldNotBeNull()
        vm.mensajeMostrado()
        vm.mensaje.value.shouldBeNull()
    }

    "actualizarPost() reemplaza la publicación y eliminarPost() la quita" {
        val repo = mockk<PostRepository>()
        coEvery { repo.getPosts() } returns listOf(post1, post2)
        coEvery { repo.actualizarPost(any()) } answers { firstArg() }
        coEvery { repo.eliminarPost(2) } returns true
        val vm = PostViewModel(repo)

        vm.actualizarPost(post1, "Cambiado", "Nuevo contenido")
        vm.eliminarPost(post2)

        vm.postList.value.first().title shouldBe "Cambiado"
        vm.postList.value.size shouldBe 1
    }
})
