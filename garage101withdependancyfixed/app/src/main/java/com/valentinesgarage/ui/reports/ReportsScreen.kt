package com.valentinesgarage.ui.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.valentinesgarage.data.model.*
import com.valentinesgarage.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(vm: ReportsViewModel = hiltViewModel()) {
    val state  by vm.state.collectAsState()
    var tabIdx by remember { mutableStateOf(0) }
    val tabs   = listOf("Employee Activity", "Vehicle Log")

    Scaffold(
        topBar = { TopAppBar(title = { Text("Reports", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        if (state.isLoading) { LoadingScreen(); return@Scaffold }
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = tabIdx) {
                tabs.forEachIndexed { i, t ->
                    Tab(selected = tabIdx == i, onClick = { tabIdx = i },
                        text = { Text(t, fontSize = 13.sp) })
                }
            }
            when (tabIdx) {
                0 -> EmployeeTab(state.employeeReports)
                1 -> VehicleLogTab(state.vehicleLog)
            }
        }
    }
}

@Composable
private fun EmployeeTab(reports: List<EmployeeReport>) {
    if (reports.isEmpty()) { EmptyState("No employee data yet."); return }
    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(reports) { r ->
            Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(r.employee.fullName, fontWeight = FontWeight.Bold)
                            Text(r.employee.role.name.replace('_', ' '),
                                fontSize = 12.sp,
                                color    = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        val pctColor = if (r.completionPercent >= 80)
                            Color(0xFF16A34A) else Color(0xFFD97706)
                        Surface(
                            color = if (r.completionPercent >= 80)
                                Color(0xFFF0FDF4) else Color(0xFFFFFBEB),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("${r.completionPercent}%",
                                fontWeight = FontWeight.Bold,
                                color      = pctColor,
                                modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress   = { r.completionPercent / 100f },
                        modifier   = Modifier.fillMaxWidth().height(6.dp),
                        color      = if (r.completionPercent >= 80)
                            Color(0xFF16A34A) else Color(0xFFD97706)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("${r.tasksCompleted} tasks · ${r.vehiclesWorkedOn} vehicles",
                        fontSize = 12.sp,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun VehicleLogTab(vehicles: List<VehicleWithTasks>) {
    if (vehicles.isEmpty()) { EmptyState("No vehicles checked in yet."); return }
    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(vehicles) { vt ->
            Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(vt.vehicle.registrationNumber, fontWeight = FontWeight.Bold)
                            Text(vt.vehicle.makeAndModel, fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        ConditionBadge(vt.vehicle.conditionAtCheckin)
                    }
                    Divider(Modifier.padding(vertical = 8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        InfoItem("Check-In KM", "${vt.vehicle.odometerAtCheckin} km")
                        InfoItem("Priority",    vt.vehicle.priority.name)
                        InfoItem("Status",      vt.vehicle.status.name.replace('_', ' '))
                    }
                    if (vt.vehicle.conditionNotes.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text("\"${vt.vehicle.conditionNotes}\"",
                            fontSize = 12.sp,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(8.dp))
                    TaskProgressBar(
                        completed = vt.tasks.count { it.isCompleted },
                        total     = vt.tasks.size
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column {
        Text(label, fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
