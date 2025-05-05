package mau.se.personalfinance.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Date

class FinanceRepository(private val incomeDao: IncomeDao, private val outcomeDao: OutcomeDao) {
    // Expose flows from DAOs
    val allIncomes: Flow<List<IncomeEntity>> = incomeDao.getAllIncomes()
    val allOutcomes: Flow<List<OutcomeEntity>> = outcomeDao.getAllOutcomes()

    val incomesByAmount: Flow<List<IncomeEntity>> = incomeDao.getIncomesSortedByAmount()
    val incomesByDescription: Flow<List<IncomeEntity>> = incomeDao.getIncomesSortedByDescription()
    val incomesByCategory: Flow<List<IncomeEntity>> = incomeDao.getIncomesSortedByCategory()

    val outcomesByAmount: Flow<List<OutcomeEntity>> = outcomeDao.getOutcomesSortedByAmount()
    val outcomesByDescription: Flow<List<OutcomeEntity>> = outcomeDao.getOutcomesSortedByDescription()
    val outcomesByCategory: Flow<List<OutcomeEntity>> = outcomeDao.getOutcomesSortedByCategory()

    // Combined flow of all transactions (for balance screen)
    val allTransactions = combine(allIncomes, allOutcomes) { incomes, outcomes ->
        Pair(incomes, outcomes)
    }

    // Calculate total balance
    val totalBalance = combine(allIncomes, allOutcomes) { incomes, outcomes ->
        val totalIncome = incomes.sumOf { it.amount }
        val totalOutcome = outcomes.sumOf { it.amount }
        totalIncome - totalOutcome
    }

    fun getIncomesBetweenDates(start: Date, end: Date) = incomeDao.getIncomesBetweenDates(start, end)
    fun getOutcomesBetweenDates(start: Date, end: Date) = outcomeDao.getOutcomesBetweenDates(start, end)

    // Income operations
    suspend fun insertIncome(income: IncomeEntity) {
        incomeDao.insertIncome(income)
    }

    suspend fun deleteIncome(income: IncomeEntity) {
        incomeDao.deleteIncome(income)
    }

    suspend fun getIncomeById(id: Int): IncomeEntity? {
        return incomeDao.getIncomeById(id)
    }

    // Outcome operations
    suspend fun insertOutcome(outcome: OutcomeEntity) {
        outcomeDao.insertOutcome(outcome)
    }

    suspend fun deleteOutcome(outcome: OutcomeEntity) {
        outcomeDao.deleteOutcome(outcome)
    }

    suspend fun getOutcomeById(id: Int): OutcomeEntity? {
        return outcomeDao.getOutcomeById(id)
    }


}
