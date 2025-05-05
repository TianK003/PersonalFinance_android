// ChartViewModel.kt
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import mau.se.personalfinance.data.FinanceRepository
import mau.se.personalfinance.data.IncomeEntity
import mau.se.personalfinance.data.OutcomeEntity
import mau.se.personalfinance.data.TransactionsDatabase
import java.util.*

class ChartViewModel(application: Application) : AndroidViewModel(application) {
    enum class TransactionType(val displayName: String) {
        INCOME("Income"),
        OUTCOME("Outcome"),
        BOTH("All")
    }

    data class PieChartData(
        val label: String,
        val amount: Double,
        val category: String
    )

    private val repository: FinanceRepository
    private val _startDate = MutableStateFlow(getInitialStartDate())
    private val _endDate = MutableStateFlow(getDefaultEndDate())
    private val _transactionType = MutableStateFlow(TransactionType.BOTH)
    private val _incomeTotal = MutableStateFlow(0.0)
    private val _outcomeTotal = MutableStateFlow(0.0)

    val startDate: StateFlow<Date> = _startDate.asStateFlow()
    val endDate: StateFlow<Date> = _endDate.asStateFlow()
    val transactionType: StateFlow<TransactionType> = _transactionType.asStateFlow()
    val totalBalance: StateFlow<Double> = combine(
        transactionType,
        _incomeTotal,
        _outcomeTotal
    ) { type, income, outcome ->
        when (type) {
            TransactionType.INCOME -> income
            TransactionType.OUTCOME -> outcome
            TransactionType.BOTH -> income - outcome
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    init {
        val database = TransactionsDatabase.getDatabase(application)
        repository = FinanceRepository(database.incomeDao(), database.outcomeDao())
    }

    val chartData: Flow<List<PieChartData>> = combine(
        _transactionType,
        _startDate,
        _endDate
    ) { type, start, end ->
        when (type) {
            TransactionType.INCOME -> repository.getIncomesBetweenDates(start, end)
                .map { processIncomes(it) }
            TransactionType.OUTCOME -> repository.getOutcomesBetweenDates(start, end)
                .map { processOutcomes(it) }
            TransactionType.BOTH -> combine(
                repository.getIncomesBetweenDates(start, end),
                repository.getOutcomesBetweenDates(start, end)
            ) { incomes, outcomes -> processBoth(incomes, outcomes) }
        }
    }.flattenMerge()

    private fun getInitialStartDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        return calendar.time
    }

    private fun processIncomes(incomes: List<IncomeEntity>): List<PieChartData> {
        _incomeTotal.value = incomes.sumOf { it.amount }
        return incomes.groupBy { it.category }
            .map { (category, list) ->
                PieChartData(
                    label = category,
                    amount = list.sumOf { it.amount },
                    category = category
                )
            }
    }

    private fun processOutcomes(outcomes: List<OutcomeEntity>): List<PieChartData> {
        _outcomeTotal.value = outcomes.sumOf { it.amount }
        return outcomes.groupBy { it.category }
            .map { (category, list) ->
                PieChartData(
                    label = category,
                    amount = list.sumOf { it.amount },
                    category = category
                )
            }
    }

    private fun processBoth(
        incomes: List<IncomeEntity>,
        outcomes: List<OutcomeEntity>
    ): List<PieChartData> {
        _incomeTotal.value = incomes.sumOf { it.amount }
        _outcomeTotal.value = outcomes.sumOf { it.amount }

        // for all combinations to properly render
        if (_incomeTotal.value == 0.0 && _outcomeTotal.value == 0.0) {
            return emptyList()
        }
        else if (_incomeTotal.value == 0.0) {
            return listOf(PieChartData("Outcome", _outcomeTotal.value, "Outcome"))
        }
        else if (_outcomeTotal.value == 0.0) {
            return listOf(PieChartData("Income", _incomeTotal.value, "Income"))
        }

        return listOf(
            PieChartData("Income", _incomeTotal.value, "Income"),
            PieChartData("Outcome", _outcomeTotal.value, "Outcome")
        )
    }

    fun updateDates(start: Date, end: Date) {
        val calendar = Calendar.getInstance().apply {
            time = end
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        _startDate.value = start
        _endDate.value = calendar.time
    }

    fun setTransactionType(type: TransactionType) {
        _transactionType.value = type
    }

    fun getDefaultEndDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        return calendar.time
    }
}