package mau.se.personalfinance.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface IncomeDao {
    @Query("SELECT * FROM incomes ORDER BY date DESC")
    fun getAllIncomes(): Flow<List<IncomeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeEntity)

    @Delete
    suspend fun deleteIncome(income: IncomeEntity)

    @Query("SELECT * FROM incomes WHERE id = :id")
    suspend fun getIncomeById(id: Int): IncomeEntity?

    // used for sorting in income screen
    @Query("SELECT * FROM incomes ORDER BY amount DESC")
    fun getIncomesSortedByAmount(): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes ORDER BY description COLLATE NOCASE ASC")
    fun getIncomesSortedByDescription(): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes ORDER BY category COLLATE NOCASE ASC")
    fun getIncomesSortedByCategory(): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getIncomesBetweenDates(start: Date, end: Date): Flow<List<IncomeEntity>>
}

@Dao
interface OutcomeDao {
    @Query("SELECT * FROM outcomes ORDER BY date DESC")
    fun getAllOutcomes(): Flow<List<OutcomeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutcome(outcome: OutcomeEntity)

    @Delete
    suspend fun deleteOutcome(outcome: OutcomeEntity)

    @Query("SELECT * FROM outcomes WHERE id = :id")
    suspend fun getOutcomeById(id: Int): OutcomeEntity?

    // used for sorting in outcome screen
    @Query("SELECT * FROM outcomes ORDER BY amount DESC")
    fun getOutcomesSortedByAmount(): Flow<List<OutcomeEntity>>

    @Query("SELECT * FROM outcomes ORDER BY description COLLATE NOCASE ASC")
    fun getOutcomesSortedByDescription(): Flow<List<OutcomeEntity>>

    @Query("SELECT * FROM outcomes ORDER BY category COLLATE NOCASE ASC")
    fun getOutcomesSortedByCategory(): Flow<List<OutcomeEntity>>

    @Query("SELECT * FROM outcomes WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getOutcomesBetweenDates(start: Date, end: Date): Flow<List<OutcomeEntity>>
}