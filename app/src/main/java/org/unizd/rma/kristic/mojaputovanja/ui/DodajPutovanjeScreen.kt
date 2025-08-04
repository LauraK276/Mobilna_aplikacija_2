package org.unizd.rma.kristic.mojaputovanja.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.unizd.rma.kristic.mojaputovanja.model.Putovanje
import org.unizd.rma.kristic.mojaputovanja.viewmodel.PutovanjeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DodajPutovanjeScreen(
    viewModel: PutovanjeViewModel,
    onSave: () -> Unit
) {
    var destinacija by remember { mutableStateOf("") }
    var prijevoz by remember { mutableStateOf("") }
    var tipPutovanja by remember { mutableStateOf("Turističko") }
    var datum by remember { mutableStateOf("") }

    val tipoviPutovanja = listOf("Turističko", "Poslovno", "Obiteljsko", "Avanturističko")

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = destinacija,
            onValueChange = { destinacija = it },
            label = { Text("Destinacija") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = prijevoz,
            onValueChange = { prijevoz = it },
            label = { Text("Prijevoz") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        // Dropdown za tip putovanja
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = tipPutovanja,
                onValueChange = {},
                label = { Text("Tip putovanja") },
                readOnly = true,
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                tipoviPutovanja.forEach { tip ->
                    DropdownMenuItem(
                        text = { Text(tip) },
                        onClick = {
                            tipPutovanja = tip
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = datum,
            onValueChange = { datum = it },
            label = { Text("Datum (dd.MM.yyyy)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                if (destinacija.isNotEmpty() && prijevoz.isNotEmpty() && datum.isNotEmpty()) {
                    val novoPutovanje = Putovanje(
                        destinacija = destinacija,
                        prijevoz = prijevoz,
                        tipPutovanja = tipPutovanja,
                        datum = datum,
                        slikaUri = "" // sliku ćemo dodati kasnije
                    )
                    viewModel.dodajPutovanje(novoPutovanje)
                    onSave()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Spremi putovanje")
        }
    }
}
