package org.unizd.rma.kristic.mojaputovanja.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.unizd.rma.kristic.mojaputovanja.model.Putovanje

@Dao
interface PutovanjeDao {

    @Query("SELECT * FROM putovanja ORDER BY datum DESC")
    fun getSvaPutovanja(): Flow<List<Putovanje>>

    @Query("SELECT * FROM putovanja WHERE id = :id LIMIT 1")
    fun getPutovanjePoId(id: Int): Flow<Putovanje?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun dodajPutovanje(putovanje: Putovanje)

    @Update
    suspend fun urediPutovanje(putovanje: Putovanje)

    @Delete
    suspend fun obrisiPutovanje(putovanje: Putovanje)

    @Update
    suspend fun azurirajPutovanje(putovanje: Putovanje)
}
