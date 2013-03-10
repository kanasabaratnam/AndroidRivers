package com.silverkeytech.android_rivers.meta_weblog

import android.app.Activity
import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Message
import android.os.RemoteException
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.RemoteViews
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.silverkeytech.android_rivers.db.savePodcastToDb
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.net.URL
import java.util.Random
import com.silverkeytech.android_rivers.MainWithFragmentsActivity
import com.silverkeytech.android_rivers.Params
import com.silverkeytech.android_rivers.with
import com.silverkeytech.android_rivers.R
import java.util.HashMap
import com.silverkeytech.android_rivers.isNullOrEmpty

public class BlogPostService(): IntentService("DownloadService"){
    class object{
        public val TAG: String = javaClass<BlogPostService>().getSimpleName()
    }

    var config : HashMap<String,String>? = null
    var post : HashMap<String, String>? = null

    fun prepareNotification(title: String): Notification {
        val notificationIntent = Intent(Intent.ACTION_MAIN)
        notificationIntent.setClass(getApplicationContext(), javaClass<MainWithFragmentsActivity>())
        //notificationIntent.putExtra(Params.DOWNLOAD_LOCATION_PATH, filePath)

        val contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val notification = NotificationCompat.Builder(this)
                .setTicker("Posting $title")
        ?.setWhen(System.currentTimeMillis())
        ?.setContentIntent(contentIntent)
        ?.build()


        notification!!.icon = android.R.drawable.stat_sys_download

        notification.contentView = RemoteViews(getApplicationContext()!!.getPackageName(), R.layout.notification_download_progress).with {
            this.setImageViewResource(R.id.notification_download_progress_status_icon, android.R.drawable.stat_sys_download_done)
            this.setProgressBar(R.id.notification_download_progress_status_progress, 100, 0, false)
            this.setTextViewText(R.id.notification_download_progress_status_text, "Posting")
        }

        return notification
    }

    protected override fun onHandleIntent(p0: Intent?) {
        config = p0!!.getSerializableExtra(Params.BLOG_CONFIGURATION)!! as HashMap<String, String>
        post = p0!!.getSerializableExtra(Params.BLOG_PAYLOAD)!!  as HashMap<String, String>

        Log.d(TAG, " Server is ${config?.get(Params.BLOG_SERVER)}")

        val server = config!!.get(Params.BLOG_SERVER)!!
        val username = config!!.get(Params.BLOG_USERNAME)!!
        val password = config!!.get(Params.BLOG_PASSWORD)!!

        val postContent = post!!.get(Params.POST_CONTENT)!!
        val postLink = post!!.get(Params.POST_LINK)


        val blg = Blog(null, server, username, password)

        if (postLink.isNullOrEmpty()){
            val content = postContent

            val pst = statusPost(content)
            blg.newPost(pst)

        }
        else {
            val pst = linkPost(postContent, postLink!!)
            blg.newPost(pst)
        }


        //val notification = prepareNotification("Posting blog")

    }

    public override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "OnStartCommand")

        return super<IntentService>.onStartCommand(intent, flags, startId)
    }

    public override fun onCreate() {
        super<IntentService>.onCreate()
        Log.d(TAG, "Service created")
    }

    public override fun onStart(intent: Intent?, startId: Int) {
        super<IntentService>.onStart(intent, startId)
        Log.d(TAG, "Service created")
    }

    public override fun onDestroy() {
        super<IntentService>.onDestroy()
        Log.d(TAG, "Service created")
    }
}