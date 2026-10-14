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
import androidx.compose.ui.unit.dp
import duoc.appmoviles.navigation.Screen
import duoc.appmoviles.viewmodels.EvaluacionViewModel
import duoc.appmoviles.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    viewModel: MainViewModel,
    evaluacionViewModel: EvaluacionViewModel
) {
    // Evaluaciones guardadas en la base de datos (se actualiza sola)
    val historial by evaluacionViewModel.historial.collectAsState()

    // Formato para mostrar la fecha, por ejemplo 14-10-2026 18:30
    val formatoFecha = remember { SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial") },
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

        if (historial.isEmpty()) {
            // ----- Sin evaluaciones guardadas -----
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Aún no tienes evaluaciones guardadas.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.navigateTo(Screen.Evaluation) }) {
                    Text("Hacer una evaluación")
                }
            }

        } else {
            // ----- Lista de evaluaciones, de la más nueva a la más antigua -----
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(historial) { evaluacion ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = formatoFecha.format(Date(evaluacion.fecha)),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Puntaje: ${evaluacion.puntaje}%")
                            Text("Brechas detectadas: ${evaluacion.brechas}")
                        }
                    }
                }
            }
        }
    }
}
