package org.unizd.rma.kristic.mojaputovanja.repository

import org.unizd.rma.kristic.mojaputovanja.data.PutovanjeDao
import org.unizd.rma.kristic.mojaputovanja.model.Putovanje
import kotlinx.coroutines.flow.Flow

class PutovanjeRepository(private val dao: PutovanjeDao) {

    val svaPutovanja: Flow<List<Putovanje>> = dao.getSvaPutovanja()

    fun getPutovanje(id: Int): Flow<Putovanje?> = dao.getPutovanjePoId(id)

    suspend fun dodajPutovanje(putovanje: Putovanje) = dao.dodajPutovanje(putovanje)
    suspend fun urediPutovanje(putovanje: Putovanje) = dao.urediPutovanje(putovanje)
    suspend fun obrisiPutovanje(putovanje: Putovanje) = dao.obrisiPutovanje(putovanje)
}
