package com.example.nexanovamp3player.data.model.repository

import android.content.Context
import com.example.nexanovamp3player.data.model.Song
import com.example.nexanovamp3player.data.model.source.MediaStorageSource

class MusicRepository (context: Context){
    private val source= MediaStorageSource(context)

    fun getSongs(): List<Song> =source.loadSongs()
}