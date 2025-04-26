package br.edu.utfpr.kmmcrudexemple.shared.data.respository

import br.edu.utfpr.kmmcrudexemple.shared.data.Task

interface TaskRepository {
    suspend fun addTask(task: Task)
    suspend fun getAllTasks(): List<Task>
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: Long)
}