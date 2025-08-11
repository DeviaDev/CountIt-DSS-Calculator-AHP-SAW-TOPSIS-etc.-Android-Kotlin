package com.devianest.countit.utils

import android.content.Context
import android.net.Uri
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.CellType
import java.io.InputStream

class ExcelReader {

    companion object {
        fun readExcelMatrix(context: Context, uri: Uri): ExcelData? {
            return try {
                val inputStream: InputStream = context.contentResolver.openInputStream(uri)
                    ?: return null

                val workbook = WorkbookFactory.create(inputStream)
                val sheet = workbook.getSheetAt(0) // Ambil sheet pertama

                val matrix = mutableListOf<List<Double>>()
                val rowHeaders = mutableListOf<String>()
                val columnHeaders = mutableListOf<String>()

                var isFirstRow = true

                for (row in sheet) {
                    if (isFirstRow) {
                        // Header kolom (kriteria)
                        for (i in 1 until row.physicalNumberOfCells) {
                            val cell = row.getCell(i)
                            columnHeaders.add(
                                when (cell?.cellType) {
                                    CellType.STRING -> cell.stringCellValue
                                    CellType.NUMERIC -> cell.numericCellValue.toString()
                                    else -> "Kriteria $i"
                                }
                            )
                        }
                        isFirstRow = false
                        continue
                    }

                    val rowData = mutableListOf<Double>()
                    var isFirstCell = true

                    for (cell in row) {
                        if (isFirstCell) {
                            // Header baris (alternatif)
                            rowHeaders.add(
                                when (cell?.cellType) {
                                    CellType.STRING -> cell.stringCellValue
                                    CellType.NUMERIC -> cell.numericCellValue.toString()
                                    else -> "Alternatif ${rowHeaders.size + 1}"
                                }
                            )
                            isFirstCell = false
                            continue
                        }

                        // Data nilai
                        val value = when (cell?.cellType) {
                            CellType.NUMERIC -> cell.numericCellValue
                            CellType.STRING -> {
                                try {
                                    cell.stringCellValue.toDouble()
                                } catch (e: NumberFormatException) {
                                    0.0
                                }
                            }
                            else -> 0.0
                        }
                        rowData.add(value)
                    }

                    if (rowData.isNotEmpty()) {
                        matrix.add(rowData)
                    }
                }

                inputStream.close()
                workbook.close()

                ExcelData(
                    matrix = matrix,
                    rowHeaders = rowHeaders,
                    columnHeaders = columnHeaders
                )

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

data class ExcelData(
    val matrix: List<List<Double>>,
    val rowHeaders: List<String>,
    val columnHeaders: List<String>
)