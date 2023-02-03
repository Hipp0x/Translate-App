package com.example.translateapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import com.example.translateapp.R
import com.example.translateapp.database.Dao
import com.example.translateapp.database.DicoApplication
import com.example.translateapp.database.entity.Mot
import java.lang.Integer.min
import kotlin.random.Random


class NotificationsService : LifecycleService() {

    private val CHANNEL_ID = "message urgent"
    private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private val pendingFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        PendingIntent.FLAG_IMMUTABLE
    else
        PendingIntent.FLAG_UPDATE_CURRENT

    private var data: List<Mot>? = null


    companion object {
        private val currentIdMotMap = mutableMapOf<Int, Mot>()
        lateinit var dao: Dao
        private lateinit var model: ServiceModel
    }

    override fun onCreate() {
        super.onCreate()

        model = ServiceModel(application)

        dao = (application as DicoApplication).database.MyDao()
        createNotificationChannel()

        model.loadMotsLearn.observe(this) {
            data = it
            val nbNotifsFromShared = getSharedPreferences("parametres", Context.MODE_PRIVATE).getInt("nbNotifs",10)
            if (data != null) {
                val nbNotifs = min(nbNotifsFromShared, data!!.size)
                val currentIdList = currentIdMotMap.keys
                for (i in 0 until nbNotifs) {
                    if (!currentIdList.contains(i)) {

                        var x = Random.nextInt(data!!.size)
                        var mot = data!![x]

                        val currentMotList = currentIdMotMap.values

                        while (currentMotList.contains(mot)) {
                            x = Random.nextInt(data!!.size)
                            mot = data!![x]
                        }

                        val message = "Traduis : ${mot.word}"
                        val monIntent = Intent(Intent.ACTION_VIEW)
                        monIntent.data = Uri.parse(mot.urlTransl)

                        val pendingIntent = PendingIntent.getActivity(
                            this, 0, monIntent,
                            pendingFlag
                        )

                        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_notif)
                            .setContentTitle("Do you remember well ?")
                            .setContentText(message)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)


                        with(NotificationManagerCompat.from(this)) {
                            notify(i, notification.build())
                        }
                        currentIdMotMap[i] = mot
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        model.loadAllMotNeedToBeLearn(true)

        return START_NOT_STICKY
    }

    /* les notifications doivent posséder un channel */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = "channel_name"
            val descriptionText = "channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }


    class NotifListenerService : NotificationListenerService() {

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            return super.onStartCommand(intent, flags, startId)
        }

        override fun onNotificationPosted(sbn: StatusBarNotification?) {
            super.onNotificationPosted(sbn)
        }

        override fun onNotificationRemoved(sbn: StatusBarNotification?) {
            super.onNotificationRemoved(sbn)

            if (sbn != null) {
                val id = sbn.id
                val mot: Mot? = currentIdMotMap[id]

                Log.i("EXAMEN", "Mot : ${mot?.word}")

                if (mot != null) {
                    mot.knowledge += 1
                    Log.i("EXAMEN", "Knowledge du mot: ${mot.knowledge}")
                    if (mot.knowledge == 3) {
                        mot.knowledge = 0
                        mot.toLearn = false
                    }
                    model.updateMot(mot)
                }
                currentIdMotMap.remove(id)
            }
        }
    }
}