package com.example.comisapp.ui.screens.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.comisapp.data.models.credits.CreditModel
import com.example.comisapp.ui.helpers.toFormattedDate
import com.example.comisapp.ui.theme.TextPurple
import com.example.comisapp.ui.screens.components.ButtonComponent
import com.example.comisapp.ui.theme.ApbGreen
import com.example.comisapp.ui.theme.CardBorderColor
import com.example.comisapp.ui.theme.RpbRed
import java.util.Locale
import java.util.Locale.getDefault

import com.example.comisapp.ui.screens.secondaryNavigationScreens.ejecutarBiometria

@Composable
fun CreditProgressCard(
    credito: CreditModel,
    onDetailClick: (String) -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = remember(context) {
        var currentContext = context
        while (currentContext is android.content.ContextWrapper) {
            if (currentContext is FragmentActivity) break
            currentContext = currentContext.baseContext
        }
        currentContext as? FragmentActivity
    }

    val estadoColor: Color = when (credito.estado) {
        "Aprobado"  -> ApbGreen
        "Rechazado" -> RpbRed
        "Liquidado" -> TextPurple
        "Cancelado" -> Color(0xFFFF6B00)
        else        -> Color.Black
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, CardBorderColor.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(credito.imagenProductoURL).crossfade(true).build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF5F5F5))
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = credito.nombreProducto, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(text = "Emitido: ${credito.fechaRegistro?.toFormattedDate()}", fontSize = 12.sp, color = Color.Gray)
                }
                Surface(color = estadoColor.copy(alpha = 0.15f), shape = RoundedCornerShape(20.dp)) {
                    Text(text = credito.estado.uppercase(), modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = estadoColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            when (credito.estado) {
                "Aprobado", "Liquidado" -> CreditBodyProgress(credito)
                "Pendiente"             -> CreditBodyPending()
                "Rechazado"             -> CreditBodyRejected()
                "Cancelado"             -> CreditBodyCancelled()
            }

            Spacer(Modifier.height(16.dp))

            ButtonComponent(
                text = if (credito.estado.equals("Pendiente", ignoreCase = true)) "CANCELAR SOLICITUD" else "VER DETALLES",
                onClick = {
                    if (credito.estado.equals("Pendiente", ignoreCase = true)) {
                        activity?.let { act ->
                            ejecutarBiometria(
                                activity = act,
                                subtitulo = "Autentícate para cancelar tu pedido"
                            ) {
                                onCancelClick()
                            }
                        }
                    } else {
                        onDetailClick(credito.id)
                    }
                }
            )
        }
    }
}

@Composable
private fun CreditBodyProgress(credito: CreditModel) {
    val progress = if (credito.plazoMeses > 0)
        credito.cuotasPagadas.toFloat() / credito.plazoMeses.toFloat()
    else 0f

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Progreso de pago",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF474747)
            )
        )
        Text(
            "${credito.cuotasPagadas}/${credito.plazoMeses} cuotas",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF474747)
            )
        )
    }
    Spacer(Modifier.height(8.dp))
    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape),
        color = if (credito.estado == "Liquidado") ApbGreen else TextPurple,
        trackColor = if (credito.estado == "Liquidado")
            ApbGreen.copy(alpha = 0.1f) else TextPurple.copy(alpha = 0.1f),
        strokeCap = StrokeCap.Round
    )
}


@Composable
private fun CreditBodyPending() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = Color(0xFF757575),
            modifier = Modifier
                .size(18.dp)
                .padding(top = 1.dp)
        )
        Text(
            text = "Tu solicitud está siendo revisada por nuestro equipo. " +
                    "Recibirás una respuesta en los siguientes 7 días hábiles " +
                    "después de la fecha de emisión.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF474747),
                lineHeight = 18.sp
            )
        )
    }
}

@Composable
private fun CreditBodyRejected() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(RpbRed.copy(alpha = 0.08f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = RpbRed,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 1.dp)
        )
        Text(
            text = "No cumple con los requisitos mínimos para optar por este crédito.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = RpbRed,
                lineHeight = 18.sp
            )
        )
    }
}

@Composable
private fun CreditBodyCancelled() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFF6B00).copy(alpha = 0.08f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Cancel,
            contentDescription = null,
            tint = Color(0xFFFF6B00),
            modifier = Modifier
                .size(18.dp)
                .padding(top = 1.dp)
        )
        Text(
            text = "Este crédito fue cancelado y ya no se encuentra activo.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFFFF6B00),
                lineHeight = 18.sp
            )
        )
    }
}