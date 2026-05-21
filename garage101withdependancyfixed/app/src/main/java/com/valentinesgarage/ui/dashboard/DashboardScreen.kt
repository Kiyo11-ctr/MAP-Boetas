package com.valentinesgarage.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.valentinesgarage.data.model.VehicleStatus
import com.valentinesgarage.data.model.VehicleWithTasks
import com.valentinesgarage.ui.components.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(vm: DashboardViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsState()
    if (state.isLoading) { LoadingScreen(); return }

    val today = LocalDate.now()
        .format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
    val active    = state.vehicles.count { it.vehicle.status != VehicleStatus.COMPLETED }
    val completed = state.vehicles.count { it.vehicle.status == VehicleStatus.COMPLETED }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Column {
                    Text("Valentine's Garage", fontWeight = FontWeight.Bold)
                    Text(today, fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            })
        }
    ) { padding ->
        LazyColumn(
            modifier        = Modifier.fillMaxSize().padding(padding),
            contentPadding  = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard("Total", "${state.vehicles.size}", "vehicles",
                        MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                    StatCard("Active", "$active", "in progress",
                        Color(0xFFD97706), Modifier.weight(1f))
                    StatCard("Done", "$completed", "completed",
                        Color(0xFF16A34A), Modifier.weight(1f))
                }
            }
            item {
                Text("Active Vehicles", fontWeight = FontWeight.Bold,
                    fontSize = 16.sp, modifier = Modifier.padding(top = 4.dp))
            }
            val activeList = state.vehicles
                .filter { it.vehicle.status != VehicleStatus.COMPLETED }
            if (activeList.isEmpty()) {
                item {
                    EmptyState("No vehicles yet.\nGo to Check-In to add a truck.")
                }
            } else {
                items(activeList) { vt -> DashboardVehicleCard(vt) }
            }
        }
    }
}

@Composable
private fun DashboardVehicleCard(vt: VehicleWithTasks) {
    val done = vt.tasks.count { it.isCompleted }
    Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(vt.vehicle.registrationNumber,
                        fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(vt.vehicle.makeAndModel, fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                ConditionBadge(vt.vehicle.conditionAtCheckin)
            }
            Spacer(Modifier.height(10.dp))
            TaskProgressBar(done, vt.tasks.size)
            Spacer(Modifier.height(6.dp))
            Text(
                "${vt.vehicle.odometerAtCheckin} km at check-in" +
                " · Priority: ${vt.vehicle.priority.name}",
                fontSize = 11.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
