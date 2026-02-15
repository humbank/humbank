package org.scrobotic.humbank.screens.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.data.formatCurrency
import org.scrobotic.humbank.ui.BlueStart
import kotlin.math.min
import kotlin.time.ExperimentalTime
import org.scrobotic.humbank.ui.White
import kotlin.time.Instant

data class BalancePoint @OptIn(ExperimentalTime::class) constructor(
    val timestamp: Instant,
    val balance: Double,
    val description: String
)

@OptIn(ExperimentalTime::class)
@Composable
fun BalanceLineChart(
    transactions: List<Transaction>,
    accountId: String,
    currentBalance: Double
) {
    // Calculate balance progression
    val balancePoints = remember(transactions, accountId, currentBalance) {
        calculateBalanceProgression(transactions, accountId, currentBalance)
    }
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    val labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val surfaceColor = MaterialTheme.colorScheme.surface

    // Early return if not enough data
    if (balancePoints.size < 2) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Not enough transaction data to display chart",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        return
    }

    val textMeasurer = rememberTextMeasurer()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(16.dp)
        ) {
            val chartWidth = size.width - 60.dp.toPx()
            val chartHeight = size.height - 50.dp.toPx()
            val leftPadding = 50.dp.toPx()
            val bottomPadding = 40.dp.toPx()

            // Calculate min and max for Y-axis
            val minBalance = balancePoints.minOf { it.balance }
            val maxBalance = balancePoints.maxOf { it.balance }
            val range = maxBalance - minBalance
            val padding = if (range > 0) range * 0.15 else 100.0

            val displayMin = minBalance - padding
            val displayMax = maxBalance + padding
            val displayRange = displayMax - displayMin

            // Prevent division by zero
            if (displayRange == 0.0) {
                // Draw a flat line if all values are the same
                val y = chartHeight / 2
                drawLine(
                    color = BlueStart,
                    start = Offset(leftPadding, y),
                    end = Offset(leftPadding + chartWidth, y),
                    strokeWidth = 3.dp.toPx()
                )
                return@Canvas
            }

            // Draw grid lines and Y-axis labels
            val ySteps = 4
            val gridColor = gridColor
            val labelStyle = TextStyle(
                color = labelColor,
                fontSize = 10.sp
            )

            for (i in 0..ySteps) {
                val value = displayMin + (i * displayRange / ySteps)
                val y = chartHeight - (i * chartHeight / ySteps)

                // Grid line
                drawLine(
                    color = gridColor,
                    start = Offset(leftPadding, y),
                    end = Offset(leftPadding + chartWidth, y),
                    strokeWidth = 1.dp.toPx()
                )

                // Y-axis label
                drawText(
                    textMeasurer = textMeasurer,
                    text = value.toInt().toString(),
                    style = labelStyle,
                    topLeft = Offset(0f, y - 6.dp.toPx())
                )
            }

            // Draw vertical grid lines and X-axis labels
            val xSteps = min(balancePoints.size - 1, 5)
            for (i in 0..xSteps) {
                val index = (i * (balancePoints.size - 1) / xSteps).coerceIn(0, balancePoints.lastIndex)
                val x = leftPadding + (i * chartWidth / xSteps)

                // Vertical grid line
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, chartHeight),
                    strokeWidth = 1.dp.toPx()
                )

                // X-axis date label
                val dateText = formatDateLabel(balancePoints[index].timestamp)
                drawText(
                    textMeasurer = textMeasurer,
                    text = dateText,
                    style = labelStyle,
                    topLeft = Offset(x - 15.dp.toPx(), chartHeight + 10.dp.toPx())
                )
            }

            // Calculate points for the line
            val points = balancePoints.mapIndexed { index, point ->
                val x = leftPadding + (index * chartWidth / (balancePoints.size - 1))
                val normalizedValue = (point.balance - displayMin) / displayRange
                val y = chartHeight - (normalizedValue * chartHeight).toFloat()
                Offset(x, y)
            }

            // Draw gradient fill
            val fillPath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, chartHeight)
                    points.forEach { point ->
                        lineTo(point.x, point.y)
                    }
                    lineTo(points.last().x, chartHeight)
                    close()
                }
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BlueStart.copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = chartHeight
                )
            )

            // Draw the main line
            val linePath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, points.first().y)
                    points.drop(1).forEach { point ->
                        lineTo(point.x, point.y)
                    }
                }
            }

            drawPath(
                path = linePath,
                color = BlueStart,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Draw points
            points.forEach { point ->
                drawCircle(
                    color = BlueStart,
                    radius = 5.dp.toPx(),
                    center = point
                )
                drawCircle(
                    color = surfaceColor,
                    radius = 2.5.dp.toPx(),
                    center = point
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun calculateBalanceProgression(
    transactions: List<Transaction>,
    accountId: String,
    currentBalance: Double
): List<BalancePoint> {
    if (transactions.isEmpty()) return emptyList()

    // Sort transactions by date (oldest first)
    val sortedTransactions = transactions.sortedBy { it.transaction_date }

    val points = mutableListOf<BalancePoint>()
    var runningBalance = currentBalance

    // Work backwards from current balance to calculate historical balances
    sortedTransactions.reversed().forEach { tx ->
        // Add point with current balance
        points.add(
            0,
            BalancePoint(
                timestamp = tx.transaction_date,
                balance = runningBalance,
                description = tx.description
            )
        )

        // Reverse the transaction to get previous balance
        // If we received money, subtract it to go back in time
        // If we sent money, add it back to go back in time
        runningBalance -= if (tx.receiver == accountId) {
            tx.amount  // Subtract incoming
        } else {
            -tx.amount // Add back outgoing (double negative = positive)
        }
    }

    // Add initial starting point
    if (sortedTransactions.isNotEmpty()) {
        points.add(
            0,
            BalancePoint(
                timestamp = sortedTransactions.first().transaction_date,
                balance = runningBalance,
                description = "Starting balance"
            )
        )
    }

    return points
}

@OptIn(ExperimentalTime::class)
private fun formatDateLabel(timestamp: Instant): String {
    val dateTime = timestamp.toString()
    // Extract MM-DD from ISO format (e.g., "2024-02-15T10:30:00Z" -> "02-15")
    return if (dateTime.length >= 10) {
        dateTime.substring(5, 10)
    } else {
        dateTime.take(10)
    }
}