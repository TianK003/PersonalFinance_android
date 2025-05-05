package mau.se.personalfinance.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mau.se.personalfinance.data.FinanceRepository
import mau.se.personalfinance.data.IncomeEntity
import mau.se.personalfinance.data.OutcomeEntity
import mau.se.personalfinance.data.TransactionsDatabase
import java.util.Date

class AddTransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FinanceRepository

    init {
        val database = TransactionsDatabase.getDatabase(application)
        repository = FinanceRepository(database.incomeDao(), database.outcomeDao())
    }

    fun saveTransaction(type: String, amount: Double,
                        description: String, category: String, date: Date) {
        viewModelScope.launch {
            when (type) {
                "income"  -> repository.insertIncome(
                    IncomeEntity(amount = amount, description = description,
                        category = category, date = date) )
                "outcome" -> repository.insertOutcome(
                    OutcomeEntity(amount = amount, description = description,
                        category = category, date = date) )
            }
        }
    }
}