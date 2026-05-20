package com.valentinesgarage.ui.service

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.valentinesgarage.data.model.*
import com.valentinesgarage.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceBoardScreen(vm: ServiceViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    val snack  = remember { SnackbarHostState() }

    LaunchedEffect(state.toast) {
        state.toast?.let { snack.showSnackbar(it); vm.clearToast() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snack) },
        topBar = { TopAppBar(title = { Text("Service Board", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        if (state.isLoading) { LoadingScreen(); return@Scaffold }
        if (state.vehiclesWithTasks.isEmpty()) {
            EmptyState(
                "No active vehicles.
Check in a truck from the Check-In tab.",
                Modifier.padding(padding)
            )
            return@Scaffold
        }
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(state.vehiclesWithTasks) { vt ->
                VehicleServiceCard(vt, state.mechanics, vm)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VehicleServiceCard(
    vt: VehicleWithTasks,
    mechanics: List<Employee>,
    vm: ServiceViewModel
) {
    val done = vt.tasks.count { it.isCompleted }
    var completeDialog by remember { mutableStateOf<ServiceTask?>(null) }
    var addTaskDialog  by remember { mutableStateOf(false) }

    Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(vt.vehicle.registrationNumber, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(vt.vehicle.makeAndModel, fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                ConditionBadge(vt.vehicle.conditionAtCheckin)
            }
            Spacer(Modifier.height(8.dp))
            TaskProgressBar(done, vt.tasks.size)

            if (vt.vehicle.conditionNotes.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        vt.vehicle.conditionNotes, fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Divider(Modifier.padding(vertical = 10.dp))

            vt.tasks.forEach { task ->
                TaskRow(task) {
                    if (task.isCompleted) vm.uncompleteTask(task)
                    else completeDialog = task
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick  = { addTaskDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Task", fontSize = 13.sp)
                }
                if (done == vt.tasks.size && vt.tasks.isNotEmpty()) {
                    Button(
                        onClick  = { vm.markVehicleComplete(vt) },
                        modifier = Modifier.weight(1f),
                        colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                    ) {
                        Text("Mark Complete", fontSize = 13.sp)
                    }
                }
            }
        }
    }

    completeDialog?.let { task ->
        CompleteTaskDialog(
            task      = task,
            mechanics = mechanics,
            onConfirm = { m, n -> vm.completeTask(task, m, n); completeDialog = null },
            onDismiss = { completeDialog = null }
        )
    }
    if (addTaskDialog) {
        AddTaskDialog(
            onConfirm = { d, c -> vm.addCustomTask(vt.vehicle.id, d, c); addTaskDialog = false },
            onDismiss = { addTaskDialog = false }
        )
    }
}

@Composable
private fun TaskRow(task: ServiceTask, onToggle: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() })
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(
                task.taskDescription,
                fontWeight = FontWeight.Medium,
                fontSize   = 13.sp,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough
                                 else TextDecoration.None,
                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
            )
            if (task.isCompleted && task.mechanicNotes.isNotBlank()) {
                Text("Notes: ${task.mechanicNotes}", fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.secondary)
            }
            Text(task.category.name.replace('_', ' '), fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompleteTaskDialog(
    task:      ServiceTask,
    mechanics: List<Employee>,
    onConfirm: (Employee, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selected by remember { mutableStateOf<Employee?>(null) }
    var notes    by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Complete Task", fontWeight = FontWeight.Bold) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(task.taskDescription, Modifier.padding(10.dp),
                        fontWeight = FontWeight.Medium, fontSize = 13.sp)
                }
                ExposedDropdownMenuBox(expanded, { expanded = it }) {
                    OutlinedTextField(
                        value         = selected?.fullName ?: "",
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Mechanic *") },
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier      = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded, { expanded = false }) {
                        mechanics.forEach { emp ->
                            DropdownMenuItem(
                                text    = { Text("${emp.fullName} (${emp.role.name.replace('_', ' ')})") },
                                onClick = { selected = emp; expanded = false }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value         = notes,
                    onValueChange = { notes = it },
                    label         = { Text("Work Notes") },
                    placeholder   = { Text("Parts used, observations...") },
                    minLines      = 2,
                    modifier      = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick  = { selected?.let { onConfirm(it, notes) } },
                enabled  = selected != null
            ) { Text("Mark Complete") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDialog(
    onConfirm: (String, TaskCategory) -> Unit,
    onDismiss: () -> Unit
) {
    var desc     by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(TaskCategory.OTHER) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Task", fontWeight = FontWeight.Bold) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value         = desc,
                    onValueChange = { desc = it },
                    label         = { Text("Task Description *") },
                    modifier      = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenuBox(expanded, { expanded = it }) {
                    OutlinedTextField(
                        value         = category.name.replace('_', ' '),
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Category") },
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier      = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded, { expanded = false }) {
                        TaskCategory.values().forEach { c ->
                            DropdownMenuItem(
                                text    = { Text(c.name.replace('_', ' ')) },
                                onClick = { category = c; expanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick  = { if (desc.isNotBlank()) onConfirm(desc, category) },
                enabled  = desc.isNotBlank()
            ) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
