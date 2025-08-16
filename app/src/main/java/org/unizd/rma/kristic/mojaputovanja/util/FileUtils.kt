package org.unizd.rma.kristic.mojaputovanja.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object FileUtils {

    fun copyUrisToAppStorage(context: Context, uris: List<Uri>): List<String> {
        val outDir = File(context.filesDir, "images").apply { if (!exists()) mkdirs() }
        val resolver = context.contentResolver
        val paths = mutableListOf<String>()

        for (uri in uris) {
            val ext = guessExtension(resolver, uri) ?: "jpg"
            val outFile = File(outDir, "${UUID.randomUUID()}.$ext")
            try {
                resolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(outFile).use { output ->
                        input.copyTo(output)
                    }
                }
                paths.add(outFile.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return paths
    }

    private fun guessExtension(resolver: ContentResolver, uri: Uri): String? {
        val mime = resolver.getType(uri) ?: return getNameExtension(resolver, uri)
        return when (mime) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> getNameExtension(resolver, uri)
            // fallback
        }
    }

    private fun getNameExtension(resolver: ContentResolver, uri: Uri): String? {
        return try {
            resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
                val nameIdx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (c.moveToFirst() && nameIdx >= 0) {
                    val name = c.getString(nameIdx) ?: return null
                    val dot = name.lastIndexOf('.')
                    if (dot != -1 && dot < name.length - 1) name.substring(dot + 1).lowercase() else null
                } else null
            }
        } catch (_: Exception) { null }
    }
}
