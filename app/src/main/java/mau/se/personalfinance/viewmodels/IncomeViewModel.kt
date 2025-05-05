package mau.se.personalfinance.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import mau.se.personalfinance.data.FinanceRepository
import mau.se.personalfinance.data.IncomeEntity
import mau.se.personalfinance.data.TransactionsDatabase
import java.util.Calendar
import java.util.Date

class IncomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FinanceRepository

    // Date filtering state
    private val _startDate = MutableStateFlow(getDefaultStartDate())
    val startDate: StateFlow<Date> = _startDate

    private val _endDate = MutableStateFlow(getDefaultEndDate())
    val endDate: StateFlow<Date> = _endDate

    // Sorting state
    enum class SortType {
        DATE,
        AMOUNT,
        DESCRIPTION,
        CATEGORY
    }

    private val _sortType = MutableStateFlow<SortType>(SortType.DATE)
    val sortType: StateFlow<SortType> = _sortType

    @OptIn(ExperimentalCoroutinesApi::class)
    val sortedIncomes: Flow<List<IncomeEntity>> = combine(
        combine(_startDate, _endDate) { start, end ->
            repository.getIncomesBetweenDates(start, end)
        }.flatMapLatest { it },
        _sortType
    ) { incomes, sortType ->
        when (sortType) {
            SortType.DATE -> incomes.sortedByDescending { it.date }
            SortType.AMOUNT -> incomes.sortedByDescending { it.amount }
            SortType.DESCRIPTION -> incomes.sortedBy { it.description }
            SortType.CATEGORY -> incomes.sortedBy { it.category }
        }
    }

    init {
        val database = TransactionsDatabase.getDatabase(application)
        repository = FinanceRepository(database.incomeDao(), database.outcomeDao())
    }

    // Date management
    fun updateDates(start: Date, end: Date) {
        updateStartDate(start)
        updateEndDate(end)
    }

    private fun updateStartDate(date: Date) {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        _startDate.value = calendar.time
    }

    private fun updateEndDate(date: Date) {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        _endDate.value = calendar.time
    }

    private fun getDefaultStartDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        return calendar.time
    }

    fun setSortType(sortType: SortType) {
        _sortType.value = sortType
    }

    fun deleteIncome(income: IncomeEntity) {
        viewModelScope.launch {
            repository.deleteIncome(income)
        }
    }

    fun getDefaultEndDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        return calendar.time
    }
}