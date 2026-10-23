package com.example.careconnect.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.careconnect.R
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.FileHelper.readBytes
import kotlinx.coroutines.launch

/**
 * Profilbild aus der Galerie auswählen (Photo Picker).
 * @param imageBytes neu gewähltes Bild (noch nicht hochgeladen)
 * @param imageUrl bereits gespeichertes Bild
 */
@Composable
fun ProfileImagePicker(
    imageBytes: ByteArray?,
    imageUrl: String,
    onImagePicked: (ByteArray) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            scope.launch {
                context.readBytes(uri)?.let(onImagePicked)
            }
        }
    }
    val pickImage = {
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { pickImage() },
            contentAlignment = Alignment.Center
        ) {
            when {
                imageBytes != null -> AsyncImage(
                    model = imageBytes,
                    contentDescription = stringResource(R.string.profilbild),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                imageUrl.isNotEmpty() -> AsyncImage(
                    model = imageUrl,
                    contentDescription = stringResource(R.string.profilbild),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                else -> Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        TextButton(onClick = { pickImage() }) {
            Icon(Icons.Default.Photo, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text(stringResource(R.string.bild_auswaehlen))
        }
    }
}

@Preview
@Composable
fun ProfileImagePickerPreview() {
    CareConnectTheme(darkTheme = false) {
        ProfileImagePicker(imageBytes = null, imageUrl = "", onImagePicked = {})
    }
}
