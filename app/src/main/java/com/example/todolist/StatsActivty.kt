package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todolist.data.AppDatabase
import kotlinx.coroutines.*

class StatsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = AppDatabase.getInstance(this).taskDao()

        setContent {
            var doneCount by remember { mutableStateOf(0) }
            var notDoneCount by remember { mutableStateOf(0) }

            LaunchedEffect(Unit) {
                val tasks = dao.getAll()
                doneCount = tasks.count { it.isDone }
                notDoneCount = tasks.count { !it.isDone }
            }

            Surface(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Statistik", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tugas Selesai: $doneCount")
                    Text("Tugas Belum Selesai: $notDoneCount")
                }
            }
        }
    }
}
