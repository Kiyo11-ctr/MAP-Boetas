package com.valentinesgarage.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinesgarage.data.model.VehicleCondition

@Composable
fun ConditionBadge(condition: VehicleCondition, modifier: Modifier = Modifier) {
    val (label, bg, fg) = when (condition) {
        VehicleCondition.GOOD     -> Triple("Good",     Color(0xFFF0FDF4), Color(0xFF16A34A))
        VehicleCondition.FAIR     -> Triple("Fair",     Color(0xFFFFFBEB), Color(0xFFD97706))
        VehicleCondition.CRITICAL -> Triple("Critical", Color(0xFFFEF2F2), Color(0xFFDC2626))
    }
    Surface(color = bg, shape = MaterialTheme.shapes.small, modifier = modifier) {
        Text(label, color = fg, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
             modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
    }
}

@Composable
fun StatCard(label: String, value: String, sub: String, accent: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(14.dp)) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                 fontWeight = FontWeight.Medium)
            Text(value, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = accent,
                 modifier = Modifier.padding(top = 4.dp))
            Text(sub, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                 modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
fun TaskProgressBar(completed: Int, total: Int, modifier: Modifier = Modifier) {
    val fraction = if (total == 0) 0f else completed.toFloat() / total
    Column(modifier) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("$completed / $total tasks", fontSize = 12.sp,
                 color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${(fraction * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                 color = if (fraction == 1f) Color(0xFF16A34A) else MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color    = if (fraction == 1f) Color(0xFF16A34A) else MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
