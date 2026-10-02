package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ExpenseDao
import com.example.data.dao.FamilyDao
import com.example.data.dao.NoteDao
import com.example.data.dao.ShoppingDao
import com.example.data.dao.TaskDao
import com.example.data.dao.UserDao
import com.example.data.model.Expense
import com.example.data.model.FamilyContact
import com.example.data.model.Note
import com.example.data.model.ShoppingItem
import com.example.data.model.Task
import com.example.data.model.User
import java.util.Calendar

@Database(
    entities = [
        User::class,
        ShoppingItem::class,
        Task::class,
        Expense::class,
        Note::class,
        FamilyContact::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun shoppingDao(): ShoppingDao
    fun shoppingItemDao(): ShoppingDao = shoppingDao()
    abstract fun taskDao(): TaskDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun noteDao(): NoteDao
    abstract fun familyDao(): FamilyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "momos_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            try {
                                populateInitialData(db)
                            } catch (_: Exception) {}
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun populateInitialData(db: SupportSQLiteDatabase) {
            // Initial default User entry for onboarding
            db.execSQL(
                "INSERT OR REPLACE INTO users (id, name, birthday, notificationsEnabled, isDarkMode, voiceLanguage, hasCompletedOnboarding) " +
                    "VALUES (1, '', '10-01', 1, 0, 'en-IN', 0)"
            )
        }
    }
}
