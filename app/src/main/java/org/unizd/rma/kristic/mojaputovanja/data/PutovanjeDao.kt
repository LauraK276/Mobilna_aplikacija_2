package org.unizd.rma.kristic.mojaputovanja.data

import androidx.room.*
import androidx.room.Dao
import org.unizd.rma.kristic.mojaputovanja.model.Putovanje
import kotlinx.coroutines.flow.Flow



@Dao
interface PutovanjeDao {

    @Query("SELECT * FROM putovanja ORDER BY datum DESC")
    fun getSvaPutovanja(): Flow<List<Putovanje>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun dodajPutovanje(putovanje: Putovanje)

    @Update
    suspend fun urediPutovanje(putovanje: Putovanje)

    @Delete
    suspend fun obrisiPutovanje(putovanje: Putovanje)
}
