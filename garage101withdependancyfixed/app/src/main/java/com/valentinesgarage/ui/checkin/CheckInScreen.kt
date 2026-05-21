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
import com.valentinesgarage.data.model.ServicePriority
import com.valentinesgarage.data.model.VehicleCondition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(vm: CheckInViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    val snack  = remember { SnackbarHostState() }

    LaunchedEffect(state.success) {
        state.success?.let { snack.showSnackbar(it); vm.clearMessages() }
    }
    LaunchedEffect(state.error) {
        state.error?.let { snack.showSnackbar(it); vm.clearMessages() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snack) },
        topBar = {
            TopAppBar(title = { Text("Truck Check-In", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Info banner
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary)
                    Text(
                        "Odometer and condition are permanently recorded at check-in " +
                        "to prevent disputes and detect vehicle misuse.",
                        fontSize = 13.sp,
                        color    = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            SectionLabel("Vehicle Details")

            OutlinedTextField(
                value         = state.registration,
                onValueChange = vm::onRegChange,
                label         = { Text("Registration Number *") },
                placeholder   = { Text("e.g. N 1234 WH") },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true
            )
            OutlinedTextField(
                value         = state.makeModel,
                onValueChange = vm::onMakeChange,
                label         = { Text("Make & Model *") },
                placeholder   = { Text("e.g. Volvo FH16") },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value           = state.odometerKm,
                    onValueChange   = vm::onOdometerChange,
                    label           = { Text("Odometer (km) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier        = Modifier.weight(1f),
                    singleLine      = true
                )
                OutlinedTextField(
                    value         = state.driverName,
                    onValueChange = vm::onDriverChange,
                    label         = { Text("Driver Name") },
                    modifier      = Modifier.weight(1f),
                    singleLine    = true
                )
            }

            SectionLabel("Vehicle Condition *")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VehicleCondition.values().forEach { c ->
                    FilterChip(
                        selected = state.condition == c,
                        onClick  = { vm.onConditionSelect(c) },
                        label    = { Text(c.name.lowercase()
                            .replaceFirstChar { it.uppercase() }) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            OutlinedTextField(
                value         = state.condNotes,
                onValueChange = vm::onCondNotesChange,
                label         = { Text("Condition Notes") },
                placeholder   = { Text("Describe any visible damage or reported faults...") },
                minLines      = 3,
                modifier      = Modifier.fillMaxWidth()
            )

            SectionLabel("Assignment")

            // Lead Mechanic dropdown
            var mechExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded          = mechExpanded,
                onExpandedChange  = { mechExpanded = it }
            ) {
                OutlinedTextField(
                    value         = state.mechanics
                        .find { it.id == state.mechanicId }?.fullName ?: "",
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Lead Mechanic *") },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(mechExpanded) },
                    modifier      = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded         = mechExpanded,
                    onDismissRequest = { mechExpanded = false }
                ) {
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
            ExposedDropdownMenuBox(
                expanded         = priExpanded,
                onExpandedChange = { priExpanded = it }
            ) {
                OutlinedTextField(
                    value         = state.priority.name,
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Priority") },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(priExpanded) },
                    modifier      = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded         = priExpanded,
                    onDismissRequest = { priExpanded = false }
                ) {
                    ServicePriority.values().forEach { p ->
                        DropdownMenuItem(
                            text    = { Text(p.name) },
                            onClick = { vm.onPrioritySelect(p); priExpanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Button(
                onClick  = vm::submit,
                enabled  = !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color    = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Complete Check-In", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text       = text,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 13.sp,
        color      = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
