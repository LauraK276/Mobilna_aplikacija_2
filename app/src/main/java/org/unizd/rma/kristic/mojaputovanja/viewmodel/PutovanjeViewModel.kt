package org.unizd.rma.kristic.mojaputovanja.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.unizd.rma.kristic.mojaputovanja.model.Putovanje
import org.unizd.rma.kristic.mojaputovanja.repository.PutovanjeRepository

class PutovanjeViewModel(private val repository: PutovanjeRepository) : ViewModel() {

    val svaPutovanja: StateFlow<List<Putovanje>> =
        repository.svaPutovanja.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /** Dohvat jednog putovanja kao StateFlow za ekran detalja */
    fun getPutovanjeState(id: Int): StateFlow<Putovanje?> =
        repository.getPutovanje(id).stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun dodajPutovanje(putovanje: Putovanje) = viewModelScope.launch {
        repository.dodajPutovanje(putovanje)
    }

    fun urediPutovanje(putovanje: Putovanje) = viewModelScope.launch {
        repository.urediPutovanje(putovanje)
    }

    fun obrisiPutovanje(putovanje: Putovanje) = viewModelScope.launch {
        repository.obrisiPutovanje(putovanje)
    }
}

class PutovanjeViewModelFactory(private val repository: PutovanjeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PutovanjeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PutovanjeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
