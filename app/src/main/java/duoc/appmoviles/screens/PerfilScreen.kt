package duoc.appmoviles.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import duoc.appmoviles.components.ImagenInteligente
import duoc.appmoviles.viewmodels.MainViewModel
import duoc.appmoviles.viewmodels.PerfilViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: MainViewModel,
    perfilViewModel: PerfilViewModel = viewModel()
) {
    // Datos guardados en la base de datos (se actualizan solos)
    val perfil by perfilViewModel.perfil.collectAsState()
    val dispositivos by perfilViewModel.dispositivos.collectAsState()

    // Lo que el usuario escribe en el formulario del perfil.
    // Se rellena con el perfil guardado cuando existe.
    var nombre by remember(perfil) { mutableStateOf(perfil?.nombre ?: "") }
    var nivel by remember(perfil) { mutableStateOf(perfil?.nivel ?: "Básico") }

    // Lo que el usuario escribe en el formulario de dispositivos
    var nombreDispositivo by remember { mutableStateOf("") }
    var sistemaDispositivo by remember { mutableStateOf("") }

    val niveles = listOf("Básico", "Intermedio", "Avanzado")

    // ----- Cámara y galería -----
    val context = LocalContext.current
    val fotoUri by perfilViewModel.fotoUri.collectAsState()

    // Abre la cámara del sistema; el resultado (true/false) va al ViewModel
    val camaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        perfilViewModel.resultadoCamara(exito)
    }

    // Pide el permiso de cámara; si lo concede, abre la cámara
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            camaraLauncher.launch(perfilViewModel.prepararFotoCamara())
        } else {
            Toast.makeText(context, "Sin permiso de cámara no se puede tomar la foto", Toast.LENGTH_SHORT).show()
        }
    }

    // Abre la galería para elegir una imagen; la Uri elegida va al ViewModel
    val galeriaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        perfilViewModel.resultadoGaleria(uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {

            // ----- Foto del perfil (cámara y galería) -----
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ImagenInteligente(uri = fotoUri)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            // Si ya tiene permiso abre la cámara; si no, lo pide
                            val tienePermiso = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                            if (tienePermiso) {
                                camaraLauncher.launch(perfilViewModel.prepararFotoCamara())
                            } else {
                                permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }) { Text("Cámara") }
                        OutlinedButton(onClick = { galeriaLauncher.launch("image/*") }) {
                            Text("Galería")
                        }
                    }
                    if (fotoUri != null) {
                        TextButton(onClick = { perfilViewModel.quitarFoto() }) {
                            Text("Quitar foto")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                }
            }

            // ----- Formulario del perfil ficticio -----
            item {
                Text(
                    text = "Mi perfil (ficticio)",
                    style = MaterialTheme.typography.titleLarge
                )
                Text("Usa datos inventados. No escribas datos personales reales.")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del perfil") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Nivel de experiencia digital")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    niveles.forEach { opcion ->
                        FilterChip(
                            selected = nivel == opcion,
                            onClick = { nivel = opcion },
                            label = { Text(opcion) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { perfilViewModel.guardarPerfil(nombre, nivel) },
                    enabled = nombre.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Guardar perfil") }
                if (perfil != null) {
                    Text("Perfil guardado: ${perfil?.nombre} (${perfil?.nivel})")
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
            }

            // ----- Formulario de dispositivos de prueba -----
            item {
                Text(
                    text = "Dispositivos de prueba",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = nombreDispositivo,
                    onValueChange = { nombreDispositivo = it },
                    label = { Text("Nombre del dispositivo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = sistemaDispositivo,
                    onValueChange = { sistemaDispositivo = it },
                    label = { Text("Sistema operativo (ej: Android 14)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        perfilViewModel.agregarDispositivo(nombreDispositivo, sistemaDispositivo)
                        nombreDispositivo = ""
                        sistemaDispositivo = ""
                    },
                    enabled = nombreDispositivo.isNotBlank() && sistemaDispositivo.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Agregar dispositivo") }
            }

            // ----- Lista de dispositivos registrados -----
            if (dispositivos.isEmpty()) {
                item { Text("Aún no has registrado dispositivos.") }
            } else {
                items(dispositivos) { dispositivo ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = dispositivo.nombre,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(dispositivo.sistema)
                            }
                            TextButton(
                                onClick = { perfilViewModel.eliminarDispositivo(dispositivo.id) }
                            ) { Text("Eliminar") }
                        }
                    }
                }
            }
        }
    }
}