package com.example.todolist

import androidx.compose.ui.Alignment

import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle

import androidx.compose.ui.unit.dp
import com.example.todolist.data.TaskEntity
import com.example.todolist.viewmodel.TaskViewModel
import com.example.todolist.ui.theme.TodolistTheme // Ganti jika kamu pakai tema sendiri

class MainActivity : ComponentActivity() {
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodolistTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TodoListScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodoListScreen(viewModel: TaskViewModel) {
    val context = LocalContext.current
    var newTaskTitle by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var taskBeingEdited by remember { mutableStateOf<TaskEntity?>(null) }
    var editedText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadTasks()
    }

    // Dialog untuk edit task
    if (isEditing && taskBeingEdited != null) {
        AlertDialog(
            onDismissRequest = { isEditing = false },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateTask(
                        taskBeingEdited!!.copy(title = editedText)
                    )
                    isEditing = false
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                Button(onClick = { isEditing = false }) {
                    Text("Batal")
                }
            },
            title = { Text("Edit Tugas") },
            text = {
                OutlinedTextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    label = { Text("Judul Tugas") }
                )
            }
        )
    }

    // Semua UI utama dalam Column
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Tombol Lihat Statistik
        Button(
            onClick = {
                val intent = Intent(context, StatsActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Lihat Statistik")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input tambah tugas
        OutlinedTextField(
            value = newTaskTitle,
            onValueChange = { newTaskTitle = it },
            label = { Text("Tambahkan tugas") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (newTaskTitle.isNotBlank()) {
                    viewModel.addTask(newTaskTitle)
                    newTaskTitle = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tambah")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Daftar tugas
        LazyColumn {
            items(viewModel.tasks) { task ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .combinedClickable(
                            onClick = {
                                val intent = Intent(context, TaskDetailActivity::class.java)
                                intent.putExtra("task_title", task.title)
                                intent.putExtra("task_status", task.isDone)
                                context.startActivity(intent)
                            },
                            onLongClick = {
                                isEditing = true
                                taskBeingEdited = task
                                editedText = task.title
                            }
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Checkbox(
                            checked = task.isDone,
                            onCheckedChange = { viewModel.toggleTask(task) }
                        )
                        Text(task.title)
                    }
                    Button(onClick = { viewModel.deleteTask(task) }) {
                        Text("Hapus")
                    }
                }
            }
        }
    }
}

