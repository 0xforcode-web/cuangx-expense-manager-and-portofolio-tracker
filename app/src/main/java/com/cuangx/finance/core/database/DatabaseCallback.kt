package com.cuangx.finance.core.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

class DatabaseCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        populateDefaultCategories(db)
    }

    private fun populateDefaultCategories(db: SupportSQLiteDatabase) {
        val expenseCategories = listOf(
            Triple("Makan & Minum", "ic_restaurant", 0xFFFF5722L),
            Triple("Transport", "ic_directions_car", 0xFF2196F3L),
            Triple("Belanja", "ic_shopping_bag", 0xFF9C27B0L),
            Triple("Rumah", "ic_home", 0xFF4CAF50L),
            Triple("Kesehatan", "ic_local_hospital", 0xFFF44336L),
            Triple("Hiburan", "ic_movie", 0xFFE91E63L),
            Triple("Pendidikan", "ic_school", 0xFF3F51B5L),
            Triple("Lainnya", "ic_more_horiz", 0xFF607D8BL)
        )

        val incomeCategories = listOf(
            Triple("Gaji", "ic_work", 0xFF4CAF50L),
            Triple("Bonus", "ic_card_giftcard", 0xFFFF9800L),
            Triple("Investasi", "ic_trending_up", 0xFF00BCD4L),
            Triple("Hadiah", "ic_redeem", 0xFFE91E63L),
            Triple("Lainnya", "ic_more_horiz", 0xFF607D8BL)
        )

        expenseCategories.forEachIndexed { index, (name, icon, color) ->
            db.execSQL(
                "INSERT INTO categories (name, type, icon, color, parentId, sortOrder) VALUES (?, 'EXPENSE', ?, ?, NULL, ?)",
                arrayOf(name, icon, color, index)
            )
        }

        incomeCategories.forEachIndexed { index, (name, icon, color) ->
            db.execSQL(
                "INSERT INTO categories (name, type, icon, color, parentId, sortOrder) VALUES (?, 'INCOME', ?, ?, NULL, ?)",
                arrayOf(name, icon, color, index)
            )
        }
    }
}
