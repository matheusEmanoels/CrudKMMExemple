package br.edu.utfpr.kmmcrudexemple.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import br.edu.utfpr.kmmcrudexemple.shared.data.Task
import br.edu.utfpr.kmmcrudexemple.shared.data.respository.SQLiteTaskRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                TaskScreen()
            }
        }
    }
}

@Composable
fun TaskScreen() {
    val context = LocalContext.current
    val taskRepository = remember { SQLiteTaskRepository(context) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var isEditing by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        tasks = taskRepository.getAllTasks()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isEditing) "Editar Tarefa" else "Adicionar Tarefa",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    scope.launch {
                        if (isEditing && taskToEdit != null) {
                            val updatedTask = taskToEdit!!.copy(title = title, description = description)
                            taskRepository.updateTask(updatedTask)
                            isEditing = false
                            taskToEdit = null
                        } else {
                            val newTask = Task(title = title, description = description)
                            taskRepository.addTask(newTask)
                        }
                        title = ""
                        description = ""
                        tasks = taskRepository.getAllTasks()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Salvar Alterações" else "Adicionar Tarefa")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Tarefas cadastradas", style = MaterialTheme.typography.titleMedium)

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onEdit = {
                            title = it.title
                            description = it.description
                            isEditing = true
                            taskToEdit = it
                        },
                        onDelete = {
                            taskToDelete = it
                            showDeleteDialog = true
                        }
                    )
                }
            }

            if (showDeleteDialog) {
                taskToDelete?.let { task ->
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Confirmar Exclusão") },
                        text = { Text("Deseja realmente excluir a tarefa \"${task.title}\"?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        taskRepository.deleteTask(task.id)
                                        tasks = taskRepository.getAllTasks()
                                        showDeleteDialog = false
                                        taskToDelete = null
                                    }
                                }
                            ) {
                                Text("Excluir")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showDeleteDialog = false
                                    taskToDelete = null
                                }
                            ) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onEdit: (Task) -> Unit,
    onDelete: (Task) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Título: ${task.title}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Descrição: ${task.description}", style = MaterialTheme.typography.bodyMedium)
            }
            Row {
                IconButton(onClick = { onEdit(task) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar tarefa")
                }
                IconButton(onClick = { onDelete(task) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Deletar tarefa")
                }
            }
        }
    }
}