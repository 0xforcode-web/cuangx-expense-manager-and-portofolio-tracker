package com.cuangx.finance.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cuangx.finance.domain.model.Frequency
import com.cuangx.finance.domain.model.Transaction
import com.cuangx.finance.domain.model.TransactionSource
import com.cuangx.finance.domain.repository.RecurringRepository
import com.cuangx.finance.domain.repository.TransactionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar

@HiltWorker
class RecurringTransactionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val recurringRepository: RecurringRepository,
    private val transactionRepository: TransactionRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val now = System.currentTimeMillis()
        val dueRecurring = recurringRepository.getDueRecurring(now)

        for (recurring in dueRecurring) {
            val transaction = Transaction(
                type = recurring.type,
                amount = recurring.amount,
                accountId = recurring.accountId,
                toAccountId = recurring.toAccountId,
                categoryId = recurring.categoryId,
                date = now,
                note = recurring.note,
                source = TransactionSource.EXPENSE
            )

            transactionRepository.insert(transaction)

            val nextDate = calculateNextDate(recurring.frequency, recurring.nextDate)
            recurringRepository.updateNextDate(recurring.id, nextDate)
        }

        return Result.success()
    }

    private fun calculateNextDate(frequency: Frequency, currentDate: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentDate

        when (frequency) {
            Frequency.DAILY -> calendar.add(Calendar.DAY_OF_MONTH, 1)
            Frequency.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            Frequency.MONTHLY -> calendar.add(Calendar.MONTH, 1)
            Frequency.YEARLY -> calendar.add(Calendar.YEAR, 1)
        }

        return calendar.timeInMillis
    }
}
