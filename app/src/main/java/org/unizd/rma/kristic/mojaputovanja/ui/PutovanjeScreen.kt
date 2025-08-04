package org.unizd.rma.kristic.mojaputovanja.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.unizd.rma.kristic.mojaputovanja.model.Putovanje
import org.unizd.rma.kristic.mojaputovanja.viewmodel.PutovanjeViewModel

@Composable
fun PutovanjeScreen(viewModel: PutovanjeViewModel, onDodajClick: () -> Unit) {
    val putovanja by viewModel.svaPutovanja.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onDodajClick() }) {
                Text("+")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(putovanja) { putovanje ->
                PutovanjeItem(putovanje)
            }
        }
    }
}

@Composable
fun PutovanjeItem(putovanje: Putovanje) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Destinacija: ${putovanje.destinacija}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Prijevoz: ${putovanje.prijevoz}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Tip: ${putovanje.tipPutovanja}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Datum: ${putovanje.datum}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
