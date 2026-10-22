package duoc.appmoviles.components

import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

// Muestra una imagen circular a partir de su Uri.
// Si no hay imagen, muestra un ícono de perfil por defecto.
// Crossfade hace una transición suave cuando la imagen cambia.
@Composable
fun ImagenInteligente(
    uri: Uri?,
    modifier: Modifier = Modifier,
    tamano: Dp = 120.dp
) {
    Crossfade(targetState = uri, label = "imagenPerfil") { imagenActual ->
        if (imagenActual != null) {
            // Hay foto: Coil la carga desde la Uri y la recorta en círculo
            AsyncImage(
                model = imagenActual,
                contentDescription = "Foto del perfil",
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(tamano)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
        } else {
            // Sin foto: círculo gris con ícono de persona
            Box(
                modifier = modifier
                    .size(tamano)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Sin foto",
                    modifier = Modifier.size(tamano / 2),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}