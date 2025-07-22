package com.example.todolist.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.AppDatabase
import com.example.todolist.data.TaskEntity
import kotlinx.coroutines.launch

class TaskViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getInstance(app).taskDao()

    private val _tasks = mutableStateListOf<TaskEntity>()
    val tasks: List<TaskEntity> get() = _tasks

    fun loadTasks() {
        viewModelScope.launch {
            _tasks.clear()
            _tasks.addAll(dao.getAll())
        }
    }

    fun addTask(title: String) {
        viewModelScope.launch {
            dao.insert(TaskEntity(title = title))
            loadTasks()
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            dao.update(task.copy(isDone = !task.isDone))
            loadTasks()
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            dao.delete(task)
            loadTasks()
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            dao.update(task)
            loadTasks()
        }
    }

}
