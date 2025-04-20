package com.ankurkushwaha.paperpalette.data.repository

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.ankurkushwaha.paperpalette.domain.repository.Downloader
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AndroidImageDownloaderImpl(
    context: Context
) : Downloader {

    private val downloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    override fun downloadFile(url: String, fileName: String?) {
        try {
            val title = fileName ?: "PAPERPALETTE_${getCurrentDateForFile()}"

            val request = DownloadManager.Request(url.toUri())
                .setMimeType("image/*")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(title)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_PICTURES,
                    File.separator + title + ".jpg"
                )
            downloadManager.enqueue(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCurrentDateForFile():String{
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        return dateFormat.format(Date())
    }
}