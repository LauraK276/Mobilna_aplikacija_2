package org.unizd.rma.kristic.mojaputovanja.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.unizd.rma.kristic.mojaputovanja.model.Putovanje

@Database(entities = [Putovanje::class], version = 1, exportSchema = false)
abstract class PutovanjeDatabase : RoomDatabase() {

    abstract fun putovanjeDao(): PutovanjeDao

    companion object {
        @Volatile
        private var INSTANCE: PutovanjeDatabase? = null

        fun getDatabase(context: Context): PutovanjeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PutovanjeDatabase::class.java,
                    "putovanje_baza"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
