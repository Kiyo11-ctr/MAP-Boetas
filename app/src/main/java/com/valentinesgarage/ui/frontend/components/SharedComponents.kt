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

/**
 * Reusable UI components shared across screens.
 * Keeping shared widgets here avoids duplication (DRY principle) and
 * makes the design consistent — matching the modularisation principle
 * from the course (Core/UI module responsibility).
 */

/** Coloured pill badge showing vehicle condition. */
@Composable
fun ConditionBadge(condition: VehicleCondition, modifier: Modifier = Modifier) {
    val (label, containerColor, contentColor) = when (condition) {
        VehicleCondition.GOOD     -> Triple("Good",     Color(0xFFF0FDF4), Color(0xFF16A34A))
        VehicleCondition.FAIR     -> Triple("Fair",     Color(0xFFFFFBEB), Color(0xFFD97706))
        VehicleCondition.CRITICAL -> Triple("Critical", Color(0xFFFEF2F2), Color(0xFFDC2626))
    }
    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = label,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

/** A stat summary card used on the Dashboard. */
@Composable
fun StatCard(
    label: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, elevation = CardDefaults.cardElevation(1.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium)
            Text(value, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = accentColor,
                modifier = Modifier.padding(top = 4.dp))
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp))
        }
    }
}

/** Linear progress bar for task completion. */
@Composable
fun TaskProgressBar(
    completed: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val fraction = if (total == 0) 0f else completed.toFloat() / total
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$completed / $total tasks", fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${(fraction * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                color = if (fraction == 1f) Color(0xFF16A34A) else MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = if (fraction == 1f) Color(0xFF16A34A) else MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outline
        )
    }
}

/** Screen-level loading indicator. */
@Composable
fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

/** Empty state placeholder. */
@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium)
    }
}
