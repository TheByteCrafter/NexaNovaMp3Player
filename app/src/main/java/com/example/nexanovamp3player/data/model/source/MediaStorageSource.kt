package com.example.nexanovamp3player.data.model.source

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.nexanovamp3player.data.model.Song

class MediaStorageSource(private val context: Context){
    fun loadSongs():List<Song>{
        val songs=mutableListOf<Song>()
        val projection=arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID
        )
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection, MediaStore.Audio.Media.IS_MUSIC + "!=0", null,
            MediaStore.Audio.Media.TITLE)?.use{
                cursor->
                val idCol=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                while(cursor.moveToNext()){
                    val id=cursor.getLong(idCol)
                    val albumId=cursor.getLong(albumCol)
                    songs.add(
                        Song(
                            id=id,
                            title=cursor.getString(titleCol),
                            artist=cursor.getString(artistCol),
                            albumArt = ContentUris.withAppendedId(
                                Uri.parse("content://media/external/audio/albumart"),
                                albumId
                            ),
                            uri=ContentUris.withAppendedId(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                id
                            )
                        )
                    )

                }
        }

        return songs
    }
}