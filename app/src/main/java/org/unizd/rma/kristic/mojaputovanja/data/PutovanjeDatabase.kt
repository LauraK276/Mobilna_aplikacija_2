package org.unizd.rma.kristic.mojaputovanja.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.unizd.rma.kristic.mojaputovanja.model.Converters
import org.unizd.rma.kristic.mojaputovanja.model.Putovanje

@Database(
    entities = [Putovanje::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class) // VAŽNO
abstract class PutovanjeDatabase : RoomDatabase() {

    abstract fun putovanjeDao(): PutovanjeDao

    companion object {
        @Volatile private var INSTANCE: PutovanjeDatabase? = null

        fun getDatabase(context: Context): PutovanjeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PutovanjeDatabase::class.java,
                    "putovanje_baza_v5"
                )
                    .fallbackToDestructiveMigration() // u razvoju je najpraktičnije
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
