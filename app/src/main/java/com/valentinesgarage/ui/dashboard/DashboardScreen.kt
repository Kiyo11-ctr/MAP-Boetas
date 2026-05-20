package com.valentinesgarage.ui.dashboard

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.valentinesgarage.data.model.*
import com.valentinesgarage.ui.auth.AuthViewModel
import com.valentinesgarage.ui.components.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Dashboard screen — summary view for managers and mechanics.
 * Shows today's check-in count, in-progress vehicles, and completion stats.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) { LoadingScreen(); return }

    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
    val inProgress = uiState.vehiclesWithTasks.count { it.vehicle.status == VehicleStatus.IN_PROGRESS || it.vehicle.status == VehicleStatus.CHECKED_IN }
    val completed = uiState.vehiclesWithTasks.count { it.vehicle.status == VehicleStatus.COMPLETED }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.title_dashboard), fontWeight = FontWeight.Bold)
                        Text(today, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = authViewModel::logout) {
                        Icon(Icons.Default.Logout, contentDescription = stringResource(R.string.btn_logout))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Stats row
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(stringResource(R.string.stat_checked_in), "${uiState.vehiclesWithTasks.size}", stringResource(R.string.stat_total),
                        MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                    StatCard(stringResource(R.string.stat_in_progress), "$inProgress", stringResource(R.string.stat_active),
                        Color(0xFFD97706), Modifier.weight(1f))
                    StatCard(stringResource(R.string.stat_completed), "$completed", stringResource(R.string.stat_today),
                        Color(0xFF16A34A), Modifier.weight(1f))
                }
            }

            // Active vehicles
            item {
                Text(stringResource(R.string.label_active_vehicles), fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 4.dp))
            }

            if (uiState.vehiclesWithTasks.isEmpty()) {
                item { EmptyState(stringResource(R.string.empty_dashboard)) }
            } else {
                items(uiState.vehiclesWithTasks.filter {
                    it.vehicle.status != VehicleStatus.COMPLETED
                }) { vt ->
                    DashboardVehicleCard(vt)
                }
            }
        }
    }
}

@Composable
private fun DashboardVehicleCard(vt: VehicleWithTasks) {
    val done = vt.tasks.count { it.isCompleted }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(vt.vehicle.registrationNumber, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(vt.vehicle.makeAndModel, fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                ConditionBadge(vt.vehicle.conditionAtCheckin)
            }
            Spacer(Modifier.height(10.dp))
            TaskProgressBar(done, vt.tasks.size)
            Spacer(Modifier.height(6.dp))
            Text(stringResource(R.string.label_at_checkin, vt.vehicle.odometerAtCheckin, vt.vehicle.priority.name),
                fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
