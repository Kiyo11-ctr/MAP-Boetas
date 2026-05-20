package com.valentinesgarage.ui.service

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.valentinesgarage.R
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.valentinesgarage.data.model.*
import com.valentinesgarage.ui.components.*

/**
 * Collaborative Service Board screen.
 *
 * This is the core feature preventing tasks from going undone.
 * Every mechanic can see all active vehicles and tick off tasks with their
 * name and notes. The system prevents any task from being "assumed done"
 * because every completion must be explicitly attributed to a person.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceBoardScreen(viewModel: ServiceViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearToast() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.title_service_board), fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        if (state.isLoading) { LoadingScreen(); return@Scaffold }

        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search registration or model...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            if (state.vehiclesWithTasks.isEmpty()) {
                EmptyState(stringResource(R.string.empty_service_board), Modifier.fillMaxSize())
            } else if (state.filteredVehicles.isEmpty()) {
                EmptyState("No vehicles match your search.", Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.filteredVehicles) { vt ->
                        VehicleTaskCard(vt, state.mechanics, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun VehicleTaskCard(
    vt: VehicleWithTasks,
    mechanics: List<Employee>,
    viewModel: ServiceViewModel
) {
    val done = vt.tasks.count { it.isCompleted }
    var showCompleteTaskDialog by remember { mutableStateOf<ServiceTask?>(null) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showCompleteVehicleDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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

            // Condition note
            if (vt.vehicle.conditionNotes.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small) {
                    Text(vt.vehicle.conditionNotes, fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp))
                }
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp))

            // Task list
            vt.tasks.forEach { task ->
                TaskRow(
                    task = task,
                    onToggle = {
                        if (task.isCompleted) viewModel.uncompleteTask(task)
                        else showCompleteTaskDialog = task
                    }
                )
            }

            // Actions
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { showAddTaskDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.btn_add_task), fontSize = 13.sp)
                }
                if (done == vt.tasks.size && vt.tasks.isNotEmpty()) {
                    Button(
                        onClick = { showCompleteVehicleDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                    ) {
                        Text(stringResource(R.string.btn_mark_complete), fontSize = 13.sp)
                    }
                }
            }
        }
    }

    if (showCompleteVehicleDialog) {
        AlertDialog(
            onDismissRequest = { showCompleteVehicleDialog = false },
            title = { Text("Complete Service") },
            text = { Text("Are you sure you want to mark truck ${vt.vehicle.registrationNumber} as complete? All tasks have been verified.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.markVehicleComplete(vt)
                        showCompleteVehicleDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                ) { Text("Confirm Completion") }
            },
            dismissButton = {
                TextButton(onClick = { showCompleteVehicleDialog = false }) { Text(stringResource(R.string.btn_cancel)) }
            }
        )
    }

    // Dialog: complete task with mechanic + notes
    showCompleteTaskDialog?.let { task ->
        CompleteTaskDialog(
            task = task,
            mechanics = mechanics,
            onConfirm = { mechanic, notes ->
                viewModel.completeTask(task, mechanic, notes)
                showCompleteTaskDialog = null
            },
            onDismiss = { showCompleteTaskDialog = null }
        )
    }

    // Dialog: add custom task
    if (showAddTaskDialog) {
        AddTaskDialog(
            onConfirm = { desc, cat ->
                viewModel.addCustomTask(vt.vehicle.id, desc, cat)
                showAddTaskDialog = false
            },
            onDismiss = { showAddTaskDialog = false }
        )
    }
}

@Composable
private fun TaskRow(task: ServiceTask, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggle() }
        )
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(
                task.taskDescription,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
            )
            if (task.isCompleted && task.mechanicNotes.isNotBlank()) {
                Text("Notes: ${task.mechanicNotes}", fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(top = 2.dp))
            }
            Text(task.category.name.replace('_', ' '), fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompleteTaskDialog(
    task: ServiceTask,
    mechanics: List<Employee>,
    onConfirm: (Employee, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMechanic by remember { mutableStateOf<Employee?>(null) }
    var notes by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_complete_task), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small) {
                    Text(task.taskDescription, modifier = Modifier.padding(10.dp),
                        fontWeight = FontWeight.Medium, fontSize = 13.sp)
                }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selectedMechanic?.fullName ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.label_mechanic)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        mechanics.forEach { emp ->
                            DropdownMenuItem(
                                text = { Text("${emp.fullName} – ${emp.role.name.replace('_',' ')}") },
                                onClick = { selectedMechanic = emp; expanded = false }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(stringResource(R.string.label_work_notes)) },
                    placeholder = { Text(stringResource(R.string.hint_work_notes)) },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { selectedMechanic?.let { onConfirm(it, notes) } },
                enabled = selectedMechanic != null
            ) { Text(stringResource(R.string.btn_mark_complete)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_cancel)) } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDialog(
    onConfirm: (String, TaskCategory) -> Unit,
    onDismiss: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(TaskCategory.OTHER) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_add_custom_task), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.label_task_description)) },
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selectedCategory.name.replace('_', ' '),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.label_category)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        TaskCategory.values().forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name.replace('_', ' ')) },
                                onClick = { selectedCategory = cat; expanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (description.isNotBlank()) onConfirm(description, selectedCategory) },
                enabled = description.isNotBlank()
            ) { Text(stringResource(R.string.btn_add_task)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_cancel)) } }
    )
}
