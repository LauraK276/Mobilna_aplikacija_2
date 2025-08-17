package org.unizd.rma.kristic.mojaputovanja.ui

import android.Manifest
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
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import org.unizd.rma.kristic.mojaputovanja.model.Putovanje
import org.unizd.rma.kristic.mojaputovanja.viewmodel.PutovanjeViewModel
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import org.unizd.rma.kristic.mojaputovanja.util.FileUtils
import androidx.compose.ui.layout.ContentScale




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


    val capturedPaths = remember { mutableStateListOf<String>() }


    val launcherSlike = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            pickedUris.clear()
            pickedUris.addAll(uris)
        }
    }


    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    var pendingPhotoPath by remember { mutableStateOf<String?>(null) }
    var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }

    fun newImageUriInAppFiles(): Uri {
        val imagesDir = File(context.filesDir, "images").apply { if (!exists()) mkdirs() }
        val file = File(imagesDir, "IMG_${System.currentTimeMillis()}.jpg")
        pendingPhotoPath = file.absolutePath
        return FileProvider.getUriForFile(
            context,
            "org.unizd.rma.kristic.mojaputovanja.provider",
            file
        ).also { pendingPhotoUri = it }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingPhotoPath != null) {
            capturedPaths.add(pendingPhotoPath!!)
        } else {
            pendingPhotoPath?.let { File(it).takeIf { f -> f.exists() }?.delete() }
        }
        // reset
        pendingPhotoPath = null
        pendingPhotoUri = null
    }

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

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded, onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = tipPutovanja, onValueChange = {},
                label = { Text("Tip putovanja") }, readOnly = true,
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                tipoviPutovanja.forEach { tip ->
                    DropdownMenuItem(text = { Text(tip) }, onClick = {
                        tipPutovanja = tip; expanded = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = datum,
            onValueChange = {},
            label = { Text("Datum") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            trailingIcon = { TextButton(onClick = { showDatePicker = true }) { Text("Odaberi") } }
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
                dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Odustani") } }
            ) { DatePicker(state = datePickerState) }
        }

        OutlinedTextField(
            value = opis,
            onValueChange = { opis = it },
            label = { Text("Opis putovanja") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            maxLines = 4
        )


        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { launcherSlike.launch("image/*") }) {
                Text("Odaberi slike")
            }
            OutlinedButton(onClick = {

                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                val uri = newImageUriInAppFiles()
                takePictureLauncher.launch(uri)
            }) {
                Text("Slikaj")
            }
        }


        val previewItems: List<Any> = run {
            val cameraUris = capturedPaths.map { path -> Uri.fromFile(File(path)) }
            pickedUris.toList() + cameraUris
        }

        Spacer(Modifier.height(12.dp))

        if (previewItems.isEmpty()) {
            Text(
                "Još nema slika…",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Pregled slika", style = MaterialTheme.typography.titleSmall)
                Text("${previewItems.size}", style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(end = 8.dp)
            ) {
                items(previewItems, key = { it.toString() }) { anyUri ->
                    AsyncImage(
                        model = anyUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(end = 8.dp)
                    )
                }
            }
        }



        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (destinacija.isNotEmpty() && prijevoz.isNotEmpty() && datum.isNotEmpty()) {
                    val savedFromGallery = FileUtils.copyUrisToAppStorage(context, pickedUris)
                    val allPaths = savedFromGallery + capturedPaths

                    val novoPutovanje = Putovanje(
                        destinacija = destinacija,
                        prijevoz = prijevoz,
                        tipPutovanja = tipPutovanja,
                        datum = datum,
                        opis = if (opis.isBlank()) null else opis,
                        slikeUri = allPaths
                    )
                    viewModel.dodajPutovanje(novoPutovanje)
                    onSave()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Spremi putovanje") }
    }
}
