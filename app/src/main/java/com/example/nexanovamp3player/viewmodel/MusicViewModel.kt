package com.example.nexanovamp3player.viewmodel

import android.app.Application
import android.content.ComponentName
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.nexanovamp3player.data.model.Song
import com.example.nexanovamp3player.data.model.repository.MusicRepository
import com.example.nexanovamp3player.player.MusicService
import com.google.common.util.concurrent.MoreExecutors

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MusicRepository(application)

    // Media3 Controller that connects to our MusicService
    private var controller: MediaController? = null

    // UI States
    var songs by mutableStateOf(emptyList<Song>())
    var isPlaying by mutableStateOf(false)
    var currentSong by mutableStateOf<Song?>(null)
    var currentPosition by mutableLongStateOf(0L)
    var duration by mutableLongStateOf(0L)

    // Search State
    var searchQuery by mutableStateOf("")
        private set

    // Playback Modes
    var isShuffleEnabled by mutableStateOf(false)
    var repeatMode by mutableStateOf(RepeatMode.NONE)
    enum class RepeatMode { NONE, ALL, ONE }

    val filteredSongs: List<Song>
        get() = if (searchQuery.isEmpty()) {
            songs
        } else {
            songs.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.artist.contains(searchQuery, ignoreCase = true)
            }
        }

    init {
        songs = repository.getSongs()
        setupMediaController(application)
    }

    private fun setupMediaController(application: Application) {
        val sessionToken = SessionToken(
            application,
            ComponentName(application, MusicService::class.java)
        )

        val controllerFuture = MediaController.Builder(application, sessionToken).buildAsync()

        controllerFuture.addListener({
            controller = controllerFuture.get()

            // Sync initial state from the service's player
            controller?.let { player ->
                //  sync the song name immediately on connect
                updateCurrentSongFromController()
                isPlaying = player.isPlaying
                isShuffleEnabled = player.shuffleModeEnabled

                // Add a listener to handle automatic track changes and state updates
                player.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(playing: Boolean) {
                        isPlaying = playing
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        updateCurrentSongFromController()
                    }
                    //  Update duration when the track changes
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            duration = player.duration
                        }
                    }
                })
            }
        }, MoreExecutors.directExecutor())
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
    }

    fun play(song: Song) {

        val mediaItems = songs.map { currentSong ->
            MediaItem.Builder()
                .setMediaId(currentSong.id.toString())
                .setUri(currentSong.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(currentSong.title)
                        .setArtist(currentSong.artist)
                        .build()
                )
                .build()
        }


        val index = songs.indexOf(song)

        controller?.let { player ->

            player.setMediaItems(mediaItems, index, 0L)
            player.prepare()
            player.play()
        }
    }


    fun togglePlayPause() {
        controller?.let { player ->
            if (player.isPlaying) player.pause() else player.play()
        }
    }

    fun next() {
        controller?.seekToNext()
    }

    fun previous() {
        controller?.let { player ->
            if (player.currentPosition > 5000) {
                player.seekTo(0)
            } else {
                player.seekToPrevious()
            }
        }
    }
    fun seekTo(position: Long) {
        controller?.seekTo(position)
        currentPosition = position
    }

    fun toggleShuffle() {
        isShuffleEnabled = !isShuffleEnabled
        controller?.shuffleModeEnabled = isShuffleEnabled
    }

    fun toggleRepeatMode() {
        repeatMode = when (repeatMode) {
            RepeatMode.NONE -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.NONE
        }

        controller?.repeatMode = when (repeatMode) {
            RepeatMode.NONE -> Player.REPEAT_MODE_OFF
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
        }
    }

    fun updateProgress() {
        controller?.let { player ->
            currentPosition = player.currentPosition
            duration = player.duration.coerceAtLeast(0L)
        }
    }

    private fun updateCurrentSongFromController() {
        val index = controller?.currentMediaItemIndex ?: -1
        if (index in songs.indices) {
            currentSong = songs[index]
        }
    }


    override fun onCleared() {
        super.onCleared()
        controller?.release()
    }
}