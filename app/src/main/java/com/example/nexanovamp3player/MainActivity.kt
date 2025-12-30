package com.example.nexanovamp3player

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.nexanovamp3player.ui.screen.HomeScreen
import com.example.nexanovamp3player.ui.theme.NexaNovaMp3PlayerTheme

class MainActivity : ComponentActivity() {

    val permissionsToRequest = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //request for permissions for access to storage and the rest of permissions in manifest file

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_AUDIO)
            permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_IMAGES)
            permissionsToRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }else{
            permissionsToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        requestNeededPermissions()
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


    private fun requestNeededPermissions(){
        val notGranted=permissionsToRequest.filter{
            ContextCompat.checkSelfPermission(this,it) != PackageManager.PERMISSION_GRANTED
        }
        if(notGranted.isNotEmpty()){
            ActivityCompat.requestPermissions(this,notGranted.toTypedArray(),0)

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissions.forEachIndexed { index, permission ->

            if(grantResults[index] ==PackageManager.PERMISSION_GRANTED){
                Log.d("Permissions","$permission granted")

            }else{
                Log.d("Permissions","$permission denied")

            }
        }

    }
}
