package org.unizd.rma.kristic.mojaputovanja.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.unizd.rma.kristic.mojaputovanja.R
import org.unizd.rma.kristic.mojaputovanja.model.Putovanje
import org.unizd.rma.kristic.mojaputovanja.viewmodel.PutovanjeViewModel
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.graphics.Color



@Composable
fun PutovanjeScreen(
    viewModel: PutovanjeViewModel,
    onDodajClick: () -> Unit,
    onOpenDetalji: (Int) -> Unit
) {
    val putovanja by viewModel.svaPutovanja.collectAsState()

    Scaffold(
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = onDodajClick,
                containerColor = Color(0xFF2196F3) // plava
            ) {
                Text(
                    "Dodaj uspomenu",
                    color = Color.White
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                AsyncImage(
                    model = R.drawable.logo_slika,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f) // omjer 1:1 – kvadrat
                )
                Spacer(Modifier.height(12.dp))
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Destinacija: ${putovanje.destinacija}", style = MaterialTheme.typography.bodyLarge)
                Text("Tip: ${putovanje.tipPutovanja}", style = MaterialTheme.typography.bodyMedium)
                Text("Datum: ${putovanje.datum}", style = MaterialTheme.typography.bodySmall)
            }
            Image(
                painter = painterResource(R.drawable.logo_small),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )

        }
    }
}
