package mau.se.personalfinance.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

interface Transaction {
    val id: Int
    val amount: Double
    val description: String
    val category: String
    val date: Date
}

@Entity(tableName = "incomes")
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    override val amount: Double,
    override val description: String,
    override val category: String,
    override val date: Date
) : Transaction

@Entity(tableName = "outcomes")
data class OutcomeEntity(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    override val amount: Double,
    override val description: String,
    override val category: String,
    override val date: Date
) : Transaction
