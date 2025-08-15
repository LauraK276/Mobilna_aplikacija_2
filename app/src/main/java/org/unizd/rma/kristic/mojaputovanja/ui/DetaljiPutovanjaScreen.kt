package org.unizd.rma.kristic.mojaputovanja.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.unizd.rma.kristic.mojaputovanja.R
import org.unizd.rma.kristic.mojaputovanja.viewmodel.PutovanjeViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetaljiPutovanjaScreen(
    id: Int,
    viewModel: PutovanjeViewModel,
    onBack: () -> Unit,
    onDelete: () -> Unit
) {

    val putovanjeState = viewModel.getPutovanjeState(id).collectAsState(initial = null)
    val putovanje = putovanjeState.value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalji") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Natrag") } }
            )
        }
    ) { padding ->
        if (putovanje == null) {
            Box(Modifier.padding(padding).fillMaxSize()) { Text("Nema podataka.") }
            return@Scaffold
        }

        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()
        ) {

            val slike = remember(putovanje.id) { putovanje.slikeUri }

            if (slike.isNotEmpty()) {
                LazyRow(Modifier.fillMaxWidth()) {
                    items(slike, key = { it }) { path ->
                        val model = remember(path) {
                            val f = File(path)
                            if (f.exists()) f else R.drawable.logo_small
                        }
                        AsyncImage(
                            model = model,
                            placeholder = painterResource(R.drawable.logo_small),
                            error = painterResource(R.drawable.logo_small),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(width = 220.dp, height = 160.dp)
                                .padding(end = 8.dp)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            } else {
                AsyncImage(
                    model = R.drawable.logo_small,
                    contentDescription = null,
                    modifier = Modifier.size(220.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            Text("Destinacija: ${putovanje.destinacija}", style = MaterialTheme.typography.titleMedium)
            Text("Tip: ${putovanje.tipPutovanja}", style = MaterialTheme.typography.bodyMedium)
            Text("Datum: ${putovanje.datum}", style = MaterialTheme.typography.bodySmall)
            if (putovanje.prijevoz.isNotBlank()) {
                Text("Prijevoz: ${putovanje.prijevoz}", style = MaterialTheme.typography.bodyMedium)
            }
            putovanje.opis?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.obrisiPutovanje(putovanje); onDelete() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("Obriši putovanje") }
        }
    }
}
