package com.valentinesgarage.ui.employees

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.valentinesgarage.data.model.Employee
import com.valentinesgarage.ui.components.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesScreen(vm: EmployeesViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text("Employees", fontWeight = FontWeight.Bold) }) }) { padding ->
        if (state.isLoading) { LoadingScreen(); return@Scaffold }
        LazyColumn(Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(state.employees) { EmployeeCard(it) }
        }
    }
}

@Composable
private fun EmployeeCard(emp: Employee) {
    Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(44.dp), shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer) {
                Box(contentAlignment = Alignment.Center) {
                    val initials = emp.fullName.split(" ").take(2).map { it.first() }.joinToString("")
                    Text(initials, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(emp.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(emp.role.name.replace('_', ' '), fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (emp.isActive) {
                Surface(color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = MaterialTheme.shapes.small) {
                    Text("Active", fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
            }
        }
    }
}
