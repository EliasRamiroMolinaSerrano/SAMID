package com.example.samid

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        // Asegúrate de que el contexto no sea nulo
        context?.let { ctx ->
            Log.d("AlarmReceiver", "onReceive: Alarma activada") // Log para ver si se activa la alarma

            // Crear el Intent para abrir DestinationActivity
            val i = Intent(ctx, DestinationActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            // Crear el PendingIntent
            val pendingIntent = PendingIntent.getActivity(
                ctx,
                0,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Construir la notificación
            val builder = NotificationCompat.Builder(ctx, "Samid")
                .setSmallIcon(R.drawable.ic_launcher_background) // Cambia a un ícono adecuado
                .setContentTitle("SAMID Alarm Manager")
                .setContentText("Dar medicamento a Manuel")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)

            // Mostrar la notificación
            with(NotificationManagerCompat.from(ctx)) {
                notify(123, builder.build())
            }
        }
    }
}
