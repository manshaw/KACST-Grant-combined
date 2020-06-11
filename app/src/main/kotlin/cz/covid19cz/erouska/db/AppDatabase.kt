package cz.covid19cz.erouska.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ScanDataEntity::class], version = 8, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    companion object{
        const val DATABASE_NAME = "database"
    }

    abstract val scanResultsDao: ScanDataDao
}