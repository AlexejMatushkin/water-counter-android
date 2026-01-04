package com.practicum.watercounter

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.watercounter.ui.theme.WaterCounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WaterCounterTheme {
                WaterCounterApp()
            }
        }
    }
}

@Composable
fun WaterCounterApp() {
    val context = LocalContext.current

    val prefsManager = remember { PreferencesManager(context) }
    // 👇 САМАЯ ВАЖНАЯ СТРОКА - состояние приложения
    // 'count' - текущее значение (сколько стаканов)
    // 'setCount' - функция для изменения этого значения
    var count by remember { mutableStateOf(prefsManager.getWaterCount()) }

    // Цель на день
    var dailyGoal by remember { mutableStateOf(prefsManager.getDailyGoal()) }

    // Проверяем, не наступил ли новый день
    LaunchedEffect(Unit) {
        prefsManager.resetIfNewDay()
        count = prefsManager.getWaterCount()
    }

    // Функция для обновления счётчика
    fun updateCount(newCount: Int) {
        count = newCount
        prefsManager.saveWaterCount(newCount)
    }

    // Функция для обновления цели
    fun updateGoal(newGoal: Int) {
        dailyGoal = newGoal
        prefsManager.saveDailyGoal(newGoal)
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Заголовок
            Text(
                text = "\uD83D\uDCA7 Water Tracker",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Большое отображение текущего количества
            Text(
                text = "\uD83E\uDD5B $count стаканов",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )

            // Прогресс текстом
            if (count >= dailyGoal) {
                Text(
                    text = "\uD83C\uDF89 Цель достигнута!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Green
                )
            } else {
                Text(
                    text = "Цель: $dailyGoal стаканов",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Визуальный прогресс (простая полоска)
            LinearProgressIndicator(
                progress = if (dailyGoal > 0) count.toFloat() / dailyGoal else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
                    .height(12.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Ряд кнопок
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Кнопка Добавить
                Button(
                    onClick = {
                        updateCount( count + 1 )
                        vibrate(context, 50) // Вибрация 50 мс
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green
                    )
                ) {
                    Text(
                        text = "+ Стакан",
                        fontSize = 15.sp
                    )
                }

                // Card

                // Кнопка +2 стакана
                Button(
                    onClick = {
                        updateCount( count + 2)
                        vibrate(context, 100) // Более длинная вибрация
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green
                    )
                ) {
                    Text(
                        text = "+2 Стакана",
                        fontSize = 15.sp
                    )
                }

                // Кнопка убрать (не может быть меньше нуля)
                Button(
                    onClick = {
                        if (count > 0) {
                            updateCount(count - 1)
                            vibrate(context, 30) // Короткая вибрация
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(
                        text = "- Стакан",
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Панель настройки цели - чисто функциональная
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎯 Настройка цели:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Кнопка уменьшения цели
                    IconButton(
                        onClick = {
                            if (dailyGoal > 1) updateGoal(dailyGoal - 1)
                        }
                    ) {
                        Text("➖", fontSize = 20.sp)
                    }

                    // Текущая цель
                    Text(
                        text = "$dailyGoal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    // Кнопка увеличения цели
                    IconButton(
                        onClick = { updateGoal(dailyGoal + 1) }
                    ) {
                        Text("➕", fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Кнопки сброса - чистый функционал
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Кнопка сброса счётчика
                OutlinedButton(
                    onClick = {
                        updateCount(0)
                        vibrate(context, 100)
                    }
                ) {
                    Text("🔄 Сбросить счётчик")
                }

                // Кнопка сброса цели
                OutlinedButton(
                    onClick = {
                        updateGoal(10)
                    }
                ) {
                    Text("🎯 Цель: 10")
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Только мотивационные сообщения
            Text(
                text = when {
                    count == 0 -> "Начни день со стакана воды! 🌅"
                    count < dailyGoal / 2 -> "Так держать! Продолжай! 💪"
                    count in dailyGoal..(dailyGoal + 2) -> "Отлично! Цель достигнута! 🎉"
                    count > dailyGoal + 2 -> "Ты сегодня чемпион! 🏆"
                    else -> "Всего ${dailyGoal - count} стаканов до цели!"
                },
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )

            // Визуальный разделитель
            Spacer(modifier = Modifier.height(30.dp))

            // Простая информация о прогрессе
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Прогресс:",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${((count.toFloat() / dailyGoal) * 100).toInt()}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

fun vibrate(context: Context, milliseconds: Long) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(milliseconds)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WaterCounterTheme {
        WaterCounterApp()
    }
}