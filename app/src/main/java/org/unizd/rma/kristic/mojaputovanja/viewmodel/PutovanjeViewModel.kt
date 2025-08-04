package org.unizd.rma.kristic.mojaputovanja.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import org.unizd.rma.kristic.mojaputovanja.model.Putovanje
import org.unizd.rma.kristic.mojaputovanja.repository.PutovanjeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PutovanjeViewModel(private val repository: PutovanjeRepository) : ViewModel() {

    // Sva putovanja kao StateFlow da UI može pratiti promjene
    val svaPutovanja: StateFlow<List<Putovanje>> = repository.svaPutovanja
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun dodajPutovanje(putovanje: Putovanje) {
        viewModelScope.launch {
            repository.dodajPutovanje(putovanje)
        }
    }

    fun urediPutovanje(putovanje: Putovanje) {
        viewModelScope.launch {
            repository.urediPutovanje(putovanje)
        }
    }

    fun obrisiPutovanje(putovanje: Putovanje) {
        viewModelScope.launch {
            repository.obrisiPutovanje(putovanje)
        }
    }
}

// Factory klasa za kreiranje ViewModela
class PutovanjeViewModelFactory(private val repository: PutovanjeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PutovanjeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PutovanjeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
