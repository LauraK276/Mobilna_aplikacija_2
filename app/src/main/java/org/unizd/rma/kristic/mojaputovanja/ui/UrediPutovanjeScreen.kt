package org.unizd.rma.kristic.mojaputovanja.ui

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import org.unizd.rma.kristic.mojaputovanja.model.Putovanje
import org.unizd.rma.kristic.mojaputovanja.util.FileUtils
import org.unizd.rma.kristic.mojaputovanja.viewmodel.PutovanjeViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


private sealed class PreviewItem {
    data class Existing(val path: String) : PreviewItem() // već spremljeno
    data class Picked(val uri: Uri) : PreviewItem()       // iz galerije
    data class Captured(val path: String) : PreviewItem() // s kamere
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrediPutovanjeScreen(
    id: Int,
    viewModel: PutovanjeViewModel,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val flow = remember(id) { viewModel.getPutovanjeState(id) }
    val putovanje = flow.collectAsState().value

    if (putovanje == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var destinacija by remember(putovanje.id) { mutableStateOf(putovanje.destinacija) }
    var prijevoz by remember(putovanje.id) { mutableStateOf(putovanje.prijevoz) }
    var tipPutovanja by remember(putovanje.id) { mutableStateOf(putovanje.tipPutovanja) }
    var datum by remember(putovanje.id) { mutableStateOf(putovanje.datum) }
    var opis by remember(putovanje.id) { mutableStateOf(putovanje.opis ?: "") }

    val tipoviPutovanja = listOf("Turističko", "Poslovno", "Obiteljsko", "Avanturističko")


    val postojecePutanje = remember(putovanje.id) {
        mutableStateListOf<String>().apply { addAll(putovanje.slikeUri) }
    }


    val pickedUris = remember { mutableStateListOf<Uri>() }
    val capturedPaths = remember { mutableStateListOf<String>() }


    val pickImages = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            pickedUris.clear()
            pickedUris.addAll(uris)
        }
    }


    val cameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    var pendingPhotoPath by remember { mutableStateOf<String?>(null) }

    fun newCameraUri(): Uri {
        val dir = File(context.filesDir, "images").apply { if (!exists()) mkdirs() }
        val f = File(dir, "IMG_${System.currentTimeMillis()}.jpg")
        pendingPhotoPath = f.absolutePath
        return FileProvider.getUriForFile(context, "org.unizd.rma.kristic.mojaputovanja.provider", f)
    }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && pendingPhotoPath != null) capturedPaths.add(pendingPhotoPath!!)
        else pendingPhotoPath?.let { File(it).takeIf(File::exists)?.delete() }
        pendingPhotoPath = null
    }

    val dateState = rememberDatePickerState()
    var showDate by remember { mutableStateOf(false) }

    Column(Modifier.padding(16.dp)) {

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
                    DropdownMenuItem(text = { Text(tip) }, onClick = { tipPutovanja = tip; expanded = false })
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = datum, onValueChange = {},
            label = { Text("Datum") }, readOnly = true,
            trailingIcon = { TextButton({ showDate = true }) { Text("Odaberi") } },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        if (showDate) {
            DatePickerDialog(
                onDismissRequest = { showDate = false },
                confirmButton = {
                    TextButton(onClick = {
                        dateState.selectedDateMillis?.let {
                            datum = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(it))
                        }
                        showDate = false
                    }) { Text("OK") }
                },
                dismissButton = { TextButton({ showDate = false }) { Text("Odustani") } }
            ) { DatePicker(state = dateState) }
        }

        OutlinedTextField(
            value = opis, onValueChange = { opis = it },
            label = { Text("Opis putovanja") },
            maxLines = 4,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { pickImages.launch("image/*") }) { Text("Dodaj slike") }
            OutlinedButton(onClick = {
                cameraPermission.launch(Manifest.permission.CAMERA)
                takePicture.launch(newCameraUri())
            }) { Text("Slikaj") }
        }


        val previewItems: List<PreviewItem> = run {
            val existing = postojecePutanje.map { PreviewItem.Existing(it) }
            val picked   = pickedUris.map { PreviewItem.Picked(it) }
            val captured = capturedPaths.map { PreviewItem.Captured(it) }
            existing + picked + captured
        }

        Spacer(Modifier.height(12.dp))
        if (previewItems.isEmpty()) {
            Text("Još nema slika…", style = MaterialTheme.typography.bodySmall)
        } else {
            LazyRow(contentPadding = PaddingValues(end = 8.dp)) {
                itemsIndexed(previewItems, key = { index, item -> "${index}_${item.toString()}" }) { _, item ->
                    val model: Any = when (item) {
                        is PreviewItem.Existing -> Uri.fromFile(File(item.path))
                        is PreviewItem.Picked   -> item.uri
                        is PreviewItem.Captured -> Uri.fromFile(File(item.path))
                    }

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(end = 8.dp)
                    ) {
                        AsyncImage(
                            model = model,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )

                        TextButton(
                            onClick = {
                                when (item) {
                                    is PreviewItem.Existing -> {
                                        postojecePutanje.remove(item.path)
                                    }
                                    is PreviewItem.Picked -> pickedUris.remove(item.uri)
                                    is PreviewItem.Captured -> {
                                        capturedPaths.remove(item.path)
                                        try { File(item.path).delete() } catch (_: Exception) {}
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.TopEnd),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                            colors = ButtonDefaults.textButtonColors()
                        ) { Text("×") }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (destinacija.isNotBlank() && datum.isNotBlank() && prijevoz.isNotBlank()) {

                    val savedFromGallery = FileUtils.copyUrisToAppStorage(context, pickedUris)

                    val finalSlike = postojecePutanje.toList() + savedFromGallery + capturedPaths.toList()

                    val azurirano = putovanje.copy(
                        destinacija = destinacija,
                        prijevoz = prijevoz,
                        tipPutovanja = tipPutovanja,
                        datum = datum,
                        opis = if (opis.isBlank()) null else opis,
                        slikeUri = finalSlike
                    )
                    viewModel.azurirajPutovanje(azurirano)
                    onSaved()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Spremi izmjene") }

        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Odustani") }
    }
}
