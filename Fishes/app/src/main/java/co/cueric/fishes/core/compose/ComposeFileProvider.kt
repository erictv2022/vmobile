package co.cueric.fishes.core.compose

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/**
 * File Provider for media storage
 */
class ComposeFileProvider : FileProvider() {
    companion object {
        /**
         * Get the image from local storage
         *
         * @param context UI context
         * @return
         */
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory,
            )
            val authority = context.packageName + ".core.compose.ComposeFileProvider"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }
}