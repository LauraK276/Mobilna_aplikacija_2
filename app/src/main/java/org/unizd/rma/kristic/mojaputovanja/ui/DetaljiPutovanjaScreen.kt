package org.unizd.rma.kristic.mojaputovanja.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.unizd.rma.kristic.mojaputovanja.viewmodel.PutovanjeViewModel
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetaljiPutovanjaScreen(
    id: Int,
    viewModel: PutovanjeViewModel,
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    val putovanje = viewModel.getPutovanjeState(id).collectAsState().value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(putovanje?.destinacija ?: "Detalji") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Natrag") }
                }
            )
        }
    ) { padding ->
        if (putovanje == null) {
            Box(Modifier.padding(padding).fillMaxSize()) {
                Text("Nema podataka.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (putovanje.slikeUri.isNotEmpty()) {
                LazyRow(Modifier.fillMaxWidth()) {
                    items(putovanje.slikeUri) { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier.size(220.dp).padding(end = 8.dp)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            Text("Destinacija: ${putovanje.destinacija}", style = MaterialTheme.typography.titleMedium)
            Text("Prijevoz: ${putovanje.prijevoz}", style = MaterialTheme.typography.bodyMedium)
            Text("Tip: ${putovanje.tipPutovanja}", style = MaterialTheme.typography.bodyMedium)
            Text("Datum: ${putovanje.datum}", style = MaterialTheme.typography.bodySmall)

            putovanje.opis?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.obrisiPutovanje(putovanje)
                    onDelete()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Obriši putovanje")
            }
        }
    }
}
