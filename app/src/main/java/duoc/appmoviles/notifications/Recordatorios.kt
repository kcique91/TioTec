package duoc.appmoviles.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import duoc.appmoviles.MainActivity
import java.util.concurrent.TimeUnit

object Recordatorios {

    private const val CANAL_ID = "recordatorios_tiotec"

    // Crea el canal de notificaciones (Android lo exige desde la versión 8)
    private fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Recordatorios preventivos",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            canal.description = "Recordatorios para revisar tu seguridad digital"
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    // Indica si la app tiene permiso para enviar notificaciones
    fun tienePermiso(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // antes de Android 13 no hace falta pedir permiso
        }
    }

    // Muestra una notificación; al tocarla se abre la app
    @SuppressLint("MissingPermission")
    fun mostrar(context: Context, titulo: String, texto: String) {
        if (!tienePermiso(context)) return
        crearCanal(context)

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1, notificacion)
    }

    // Programa un recordatorio que se mostrará dentro de unos minutos
    fun programar(context: Context, minutos: Long) {
        val trabajo = OneTimeWorkRequestBuilder<RecordatorioWorker>()
            .setInitialDelay(minutos, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueue(trabajo)
    }
}

// Tarea que WorkManager ejecuta cuando llega la hora del recordatorio
class RecordatorioWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        Recordatorios.mostrar(
            applicationContext,
            "TíoTec Preventivo",
            "Tienes acciones pendientes en tu checklist. ¡Revísalas hoy!"
        )
        return Result.success()
    }
}