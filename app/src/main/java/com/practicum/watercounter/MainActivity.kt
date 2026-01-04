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
    // 👇 САМАЯ ВАЖНАЯ СТРОКА - состояние приложения
    // 'count' - текущее значение (сколько стаканов)
    // 'setCount' - функция для изменения этого значения
    var count by remember { mutableStateOf(0) }

    // Цель на день
    val dailyGoal = 10

    val context = LocalContext.current
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
                        count++
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

                // Кнопка +2 стакана
                Button(
                    onClick = {
                        count += 2
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
                            count--
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

            Spacer(modifier = Modifier.height(20.dp))

            // Кнопка сброса
            OutlinedButton(
                onClick = { count = 0 } // возвразщаем к 0
            ) {
                Text("Сбросить за день")
            }

            // Подсказка внизу
            Text(
                text = when {
                    count == 0 -> "Начни день со стакана воды! 🌅"
                    count < dailyGoal / 2 -> "Так держать! Продолжай! 💪"
                    count in dailyGoal..(dailyGoal + 2) -> "Отлично! Цель достигнута! 🎉"
                    count > dailyGoal + 2 -> "Ты сегодня чемпион! 🏆"
                    else -> "Всего ${dailyGoal - count} стакана до цели! Ты сможешь!"
                },
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 40.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
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