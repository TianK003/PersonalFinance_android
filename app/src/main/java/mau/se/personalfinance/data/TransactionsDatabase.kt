package mau.se.personalfinance.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [IncomeEntity::class, OutcomeEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TransactionsDatabase : RoomDatabase() {
    abstract fun incomeDao(): IncomeDao
    abstract fun outcomeDao(): OutcomeDao

    companion object {
        @Volatile
        private var INSTANCE: TransactionsDatabase? = null

        fun getDatabase(context: Context): TransactionsDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    TransactionsDatabase::class.java,
                    "finance_db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // potential initial data
                        }
                    })
                    // If the schema changes, destroy the db
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
