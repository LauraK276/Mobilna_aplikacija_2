package org.unizd.rma.kristic.mojaputovanja.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.unizd.rma.kristic.mojaputovanja.R
import org.unizd.rma.kristic.mojaputovanja.model.Putovanje
import org.unizd.rma.kristic.mojaputovanja.viewmodel.PutovanjeViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetaljiPutovanjaScreen(
    id: Int,
    viewModel: PutovanjeViewModel,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit = {}    // <- proslijedit ćemo iz NavHost-a
) {

    val flow = remember(id) { viewModel.getPutovanjeState(id) }
    val putovanjeState = flow.collectAsState(initial = null)
    val putovanje = putovanjeState.value

    var hasFirstEmission by remember { mutableStateOf(false) }
    LaunchedEffect(putovanje) { if (!hasFirstEmission) hasFirstEmission = true }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalji") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Natrag") } }
                // NEMA više actions; "Uredi" ide dolje uz "Obriši"
            )
        }
    ) { padding ->

        if (!hasFirstEmission) {
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        if (putovanje == null) {
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { Text("Nema podataka.") }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            val slike = putovanje.slikeUri
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                if (slike.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(end = 8.dp)
                    ) {
                        items(slike, key = { it }) { path ->
                            val fileOrPlaceholder = remember(path) {
                                val f = File(path)
                                if (f.exists()) f else R.drawable.logo_small
                            }
                            val request = remember(fileOrPlaceholder) {
                                ImageRequest.Builder(context)
                                    .data(fileOrPlaceholder)
                                    .crossfade(true)
                                    .build()
                            }
                            AsyncImage(
                                model = request,
                                placeholder = painterResource(R.drawable.logo_small),
                                error = painterResource(R.drawable.logo_small),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .fillMaxHeight()
                                    .width(220.dp)
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = R.drawable.logo_small,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Nema slika", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))


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
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Obriši putovanje") }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onEdit, // -> ide na ekran za uređivanje
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Uredi putovanje") }
        }
    }
}
