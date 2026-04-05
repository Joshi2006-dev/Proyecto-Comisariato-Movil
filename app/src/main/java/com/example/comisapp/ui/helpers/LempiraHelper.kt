import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

object LempiraHelper {

    private val hondurasLocale = Locale("es", "HN")
    private val lempiraFormat = NumberFormat.getCurrencyInstance(hondurasLocale)


    fun Double.toLempira(): String =
        lempiraFormat.format(this)


    fun Double.toLempiraPlain(): String =
        lempiraFormat.format(this)
            .replace(lempiraFormat.currency?.symbol ?: "L ", "")
            .trim()

    fun Double.toLempiraFormal(): String =
        "L. ${toLempiraPlain()}"

    class LempiraVisualTransformation : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            val rawInput = text.text.replace(",", "").replace("L ", "").replace("L.", "").trim()
            val formatted = rawInput.toDoubleOrNull()?.let {
                lempiraFormat.format(it)
            } ?: rawInput

            val offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int =
                    formatted.length

                override fun transformedToOriginal(offset: Int): Int =
                    rawInput.length
            }

            return TransformedText(AnnotatedString(formatted), offsetMapping)
        }
    }
}