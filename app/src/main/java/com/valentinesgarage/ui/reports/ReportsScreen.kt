package com.valentinesgarage.ui.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.valentinesgarage.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.valentinesgarage.data.model.*
import com.valentinesgarage.ui.components.*

/**
 * Reports screen — restricted to Valentine (manager role).
 *
 * Two tabs:
 * 1. Employee Activity — how many tasks each mechanic completed and on which vehicles.
 * 2. Vehicle Log — each vehicle's condition and odometer at check-in vs current reading.
 *
 * This fulfils the assignment requirement: "Valentine should be able to see reports
 * on what each employee did and the condition of the vehicles when they were checked in."
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(stringResource(R.string.tab_employee_activity), stringResource(R.string.tab_vehicle_log))

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.title_reports), fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        if (state.isLoading) { LoadingScreen(); return@Scaffold }

        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { idx, title ->
                    Tab(selected = selectedTab == idx, onClick = { selectedTab = idx },
                        text = { Text(title, fontSize = 13.sp) })
                }
            }

            when (selectedTab) {
                0 -> EmployeeActivityTab(state.employeeReports)
                1 -> VehicleLogTab(state.vehicleLog)
            }
        }
    }
}

@Composable
private fun EmployeeActivityTab(reports: List<EmployeeReport>) {
    if (reports.isEmpty()) { EmptyState(stringResource(R.string.empty_reports_employee)); return }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(reports) { report ->
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(report.employee.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(report.employee.role.name.replace('_', ' '), fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            color = if (report.completionPercent >= 80) Color(0xFFF0FDF4) else Color(0xFFFFFBEB),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("${report.completionPercent}%",
                                color = if (report.completionPercent >= 80) Color(0xFF16A34A) else Color(0xFFD97706),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { report.completionPercent / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = if (report.completionPercent >= 80) Color(0xFF16A34A) else Color(0xFFD97706)
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(stringResource(R.string.label_tasks_completed, report.tasksCompleted), fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(stringResource(R.string.label_vehicles_worked, report.vehiclesWorkedOn), fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun VehicleLogTab(vehicles: List<VehicleWithTasks>) {
    if (vehicles.isEmpty()) { EmptyState(stringResource(R.string.empty_reports_vehicle)); return }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(vehicles) { vt ->
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(vt.vehicle.registrationNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(vt.vehicle.makeAndModel, fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        ConditionBadge(vt.vehicle.conditionAtCheckin)
                    }
                    Divider(Modifier.padding(vertical = 8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        InfoItem(stringResource(R.string.label_checkin_km), "${vt.vehicle.odometerAtCheckin} km")
                        InfoItem(stringResource(R.string.label_priority), vt.vehicle.priority.name)
                        InfoItem(stringResource(R.string.label_status), vt.vehicle.status.name.replace('_', ' '))
                    }
                    if (vt.vehicle.conditionNotes.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text("\"${vt.vehicle.conditionNotes}\"", fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(8.dp))
                    TaskProgressBar(
                        completed = vt.tasks.count { it.isCompleted },
                        total = vt.tasks.size
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column {
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
