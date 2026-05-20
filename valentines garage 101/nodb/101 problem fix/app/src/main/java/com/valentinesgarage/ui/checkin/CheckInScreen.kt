package com.valentinesgarage.ui.checkin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.valentinesgarage.data.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(vm: CheckInViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    val snack  = remember { SnackbarHostState() }

    LaunchedEffect(state.success) { state.success?.let { snack.showSnackbar(it); vm.clearMessages() } }
    LaunchedEffect(state.error)   { state.error?.let   { snack.showSnackbar(it); vm.clearMessages() } }

    Scaffold(
        snackbarHost = { SnackbarHost(snack) },
        topBar = { TopAppBar(title = { Text("Truck Check-In", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Why capture info
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.secondary)
                    Text("Odometer and condition are permanently recorded to prevent
disputes and detect vehicle misuse while in the garage.",
                        fontSize = 13.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }

            SectionLabel("Vehicle Details")
            OutlinedTextField(state.registration, vm::onRegChange, Modifier.fillMaxWidth(),
                label = { Text("Registration Number *") }, placeholder = { Text("e.g. N 1234 WH") })

            OutlinedTextField(state.makeModel, vm::onMakeChange, Modifier.fillMaxWidth(),
                label = { Text("Make & Model *") }, placeholder = { Text("e.g. Volvo FH16") })

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(state.odometerKm, vm::onOdometerChange, Modifier.weight(1f),
                    label = { Text("Odometer (km) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(state.driverName, vm::onDriverChange, Modifier.weight(1f),
                    label = { Text("Driver Name") })
            }

            SectionLabel("Vehicle Condition *")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VehicleCondition.values().forEach { c ->
                    FilterChip(
                        selected = state.condition == c,
                        onClick  = { vm.onConditionSelect(c) },
                        label    = { Text(c.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            OutlinedTextField(state.condNotes, vm::onCondNotesChange, Modifier.fillMaxWidth(),
                label = { Text("Condition Notes") },
                placeholder = { Text("Describe visible damage or faults...") }, minLines = 3)

            SectionLabel("Assignment")
            // Mechanic dropdown
            var mechExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(mechExpanded, { mechExpanded = it }) {
                OutlinedTextField(
                    value = state.mechanics.find { it.id == state.mechanicId }?.fullName ?: "",
                    onValueChange = {}, readOnly = true,
                    label = { Text("Lead Mechanic *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(mechExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(mechExpanded, { mechExpanded = false }) {
                    state.mechanics.forEach { emp ->
                        DropdownMenuItem(
                            text    = { Text("${emp.fullName} (${emp.role.name.replace('_', ' ')})") },
                            onClick = { vm.onMechanicSelect(emp.id); mechExpanded = false }
                        )
                    }
                }
            }

            // Priority dropdown
            var priExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(priExpanded, { priExpanded = it }) {
                OutlinedTextField(
                    value = state.priority.name, onValueChange = {}, readOnly = true,
                    label = { Text("Priority") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(priExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(priExpanded, { priExpanded = false }) {
                    ServicePriority.values().forEach { p ->
                        DropdownMenuItem(text = { Text(p.name) },
                            onClick = { vm.onPrioritySelect(p); priExpanded = false })
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Button(onClick = vm::submit, enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp)) {
                if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary)
                else Text("Complete Check-In", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant)
}
