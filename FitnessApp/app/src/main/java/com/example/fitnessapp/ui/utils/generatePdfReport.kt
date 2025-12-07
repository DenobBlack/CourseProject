package com.example.fitnessapp.ui.utils

import android.content.ContentValues
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.example.fitnessapp.data.model.UserProfileDto
import com.example.fitnessapp.ui.screens.formatRussian
import com.example.fitnessapp.ui.viewmodel.ExerciseViewModel
import com.example.fitnessapp.ui.viewmodel.WorkoutViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

fun generatePdfReport(
    context: Context,
    user: UserProfileDto,
    workoutsVm: WorkoutViewModel,
    exerciseVm: ExerciseViewModel,
    month: YearMonth
) {
    val pdf = PdfDocument()
    val pageWidth = 595
    val pageHeight = 842
    var pageNumber = 1

    var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
    var page = pdf.startPage(pageInfo)
    var canvas: Canvas = page.canvas
    var y = 40f

    val titlePaint = Paint().apply {
        color = Color.BLACK
        textSize = 22f
        isFakeBoldText = true
    }
    val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 14f
    }
    val blue = Paint().apply {
        color = Color.parseColor("#4DA3FF")
        strokeWidth = 3f
    }

    fun newPage() {
        pdf.finishPage(page)
        pageNumber++
        pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        page = pdf.startPage(pageInfo)
        canvas = page.canvas
        y = 40f
    }

    fun drawTextLine(text: String, paint: Paint, lineHeight: Float = 18f) {
        if (y + lineHeight > pageHeight - 40f) newPage()
        canvas.drawText(text, 40f, y, paint)
        y += lineHeight
    }

    fun drawLine() {
        if (y + 10f > pageHeight - 40f) newPage()
        canvas.drawLine(40f, y, pageWidth - 40f, y, blue)
        y += 10f
    }

    // ░░░ Заголовок ░░░
    drawTextLine("ФИТНЕС-ТРЕНЕР — ОТЧЁТ", titlePaint, 22f)
    y += 10f
    drawLine()
    y += 10f

    // ░░░ Пользователь ░░░
    drawTextLine("Пользователь", titlePaint)
    drawTextLine("Имя: ${user.username}", textPaint)
    drawTextLine("Пол: ${if(user.gender=="male") "мужчина" else "женщина"}", textPaint)
    drawTextLine("Дата рождения: ${user.birthDate}", textPaint)
    drawTextLine("Рост: ${user.heightCm} см", textPaint)
    drawTextLine("Вес: ${user.weightKg} кг", textPaint)
    y += 10f
    drawLine()
    y += 10f

    // ░░░ Период ░░░
    drawTextLine("Период: ${month.formatRussian()}", titlePaint)
    y += 10f

    val workouts = workoutsVm.workouts.value
    val completions = workoutsVm.completions.value

    // ░░░ Тренировки ░░░
    val sectionPaint = Paint().apply {
        color = Color.parseColor("#1E3A8A") // тёмно-синий
        textSize = 20f
        isFakeBoldText = true
    }

    val donePaint = Paint().apply {
        color = Color.parseColor("#2E7D32") // зелёный
        textSize = 14f
    }

    val exercisePaint = Paint().apply {
        color = Color.DKGRAY
        textSize = 14f
    }



    fun drawSection(title: String) {
        drawTextLine(title, sectionPaint, 22f)
        y += 5f
        drawLine()
        y += 10f
    }

// Пример блока тренировок
    workouts.forEach { w ->
        drawTextLine("• ${w.name}", textPaint, 18f)
        w.durationMin?.let { drawTextLine("   ⏱ ${it} мин", exercisePaint) }

        val done = completions.filter { it.workoutId == w.workoutId }
        if (done.isNotEmpty()) {
            drawTextLine("   ✓ Выполнена:", donePaint)
            done.forEach { completion ->
                formatCompletionDateTime(completion.completedAt)?.let { (date, time) ->
                    drawTextLine("      $date $time", donePaint)
                }
            }
        }

        val we = workoutsVm.workoutExercises.value[w.workoutId] ?: emptyList()
        if (we.isNotEmpty()) {
            drawTextLine("   🏋️ Упражнения:", exercisePaint)
            val ui = workoutsVm.mergeExercises(we, exerciseVm.exercises.value)
            ui.forEach { ex ->
                drawTextLine("      - ${ex.name}: ${ex.sets}×${ex.reps} (${ex.weightKg} кг)", exercisePaint)
            }
        }

        y += 12f
    }


    // ░░░ Goals ░░░
    drawLine()
    y += 10f
    drawTextLine("Цели", titlePaint)
    drawTextLine("(появится позже)", textPaint)

    pdf.finishPage(page)

    val fileName = "fitness_report_${month.year}_${month.monthValue}.pdf"
    var outputStream: OutputStream? = null

    try {
        outputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Публичная Downloads папка через MediaStore
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let { resolver.openOutputStream(it) }
        } else {
            // Старый способ для Android < Q
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!dir.exists()) dir.mkdirs()
            File(dir, fileName).outputStream()
        }

        outputStream?.use { pdf.writeTo(it) }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        pdf.close()
    }
}
fun formatCompletionDateTime(completedAt: String): Pair<String, String>? {
    return try {
        val dt = LocalDateTime.parse(completedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
        val date = dt.toLocalDate().toString()        // yyyy-MM-dd
        val time = dt.toLocalTime().toString()        // HH:mm:ss
        date to time
    } catch (e: Exception) {
        null
    }
}