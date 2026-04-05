package com.example.comisapp.ui.screens.components

import LempiraHelper.toLempira
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.comisapp.data.models.ProductModel
import com.example.comisapp.data.models.settings.PlazoModel
import com.example.comisapp.ui.theme.TextPurple

val BrandPurple  = TextPurple
val SectionBg    = Color(0xFFF5F5F5)
val InfoBlueBg   = Color(0xFFEEF2FF)
private val InfoBlueIcon = Color(0xFF6366F1)
val DividerColor = Color(0xFFE0E0E0)
val BodyText     = Color(0xFF212121)
val SubtleText   = Color(0xFF757575)

enum class PaymentMode { SINGLE, INSTALLMENTS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentBottomSheetContent(
    product: ProductModel?=null,
    paymentMode: PaymentMode,
    onPaymentModeChange: (PaymentMode) -> Unit,
    quantity: Int,
    plazos: List<PlazoModel>,
    onQuantityChange: (Int) -> Unit,
    selectedMonths: Int,
    onMonthsChange: (Int) -> Unit,
    monthsMenuExpanded: Boolean,
    onMonthsMenuExpandedChange: (Boolean) -> Unit,
    totalPrice: Double,
    monthlyPayment: Double,
    isLoading: Boolean,
    isEditMode: Boolean = false,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Método de Pago", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BodyText)
        Spacer(Modifier.height(20.dp))

        PaymentModeToggle(selected = paymentMode, onSelect = onPaymentModeChange)
        Spacer(Modifier.height(20.dp))

        if (paymentMode == PaymentMode.INSTALLMENTS) {
            Text(
                "PLAZO DE FINANCIACIÓN", fontSize = 11.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 1.sp,
                color = SubtleText, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            if (plazos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = BrandPurple,
                        strokeWidth = 2.dp
                    )
                }
            }else{
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, DividerColor, RoundedCornerShape(10.dp))
                        .clickable { onMonthsMenuExpandedChange(true) }
                        .padding(horizontal = 14.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedMonths == 0) "Seleccione un plazo" else "$selectedMonths meses",
                            fontSize = 14.sp,
                            color = if (selectedMonths == 0) SubtleText else BodyText
                        )
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = SubtleText)
                    }
                    DropdownMenu(
                        expanded = monthsMenuExpanded,
                        onDismissRequest = { onMonthsMenuExpandedChange(false) }
                    ) {
                        plazos.sortedBy { it.plazoMeses }.forEach { plazo ->
                            DropdownMenuItem(
                                text = { Text("${plazo.plazoMeses} meses") },
                                onClick = {
                                    onMonthsChange(plazo.plazoMeses)
                                    onMonthsMenuExpandedChange(false)
                                }
                            )
                        }
                    }
                }
            }


            Spacer(Modifier.height(20.dp))
        }

        Text(
            "CANTIDAD", fontSize = 11.sp, fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp, color = SubtleText, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1C1C1E))
                    .clickable { onQuantityChange(quantity - 1) },
                contentAlignment = Alignment.Center
            ) {
                Text("−", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }

            Text(
                quantity.toString(), fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold, color = BodyText,
                modifier = Modifier.weight(1f), textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1C1C1E))
                    .clickable { onQuantityChange(quantity + 1) },
                contentAlignment = Alignment.Center
            ) {
                Text("+", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SectionBg)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (paymentMode == PaymentMode.INSTALLMENTS) "Deducción Mensual" else "Total a pagar",
                    fontSize = 13.sp, color = SubtleText
                )
                Spacer(Modifier.height(4.dp))
                val amount = if (paymentMode == PaymentMode.INSTALLMENTS) monthlyPayment else totalPrice
                val amountStr = "%.2f".format(amount).replace(".", ",")
                Text(
                    text = amount.toLempira(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = BodyText

                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(InfoBlueBg)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Default.Info, contentDescription = null,
                tint = InfoBlueIcon, modifier = Modifier
                    .size(18.dp)
                    .padding(top = 1.dp)
            )
            Text(
                text = if (paymentMode == PaymentMode.INSTALLMENTS)
                    "Durante los próximos $selectedMonths meses se deducirá el valor de la cuota del pago. " +
                            "La solicitud será procesada en 7 días hábiles. Por favor, esté atento a cualquier situación."
                else
                    "El valor del producto será deducido de su próximo pago. La solicitud será procesada " +
                            "en 7 días hábiles. Por favor, esté atento a cualquier situación.",
                fontSize = 12.sp, color = Color(0xFF3730A3), lineHeight = 18.sp
            )
        }

        Spacer(Modifier.height(24.dp))
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandPurple)
            }
        } else {
            ButtonComponent(text = "CONFIRMAR SOLICITUD", onClick = onConfirm)
        }    }
}

@Composable
fun PaymentModeToggle(selected: PaymentMode, onSelect: (PaymentMode) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp))
            .background(SectionBg)
            .padding(4.dp)
    ) {
        Row {
            PaymentModeTab("Pago Único", selected == PaymentMode.SINGLE, Modifier.weight(1f)) {
                onSelect(PaymentMode.SINGLE)
            }
            PaymentModeTab("Cuotas", selected == PaymentMode.INSTALLMENTS, Modifier.weight(1f)) {
                onSelect(PaymentMode.INSTALLMENTS)
            }
        }
    }
}

@Composable
fun PaymentModeTab(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Transparent,
        animationSpec = tween(200), label = "tab_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) BrandPurple else SubtleText,
        animationSpec = tween(200), label = "tab_text"
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label, fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor
        )
    }
}
