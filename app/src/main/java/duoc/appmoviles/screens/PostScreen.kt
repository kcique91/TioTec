package duoc.appmoviles.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import duoc.appmoviles.model.Post
import duoc.appmoviles.viewmodels.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    viewModel: PostViewModel,
    onVolver: () -> Unit = {}
) {
    // Observamos el estado del ViewModel: la pantalla se redibuja cuando cambia
    val posts by viewModel.postList.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    // Estado de la ventana de crear o editar
    var mostrarDialogo by remember { mutableStateOf(false) }
    var postEditando by remember { mutableStateOf<Post?>(null) } // null = creando uno nuevo

    // Para mostrar mensajes cortos abajo
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicaciones") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            // Botón "+" para crear una publicación (POST)
            FloatingActionButton(onClick = {
                postEditando = null
                mostrarDialogo = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Crear publicación")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                // 1) Esperando la respuesta de la API
                cargando -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                // 2) La API falló: mensaje y botón para reintentar
                error != null -> Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = error ?: "")
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { viewModel.fetchPosts() }) {
                        Text("Reintentar")
                    }
                }

                // 3) Todo bien: lista de publicaciones
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(posts) { post ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Título: ${post.title}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = post.body,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    // PUT: abre la ventana con los datos de este post
                                    TextButton(onClick = {
                                        postEditando = post
                                        mostrarDialogo = true
                                    }) { Text("Editar") }
                                    // DELETE: elimina este post
                                    TextButton(onClick = { viewModel.eliminarPost(post) }) {
                                        Text("Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Ventana para crear (POST) o editar (PUT)
    if (mostrarDialogo) {
        FormularioPostDialog(
            esEdicion = postEditando != null,
            tituloInicial = postEditando?.title ?: "",
            contenidoInicial = postEditando?.body ?: "",
            onGuardar = { titulo, contenido ->
                val editando = postEditando
                if (editando == null) {
                    viewModel.crearPost(titulo, contenido)
                } else {
                    viewModel.actualizarPost(editando, titulo, contenido)
                }
                mostrarDialogo = false
            },
            onCancelar = { mostrarDialogo = false }
        )
    }
}

// Ventana con dos campos de texto; sirve para crear y para editar
@Composable
private fun FormularioPostDialog(
    esEdicion: Boolean,
    tituloInicial: String,
    contenidoInicial: String,
    onGuardar: (String, String) -> Unit,
    onCancelar: () -> Unit
) {
    var titulo by remember { mutableStateOf(tituloInicial) }
    var contenido by remember { mutableStateOf(contenidoInicial) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = {
            Text(if (esEdicion) "Editar publicación (PUT)" else "Nueva publicación (POST)")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = contenido,
                    onValueChange = { contenido = it },
                    label = { Text("Contenido") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            // El botón se activa solo si los dos campos tienen texto
            Button(
                onClick = { onGuardar(titulo, contenido) },
                enabled = titulo.isNotBlank() && contenido.isNotBlank()
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}