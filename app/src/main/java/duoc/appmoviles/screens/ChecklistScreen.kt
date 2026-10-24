package duoc.appmoviles.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import duoc.appmoviles.navigation.Screen
import duoc.appmoviles.model.Prioridad
import duoc.appmoviles.notifications.Recordatorios
import duoc.appmoviles.viewmodels.ChecklistViewModel
import duoc.appmoviles.viewmodels.EvaluacionViewModel
import duoc.appmoviles.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    viewModel: MainViewModel,
    evaluacionViewModel: EvaluacionViewModel,
    checklistViewModel: ChecklistViewModel
) {
    val estadoEvaluacion by evaluacionViewModel.estado.collectAsState()
    val acciones by checklistViewModel.acciones.collectAsState()
    val context = LocalContext.current

    // Solo cuando la evaluación está terminada se actualizan las acciones.
    // Si no, se muestran las que ya estaban guardadas en la base de datos.
    LaunchedEffect(estadoEvaluacion.terminada, estadoEvaluacion.respuestas) {
        if (estadoEvaluacion.terminada) {
            checklistViewModel.cargarDesdeBrechas(evaluacionViewModel.brechas())
        }
    }

    // Ventana que le pide permiso al usuario para enviar notificaciones
    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            Recordatorios.programar(context, 1)
            Toast.makeText(context, "Recordatorio programado para dentro de 1 minuto", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Sin permiso no se pueden enviar recordatorios", Toast.LENGTH_SHORT).show()
        }
    }

    val completadas = acciones.count { it.completada }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checklist") },
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

        if (acciones.isEmpty()) {
            // ----- Sin acciones: aún no hay evaluación o no hay brechas -----
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No tienes acciones pendientes por ahora.")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Responde la evaluación para generar tu checklist.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.navigateTo(Screen.Evaluation) }) {
                    Text("Ir a la evaluación")
                }
            }

        } else {
            // ----- Con acciones: lista con casillas -----
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Progreso animado: la barra avanza suavemente en vez de saltar
                val progresoAnimado by animateFloatAsState(
                    targetValue = completadas.toFloat() / acciones.size,
                    animationSpec = tween(durationMillis = 500),
                    label = "progreso"
                )
                Text(
                    text = "Completadas: $completadas de ${acciones.size}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = {progresoAnimado },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Botón que programa el recordatorio
                OutlinedButton(
                    onClick = {
                        if (Recordatorios.tienePermiso(context)) {
                            // Ya tiene permiso: se programa directamente
                            Recordatorios.programar(context, 1)
                            Toast.makeText(context, "Recordatorio programado para dentro de 1 minuto", Toast.LENGTH_SHORT).show()
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            // Sin permiso: se le pide al usuario
                            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Recordarme en 1 minuto") }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(acciones) { accion ->
                        // Color animado: cambia suavemente cuando la acción se completa
                        val colorTarjeta by animateColorAsState(
                            targetValue = if (accion.completada)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            animationSpec = tween(durationMillis = 500),
                            label = "colorTarjeta"
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = colorTarjeta)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = accion.completada,
                                    onCheckedChange = { checklistViewModel.alternar(accion.id) }
                                )
                                Column(modifier = Modifier.padding(start = 8.dp)) {
                                    Text(
                                        text = accion.categoria.titulo,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = accion.texto,
                                        textDecoration = if (accion.completada)
                                            TextDecoration.LineThrough else TextDecoration.None
                                    )
                                    Text(
                                        text = if (accion.completada) "Completada" else "Pendiente",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    // Prioridad: la Alta pendiente se destaca en color de alerta
                                    Text(
                                        text = "Prioridad: ${accion.prioridad.titulo}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (accion.prioridad == Prioridad.ALTA && !accion.completada)
                                            MaterialTheme.colorScheme.error
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}