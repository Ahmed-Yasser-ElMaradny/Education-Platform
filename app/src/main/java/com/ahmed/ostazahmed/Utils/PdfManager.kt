package com.ahmed.ostazahmed.Utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PdfManager(private val context: Context) {

    suspend fun createCodesPdf(codesList: List<String>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val pdfDocument = PdfDocument()

                // مقاسات الورقة الـ A4 الثابتة
                val pageWidth = 595
                val pageHeight = 842

                // متغيرات لتتبع الصفحات ومكان الكتابة
                var pageNumber = 1
                var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                var page = pdfDocument.startPage(pageInfo)
                var canvas = page.canvas

                // تجهيز الفرشة (Paint)
                val paint = Paint()
                paint.color = Color.BLACK

                // 1. تكبير حجم الخط
                paint.textSize = 24f

                // 2. سنترة الكلام في النص بالظبط
                paint.textAlign = Paint.Align.CENTER
                val xPosition = pageWidth / 2f // نقطة الـ X بقت في نص عرض الورقة

                // تحديد نقطة البداية لأول كود من فوق
                var yPosition = 100f

                // مسافة السطر (المسافة بين كل كود والتاني)
                val lineSpacing = 50f

                // اللوب الذكي اللي بيكتب ويراقب حجم الورقة
                for (code in codesList) {

                    // 3. اختبار حجم الورقة: لو نزلنا تحت أوي وقربنا من آخر الورقة (مثلاً عند 800)
                    if (yPosition > 800f) {
                        // نقفل الصفحة الحالية
                        pdfDocument.finishPage(page)

                        // نفتح صفحة جديدة ونزود رقم الصفحة
                        pageNumber++
                        pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                        page = pdfDocument.startPage(pageInfo)
                        canvas = page.canvas

                        // نرجع الـ Y لفوق تاني عشان نكتب في أول الصفحة الجديدة
                        yPosition = 100f
                    }

                    // رسم الكود في الصفحة
                    canvas.drawText(code, xPosition, yPosition, paint)

                    // 4. ننزل سطر جديد بالمسافة الكبيرة اللي حددناها
                    yPosition += lineSpacing
                }

                // نقفل آخر صفحة كنا شغالين فيها بعد ما اللوب يخلص
                pdfDocument.finishPage(page)

                // حفظ الملف في مجلد الـ Downloads
                val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(directory, "Students_Codes.pdf")

                pdfDocument.writeTo(FileOutputStream(file))
                pdfDocument.close()

                true // تمت العملية بنجاح

            } catch (e: Exception) {
                e.printStackTrace()
                false // حصل خطأ
            }
        }
    }
}