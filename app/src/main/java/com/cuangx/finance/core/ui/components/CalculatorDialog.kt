package com.cuangx.finance.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorDialog(
    initialValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var expression by remember { mutableStateOf(if (initialValue.isBlank()) "0" else initialValue) }
    var lastResult by remember { mutableStateOf<Double?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Kalkulator", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = expression,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                val buttons = listOf(
                    listOf("7", "8", "9", "/"),
                    listOf("4", "5", "6", "*"),
                    listOf("1", "2", "3", "-"),
                    listOf("C", "0", "=", "+")
                )
                
                buttons.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { char ->
                            val isOperator = char in listOf("+", "-", "*", "/", "=")
                            val isClear = char == "C"
                            
                            OutlinedButton(
                                onClick = {
                                    when (char) {
                                        "C" -> {
                                            expression = "0"
                                            lastResult = null
                                        }
                                        "=" -> {
                                            val result = evaluate(expression)
                                            expression = result.toString().removeSuffix(".0")
                                            lastResult = result
                                        }
                                        else -> {
                                            if (expression == "0" && !isOperator) {
                                                expression = char
                                            } else {
                                                expression += char
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1.2f),
                                contentPadding = PaddingValues(0.dp),
                                colors = when {
                                    isOperator -> ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                    isClear -> ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                    else -> ButtonDefaults.outlinedButtonColors()
                                }
                            ) {
                                Text(
                                    text = char,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = when {
                                        isOperator -> MaterialTheme.colorScheme.onPrimaryContainer
                                        isClear -> MaterialTheme.colorScheme.onErrorContainer
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { 
                val finalResult = evaluate(expression)
                onConfirm(finalResult.toString().removeSuffix(".0")) 
            }) {
                Text("Gunakan Hasil")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

private fun evaluate(expression: String): Double {
    return try {
        // Very simple expression evaluator for +, -, *, /
        val tokens = mutableListOf<String>()
        var currentNumber = ""
        
        expression.forEach { char ->
            if (char in "+-*/") {
                if (currentNumber.isNotEmpty()) tokens.add(currentNumber)
                tokens.add(char.toString())
                currentNumber = ""
            } else {
                currentNumber += char
            }
        }
        if (currentNumber.isNotEmpty()) tokens.add(currentNumber)
        
        if (tokens.isEmpty()) return 0.0
        
        // Handle * and / first
        val pass1 = mutableListOf<String>()
        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            if (token == "*" || token == "/") {
                val prev = pass1.removeAt(pass1.size - 1).toDouble()
                val next = tokens[++i].toDouble()
                val res = if (token == "*") prev * next else prev / next
                pass1.add(res.toString())
            } else {
                pass1.add(token)
            }
            i++
        }
        
        // Handle + and -
        var result = pass1[0].toDouble()
        var j = 1
        while (j < pass1.size) {
            val op = pass1[j]
            val next = pass1[++j].toDouble()
            result = if (op == "+") result + next else result - next
            j++
        }
        result
    } catch (e: Exception) {
        0.0
    }
}
