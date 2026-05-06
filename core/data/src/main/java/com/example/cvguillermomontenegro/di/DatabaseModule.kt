package com.example.cvguillermomontenegro.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cvguillermomontenegro.data.local.AppDatabase
import com.example.cvguillermomontenegro.data.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "cv_database"
        )
            .addMigrations(MIGRATION_3_4)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "ALTER TABLE users ADD COLUMN isActive INTEGER NOT NULL DEFAULT 0"
            )
            database.execSQL(
                """
                UPDATE users
                SET isActive = 1
                WHERE id = (
                    SELECT id
                    FROM users
                    ORDER BY updatedAt DESC, name ASC
                    LIMIT 1
                )
                """.trimIndent()
            )
        }
    }
}
