package com.example.piano

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.piano.ui.theme.PianoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PianoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    AllComponents()
                }
            }
        }
    }
}

@Composable
fun AllComponents() {
    Column(modifier = Modifier.fillMaxSize()) {
        var selectedOctave by remember { mutableIntStateOf(4) }
        OctaveSelector(selectedOctave) { octave ->
            selectedOctave = octave
        }
        Mix()
    }
}

@Composable
fun OctaveSelector(selectedOctave: Int, onOctaveChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (octave in 3..5) {
            Button(
                onClick = { onOctaveChange(octave) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (octave == selectedOctave) Color.Gray else Color.LightGray
                )
            ) {
                Text(text = "Octava $octave", color = Color.Black)
            }
        }
    }
}

@Composable
fun Mix() {
    val context = LocalContext.current
    val soundPool: SoundPool
    val soundIds = remember { IntArray(7) }
    val notes = listOf(
        "Do" to R.raw.doo,
        "Re" to R.raw.re,
        "Mi" to R.raw.mi,
        "Fa" to R.raw.fa,
        "Sol" to R.raw.sol,
        "La" to R.raw.la,
        "Si" to R.raw.si
    )

    val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    soundPool = remember { SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes).build() }

    LaunchedEffect(Unit) {
        notes.forEachIndexed { index, pair ->
            soundIds[index] = soundPool.load(context, pair.second, 1)
        }
    }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        notes.forEachIndexed { index, (note, _) ->
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
                    .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                    .clickable {
                        playNote(soundPool, soundIds[index])
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = note,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private fun playNote(soundPool: SoundPool, soundId: Int) {
    soundPool.autoPause()
    soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
}
