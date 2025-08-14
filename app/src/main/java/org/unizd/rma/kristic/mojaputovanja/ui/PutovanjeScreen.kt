package org.unizd.rma.kristic.mojaputovanja.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.unizd.rma.kristic.mojaputovanja.R
import org.unizd.rma.kristic.mojaputovanja.model.Putovanje
import org.unizd.rma.kristic.mojaputovanja.viewmodel.PutovanjeViewModel

@Composable
fun PutovanjeScreen(
    viewModel: PutovanjeViewModel,
    onDodajClick: () -> Unit,
    onOpenDetalji: (Int) -> Unit
) {
    val putovanja by viewModel.svaPutovanja.collectAsState()

    Scaffold(
        floatingActionButton = {
            // Ako želiš Extended FAB, vidi napomenu ispod.
            LargeFloatingActionButton(onClick = onDodajClick) {
                Text("Dodaj uspomenu +")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            // Header – naslov + slika (trenutno placeholder)
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Moja putovanja", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(8.dp))
                    // PRIVREMENO koristimo postojeći resurs; zamijeni svojom slikom (npr. R.drawable.header_logo)
                    AsyncImage(
                        model = R.drawable.ic_launcher_foreground,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }

            items(putovanja) { p ->
                PutovanjeItem(p) { onOpenDetalji(p.id) }
            }
        }
    }
}

@Composable
fun PutovanjeItem(putovanje: Putovanje, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            if (putovanje.slikeUri.isNotEmpty()) {
                AsyncImage(
                    model = putovanje.slikeUri.first(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(bottom = 8.dp)
                )
            }

            Text("Destinacija: ${putovanje.destinacija}", style = MaterialTheme.typography.bodyLarge)
            Text("Prijevoz: ${putovanje.prijevoz}", style = MaterialTheme.typography.bodyMedium)
            Text("Tip: ${putovanje.tipPutovanja}", style = MaterialTheme.typography.bodyMedium)
            Text("Datum: ${putovanje.datum}", style = MaterialTheme.typography.bodySmall)

            putovanje.opis?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(6.dp))
                Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            }
        }
    }
}
