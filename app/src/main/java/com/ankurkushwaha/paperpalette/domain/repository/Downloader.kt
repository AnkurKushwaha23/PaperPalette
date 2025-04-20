package com.ankurkushwaha.paperpalette.domain.repository

interface Downloader {
    fun downloadFile(url:String,fileName:String?)
}