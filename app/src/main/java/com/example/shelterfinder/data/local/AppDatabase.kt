// 앱 전체에서 사용할 Room Database를 생성하고 관리하는 클래스이다.
package com.example.shelterfinder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.shelterfinder.data.local.dao.ShelterDao
import com.example.shelterfinder.data.local.entity.ShelterEntity
import android.content.Context
import androidx.room.Room



@Database(
    entities = [ShelterEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shelterDao(): ShelterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shelter_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}