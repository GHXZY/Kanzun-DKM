package com.kanzun.perbendaharaan.core.util.pdf

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportContent
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

    // A4 dimensions in points at 72 DPI (595 x 842)
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 36f

    fun generatePdf(context: Context, content: ReportContent, outputFile: File): File {
        val pdfDocument = PdfDocument()

        val titlePaint = Paint().apply {
            color = Color.rgb(15, 23, 42) // Slate 900
            textSize = 15f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val centerTitlePaint = Paint().apply {
            color = Color.rgb(15, 23, 42) // Slate 900
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val centerSubTitlePaint = Paint().apply {
            color = Color.rgb(71, 85, 105) // Slate 600
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val subTitlePaint = Paint().apply {
            color = Color.rgb(71, 85, 105) // Slate 600
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = Color.rgb(51, 65, 85) // Slate 700
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val italicTextPaint = Paint().apply {
            color = Color.rgb(71, 85, 105) // Slate 600
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            isAntiAlias = true
        }

        val tableHeaderPaint = Paint().apply {
            color = Color.WHITE
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val borderPaint = Paint().apply {
            color = Color.rgb(203, 213, 225) // Slate 300
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val tableHeaderBgPaint = Paint().apply {
            color = Color.rgb(30, 41, 59) // Slate 800
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val altRowBgPaint = Paint().apply {
            color = Color.rgb(248, 250, 252) // Slate 50
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.rgb(15, 23, 42)
            strokeWidth = 2f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val thinLinePaint = Paint().apply {
            color = Color.rgb(100, 116, 139)
            strokeWidth = 0.5f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        var y = MARGIN

        // 1. KOP SURAT RESMI MASJID
        var logoWidthOffset = 0f
        if (content.logoPath.isNotBlank()) {
            val logoFile = File(content.logoPath)
            if (logoFile.exists()) {
                try {
                    val bitmap = BitmapFactory.decodeFile(logoFile.absolutePath)
                    if (bitmap != null) {
                        val logoSize = 48f
                        val srcRect = Rect(0, 0, bitmap.width, bitmap.height)
                        val dstRect = RectF(MARGIN, y, MARGIN + logoSize, y + logoSize)
                        canvas.drawBitmap(bitmap, srcRect, dstRect, null)
                        logoWidthOffset = logoSize + 12f
                    }
                } catch (_: Exception) {
                    logoWidthOffset = 0f
                }
            }
        }

        val textStartX = MARGIN + logoWidthOffset

        canvas.drawText(content.mosqueName, textStartX, y + 14f, titlePaint)
        canvas.drawText(content.mosqueAddress, textStartX, y + 27f, subTitlePaint)
        canvas.drawText("Kontak: ${content.mosquePhone} | Email: ${content.mosqueEmail}", textStartX, y + 40f, subTitlePaint)
        y += 52f

        // Kop Surat Double Divider Line
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
        canvas.drawLine(MARGIN, y + 2.5f, PAGE_WIDTH - MARGIN, y + 2.5f, thinLinePaint)
        y += 18f

        // 2. JUDUL SURAT & PERIODE LAPORAN
        val docTitle = "SURAT LAPORAN ${content.reportType.title.uppercase()}"
        canvas.drawText(docTitle, PAGE_WIDTH / 2f, y, centerTitlePaint)
        y += 15f
        canvas.drawText("Periode: ${content.periodLabel}", PAGE_WIDTH / 2f, y, centerSubTitlePaint)
        y += 22f

        // 3. RINGKASAN LAPORAN (jika ada)
        if (content.summaries.isNotEmpty()) {
            val boxHeight = (content.summaries.size * 15f) + 10f
            val boxRect = RectF(MARGIN, y, PAGE_WIDTH - MARGIN, y + boxHeight)
            canvas.drawRoundRect(boxRect, 4f, 4f, altRowBgPaint)
            canvas.drawRoundRect(boxRect, 4f, 4f, borderPaint)

            var summaryY = y + 14f
            content.summaries.forEach { item ->
                canvas.drawText("${item.label}:", MARGIN + 10f, summaryY, textPaint)
                val valWidth = textPaint.measureText(item.value)
                canvas.drawText(item.value, PAGE_WIDTH - MARGIN - 10f - valWidth, summaryY, textPaint)
                summaryY += 15f
            }
            y += boxHeight + 18f
        }

        // 4. TABEL LAPORAN
        if (content.tableHeaders.isNotEmpty()) {
            val numCols = content.tableHeaders.size
            val colWidth = (PAGE_WIDTH - (MARGIN * 2)) / numCols
            val rowHeight = 20f

            // Table Header Bar
            canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + rowHeight, tableHeaderBgPaint)
            content.tableHeaders.forEachIndexed { idx, header ->
                val colX = MARGIN + (idx * colWidth) + 5f
                canvas.drawText(header, colX, y + 14f, tableHeaderPaint)
            }
            y += rowHeight

            // Table Rows
            content.tableRows.forEachIndexed { rowIdx, row ->
                // Overflow Check
                if (y + rowHeight + 130f > PAGE_HEIGHT - MARGIN) {
                    canvas.drawText("Halaman $pageNumber", PAGE_WIDTH / 2f - 20f, PAGE_HEIGHT - 20f, subTitlePaint)
                    pdfDocument.finishPage(page)

                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    y = MARGIN

                    // Repeat Header on new page
                    canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + rowHeight, tableHeaderBgPaint)
                    content.tableHeaders.forEachIndexed { idx, header ->
                        val colX = MARGIN + (idx * colWidth) + 5f
                        canvas.drawText(header, colX, y + 14f, tableHeaderPaint)
                    }
                    y += rowHeight
                }

                if (rowIdx % 2 == 1) {
                    canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + rowHeight, altRowBgPaint)
                }
                canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + rowHeight, borderPaint)

                row.columns.forEachIndexed { colIdx, text ->
                    val colX = MARGIN + (colIdx * colWidth) + 5f
                    val truncated = if (text.length > 22) text.take(20) + "..." else text
                    canvas.drawText(truncated, colX, y + 14f, textPaint)
                }
                y += rowHeight
            }
        }

        y += 20f

        // 5. PENUTUP FORMAL SURAT LAPORAN
        val closingText = "Demikian surat laporan ini dibuat dengan sebenarnya sebagai bentuk pertanggungjawaban pengelolaan keuangan masjid."
        canvas.drawText(closingText, MARGIN, y, italicTextPaint)
        y += 28f

        // 6. AREA TANDA TANGAN & TANGGAL
        if (y + 110f > PAGE_HEIGHT - MARGIN) {
            canvas.drawText("Halaman $pageNumber", PAGE_WIDTH / 2f - 20f, PAGE_HEIGHT - 20f, subTitlePaint)
            pdfDocument.finishPage(page)

            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            y = MARGIN + 20f
        }

        val todayFormatted = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())
        val cityLocation = content.mosqueAddress.split(",").lastOrNull()?.trim()?.ifBlank { "Jakarta" } ?: "Jakarta"
        val dateText = "$cityLocation, $todayFormatted"

        val leftX = MARGIN + 20f
        val rightX = PAGE_WIDTH - MARGIN - 150f

        // Place and date above right signature block
        canvas.drawText(dateText, rightX, y, subTitlePaint)
        y += 18f

        canvas.drawText("Mengetahui,", leftX, y, subTitlePaint)
        canvas.drawText("Ketua DKM Masjid", leftX, y + 13f, titlePaint)

        canvas.drawText("Dibuat oleh,", rightX, y, subTitlePaint)
        canvas.drawText("Bendahara", rightX, y + 13f, titlePaint)

        y += 54f

        canvas.drawText("( ${content.chairmanName} )", leftX, y, textPaint)
        canvas.drawText("( ${content.treasurerName} )", rightX, y, textPaint)

        // Footer Page Numbering
        canvas.drawText("Halaman $pageNumber", PAGE_WIDTH / 2f - 20f, PAGE_HEIGHT - 20f, subTitlePaint)

        pdfDocument.finishPage(page)

        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return outputFile
    }
}
