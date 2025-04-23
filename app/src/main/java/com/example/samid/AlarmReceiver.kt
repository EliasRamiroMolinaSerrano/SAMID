package com.example.samid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.Notification
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val nombre = intent.getStringExtra("nombre") ?: "Paciente"
        val medicamento = intent.getStringExtra("medicamento") ?: "medicina"
        val hora = intent.getStringExtra("hora") ?: ""

        Log.d("AlarmReceiver", "✅ Notificación recibida para $nombre a las $hora")

        val canalId = "SamidAlarmChannel"
        val canalNombre = "Canal de alarmas Samid"

        // Crear canal si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                canalId,
                canalNombre,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios importantes de alarmas médicas"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        // Crear notificación con estilo llamativo
        val builder = NotificationCompat.Builder(context, canalId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // usa tu icono si tienes uno
            .setContentTitle("⏰ Recordatorio para $nombre")
            .setContentText("Toma tu medicamento: $medicamento a las $hora")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("¡Es hora de tomar tu medicamento!\n\n$nombre, recuerda tomar $medicamento a las $hora.")
            )
            .setColor(Color.parseColor("#4CAF50")) // verde vibrante
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_alarm)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(0, 300, 200, 300))

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}
