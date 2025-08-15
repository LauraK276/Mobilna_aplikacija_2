package org.unizd.rma.kristic.mojaputovanja.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.unizd.rma.kristic.mojaputovanja.model.Putovanje
import org.unizd.rma.kristic.mojaputovanja.viewmodel.PutovanjeViewModel
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import java.text.SimpleDateFormat
import java.util.*
import org.unizd.rma.kristic.mojaputovanja.util.FileUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DodajPutovanjeScreen(
    viewModel: PutovanjeViewModel,
    onSave: () -> Unit
) {
    val context = LocalContext.current

    var destinacija by remember { mutableStateOf("") }
    var prijevoz by remember { mutableStateOf("") }
    var tipPutovanja by remember { mutableStateOf("Turističko") }
    var opis by remember { mutableStateOf("") }
    val tipoviPutovanja = listOf("Turističko", "Poslovno", "Obiteljsko", "Avanturističko")

    var datum by remember { mutableStateOf("") }
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    val pickedUris = remember { mutableStateListOf<Uri>() }

    val launcherSlike = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            pickedUris.clear()
            pickedUris.addAll(uris)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {

        OutlinedTextField(
            value = destinacija, onValueChange = { destinacija = it },
            label = { Text("Destinacija") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = prijevoz, onValueChange = { prijevoz = it },
            label = { Text("Prijevoz") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = tipPutovanja, onValueChange = {},
                label = { Text("Tip putovanja") }, readOnly = true,
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                tipoviPutovanja.forEach { tip ->
                    DropdownMenuItem(
                        text = { Text(tip) },
                        onClick = { tipPutovanja = tip; expanded = false }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = datum, onValueChange = {},
            label = { Text("Datum") }, readOnly = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            trailingIcon = { TextButton({ showDatePicker = true }) { Text("Odaberi") } }
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val fmt = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                            datum = fmt.format(Date(millis))
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = { TextButton({ showDatePicker = false }) { Text("Odustani") } }
            ) { DatePicker(state = datePickerState) }
        }

        OutlinedTextField(
            value = opis, onValueChange = { opis = it },
            label = { Text("Opis putovanja") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            maxLines = 4
        )

        Button(onClick = { launcherSlike.launch("image/*") }) {
            Text("Odaberi slike")
        }

        if (pickedUris.isNotEmpty()) {
            LazyRow(Modifier.padding(top = 8.dp)) {
                items(pickedUris, key = { it.toString() }) { uri ->
                    AsyncImage(
                        model = uri, contentDescription = null,
                        modifier = Modifier.size(100.dp).padding(end = 8.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                if (destinacija.isNotEmpty() && prijevoz.isNotEmpty() && datum.isNotEmpty()) {

                    val savedPaths = FileUtils.copyUrisToAppStorage(context, pickedUris)
                    val novoPutovanje = Putovanje(
                        destinacija = destinacija,
                        prijevoz = prijevoz,
                        tipPutovanja = tipPutovanja,
                        datum = datum,
                        opis = if (opis.isBlank()) null else opis,
                        slikeUri = savedPaths   // ⬅️ spremamo LISTU putanja, ne jedan uri
                    )
                    viewModel.dodajPutovanje(novoPutovanje)
                    onSave()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Spremi putovanje") }
    }
}
