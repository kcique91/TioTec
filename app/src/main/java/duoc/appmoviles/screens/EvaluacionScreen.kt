package duoc.appmoviles.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import duoc.appmoviles.navigation.Screen
import duoc.appmoviles.viewmodels.EvaluacionViewModel
import duoc.appmoviles.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluacionScreen(
    viewModel: MainViewModel,
    evaluacionViewModel: EvaluacionViewModel = viewModel()
) {
    // Estado de la evaluación: la pantalla se redibuja cuando cambia
    val estado by evaluacionViewModel.estado.collectAsState()
    val preguntas = evaluacionViewModel.preguntas

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Evaluación") },
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

        if (!estado.terminada) {
            // ----- Mientras responde: una pregunta a la vez -----
            val pregunta = preguntas[estado.indice]

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Pregunta ${estado.indice + 1} de ${preguntas.size}")
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (estado.indice + 1).toFloat() / preguntas.size },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = pregunta.categoria.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = pregunta.texto,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { evaluacionViewModel.responder(true) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Sí") }
                    OutlinedButton(
                        onClick = { evaluacionViewModel.responder(false) },
                        modifier = Modifier.weight(1f)
                    ) { Text("No") }
                }
            }

        } else {
            // ----- Al terminar: resultado y recomendaciones -----
            val brechas = evaluacionViewModel.brechas()

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Resultado de tu evaluación",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cumples ${evaluacionViewModel.puntaje()}% de las medidas revisadas",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (brechas.isEmpty()) {
                    Text("¡Excelente! No encontramos brechas de seguridad.")
                } else {
                    Text("Recomendaciones (${brechas.size}):")
                    Spacer(modifier = Modifier.height(8.dp))
                    // Lista que se puede deslizar, con una tarjeta por brecha
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(brechas) { pregunta ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = pregunta.categoria.titulo,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(pregunta.recomendacion)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.navigateTo(Screen.Checklist) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Ver mi checklist") }
                OutlinedButton(
                    onClick = { evaluacionViewModel.reiniciar() },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Repetir evaluación") }
            }
        }
    }
}

