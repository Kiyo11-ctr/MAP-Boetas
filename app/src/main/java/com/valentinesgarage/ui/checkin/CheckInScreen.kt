package com.valentinesgarage.ui.checkin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.valentinesgarage.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.valentinesgarage.data.model.*

import com.valentinesgarage.ui.auth.AuthViewModel

/**
 * Truck Check-In screen.
 *
 * Captures the vehicle's registration, make/model, odometer reading,
 * condition, and condition notes — all fields required for the anti-misuse
 * baseline. The form validates that all required fields are filled before
 * submitting to the repository.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    viewModel: CheckInViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Show success/error snackbars
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_checkin), fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = authViewModel::logout) {
                        Icon(Icons.Default.Logout, contentDescription = stringResource(R.string.btn_logout))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Info card
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Row(Modifier.padding(14.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.width(10.dp))
                    Text(stringResource(R.string.checkin_info_banner),
                        fontSize = 13.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }

            // Vehicle details
            SectionHeader(stringResource(R.string.section_vehicle_details))
            OutlinedTextField(
                value = state.registrationNumber,
                onValueChange = viewModel::onRegistrationChanged,
                label = { Text(stringResource(R.string.label_registration)) },
                placeholder = { Text(stringResource(R.string.hint_registration)) },
                modifier = Modifier.fillMaxWidth(),
                isError = state.registrationError != null,
                supportingText = state.registrationError?.let { { Text(it) } }
            )
            OutlinedTextField(
                value = state.makeAndModel,
                onValueChange = viewModel::onMakeModelChanged,
                label = { Text("Make & Model *") },
                placeholder = { Text(stringResource(R.string.hint_make_model)) },
                modifier = Modifier.fillMaxWidth(),
                isError = state.makeModelError != null,
                supportingText = state.makeModelError?.let { { Text(it) } }
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.odometerKm,
                    onValueChange = viewModel::onOdometerChanged,
                    label = { Text(stringResource(R.string.label_odometer)) },
                    modifier = Modifier.weight(1f),
                    isError = state.odometerError != null,
                    supportingText = state.odometerError?.let { { Text(it) } }
                )
                OutlinedTextField(
                    value = state.driverName,
                    onValueChange = viewModel::onDriverNameChanged,
                    label = { Text(stringResource(R.string.label_driver_name)) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Condition selection
            SectionHeader(stringResource(R.string.section_condition))
            if (state.conditionError != null) {
                Text(state.conditionError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VehicleCondition.values().forEach { cond ->
                    val selected = state.condition == cond
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.onConditionSelected(cond) },
                        label = { Text(cond.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            OutlinedTextField(
                value = state.conditionNotes,
                onValueChange = viewModel::onConditionNotesChanged,
                label = { Text(stringResource(R.string.label_condition)) }, // This might be wrong in strings.xml, checking...
                placeholder = { Text(stringResource(R.string.hint_condition_notes)) },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            // Assignment
            SectionHeader(stringResource(R.string.section_assignment))
            var mechanicExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = mechanicExpanded,
                onExpandedChange = { mechanicExpanded = it }
            ) {
                OutlinedTextField(
                    value = state.mechanics.find { it.id == state.selectedMechanicId }?.fullName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_assign_mechanic)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(mechanicExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    isError = state.mechanicError != null,
                    supportingText = state.mechanicError?.let { { Text(it) } }
                )
                ExposedDropdownMenu(expanded = mechanicExpanded, onDismissRequest = { mechanicExpanded = false }) {
                    state.mechanics.forEach { emp ->
                        DropdownMenuItem(
                            text = { Text("${emp.fullName} – ${emp.role.name.replace('_', ' ')}") },
                            onClick = { viewModel.onMechanicSelected(emp.id); mechanicExpanded = false }
                        )
                    }
                }
            }

            var priorityExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = priorityExpanded,
                onExpandedChange = { priorityExpanded = it }
            ) {
                OutlinedTextField(
                    value = state.priority.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_priority)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(priorityExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = priorityExpanded, onDismissRequest = { priorityExpanded = false }) {
                    ServicePriority.values().forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p.name) },
                            onClick = { viewModel.onPrioritySelected(p); priorityExpanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Button(
                onClick = viewModel::submitCheckIn,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                else Text(stringResource(R.string.btn_checkin), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
}
