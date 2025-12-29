package com.example.nexanovamp3player.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.nexanovamp3player.viewmodel.MusicViewModel

@Composable
fun SongList(viewModel: MusicViewModel, modifier: Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(
            items = viewModel.filteredSongs,
            key = { it.id }
        ) { song ->
            // Check if this song is the one currently active in the player
            val isSelected = viewModel.currentSong?.id == song.id

            SongItem(
                song = song,
                isSelected = isSelected,
                isPlaying = viewModel.isPlaying // Only animates if music is actually playing
            ) {
                viewModel.play(song)
            }
        }
    }
}