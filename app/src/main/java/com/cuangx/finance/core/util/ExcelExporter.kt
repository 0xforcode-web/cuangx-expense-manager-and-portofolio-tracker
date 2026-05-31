package com.cuangx.finance.core.util

import android.content.Context
import android.net.Uri
import com.cuangx.finance.core.database.entity.AccountEntity
import com.cuangx.finance.core.database.entity.CategoryEntity
import com.cuangx.finance.core.database.entity.TransactionEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

data class BackupData(
    val accounts: List<AccountEntity>,
    val categories: List<CategoryEntity>,
    val transactions: List<TransactionEntity>
)

@Singleton
class ExcelExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun exportBackup(data: BackupData): File {
        val workbook = XSSFWorkbook()

        // Sheet 1: Accounts
        val accountSheet = workbook.createSheet("Accounts")
        val accountHeader = accountSheet.createRow(0)
        accountHeader.createCell(0).setCellValue("ID")
        accountHeader.createCell(1).setCellValue("Name")
        accountHeader.createCell(2).setCellValue("Type")
        accountHeader.createCell(3).setCellValue("Balance")
        accountHeader.createCell(4).setCellValue("Currency")

        data.accounts.forEachIndexed { index, account ->
            val row = accountSheet.createRow(index + 1)
            row.createCell(0).setCellValue(account.id.toDouble())
            row.createCell(1).setCellValue(account.name)
            row.createCell(2).setCellValue(account.type)
            row.createCell(3).setCellValue(account.balance)
            row.createCell(4).setCellValue(account.currency)
        }

        // Sheet 2: Categories
        val categorySheet = workbook.createSheet("Categories")
        val categoryHeader = categorySheet.createRow(0)
        categoryHeader.createCell(0).setCellValue("ID")
        categoryHeader.createCell(1).setCellValue("Name")
        categoryHeader.createCell(2).setCellValue("Type")

        data.categories.forEachIndexed { index, category ->
            val row = categorySheet.createRow(index + 1)
            row.createCell(0).setCellValue(category.id.toDouble())
            row.createCell(1).setCellValue(category.name)
            row.createCell(2).setCellValue(category.type)
        }

        // Sheet 3: Transactions
        val transactionSheet = workbook.createSheet("Transactions")
        val transactionHeader = transactionSheet.createRow(0)
        transactionHeader.createCell(0).setCellValue("ID")
        transactionHeader.createCell(1).setCellValue("Type")
        transactionHeader.createCell(2).setCellValue("Amount")
        transactionHeader.createCell(3).setCellValue("Account ID")
        transactionHeader.createCell(4).setCellValue("Category ID")
        transactionHeader.createCell(5).setCellValue("Date")
        transactionHeader.createCell(6).setCellValue("Note")

        data.transactions.forEachIndexed { index, transaction ->
            val row = transactionSheet.createRow(index + 1)
            row.createCell(0).setCellValue(transaction.id.toDouble())
            row.createCell(1).setCellValue(transaction.type)
            row.createCell(2).setCellValue(transaction.amount)
            row.createCell(3).setCellValue(transaction.accountId.toDouble())
            row.createCell(4).setCellValue((transaction.categoryId ?: 0).toDouble())
            row.createCell(5).setCellValue(transaction.date.toDouble())
            row.createCell(6).setCellValue(transaction.note)
        }

        val file = File(context.cacheDir, "cuangx_backup_${System.currentTimeMillis()}.xlsx")
        FileOutputStream(file).use { workbook.write(it) }
        workbook.close()

        return file
    }
}
