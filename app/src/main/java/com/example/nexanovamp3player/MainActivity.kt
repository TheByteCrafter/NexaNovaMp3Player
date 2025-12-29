package com.example.nexanovamp3player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.nexanovamp3player.ui.screen.HomeScreen
import com.example.nexanovamp3player.ui.theme.NexaNovaMp3PlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //request for permissions for access to storage and the rest of permissions in manifest file

        requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),0)
        //rewuest
        requestPermissions(arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO),0)

        enableEdgeToEdge()
        setContent {
            NexaNovaMp3PlayerTheme (darkTheme = true){
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(modifier=Modifier.padding(innerPadding))
                }
            }

        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NexaNovaMp3PlayerTheme {
        Greeting("Android")
    }
}