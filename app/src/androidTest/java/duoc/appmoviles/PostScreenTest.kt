package duoc.appmoviles

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import duoc.appmoviles.model.Post
import duoc.appmoviles.repository.PostRepository
import duoc.appmoviles.screens.PostScreen
import duoc.appmoviles.viewmodels.PostViewModel
import org.junit.Rule
import org.junit.Test

// Repositorio falso: devuelve posts de prueba sin usar internet (Guía 15)
class FakePostRepository(private val posts: List<Post>) : PostRepository() {
    override suspend fun getPosts(): List<Post> = posts
}

class PostScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun el_titulo_de_post_debe_aparecer_en_pantalla() {
        // Datos que el ViewModel entregaría
        val fakePosts = listOf(
            Post(userId = 1, id = 1, title = "Titulo 1", body = "Contenido 1"),
            Post(userId = 2, id = 2, title = "Titulo 2", body = "Contenido 2")
        )

        // ViewModel real, pero con el repositorio falso
        val fakeViewModel = PostViewModel(FakePostRepository(fakePosts))

        // Dibujamos la pantalla con ese ViewModel
        composeRule.setContent {
            PostScreen(viewModel = fakeViewModel)
        }

        // Esperamos a que la lista aparezca
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Título: Titulo 1")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Validamos que los títulos se muestran en la UI
        composeRule.onNodeWithText("Título: Titulo 1").assertIsDisplayed()
        composeRule.onNodeWithText("Título: Titulo 2").assertIsDisplayed()
    }
}