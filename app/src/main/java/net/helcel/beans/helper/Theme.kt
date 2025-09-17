package net.helcel.beans.helper

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.graphics.ColorUtils
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import androidx.core.graphics.drawable.toDrawable

object Theme {

    fun colorToHex6(c: ColorDrawable): String {
        return '#' + colorToHex8(c).substring(3)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun colorToHex8(c: ColorDrawable): String {
        return '#' + c.color.toHexString()
    }

    fun getContrastColor(color: Int): Int {
        val whiteContrast = ColorUtils.calculateContrast(Color.WHITE, color)
        val blackContrast = ColorUtils.calculateContrast(Color.BLACK, color)
        return if (whiteContrast > blackContrast) Color.WHITE else Color.BLACK
    }

    object ColorDrawableSerializer : KSerializer<ColorDrawable> {
        override val descriptor = PrimitiveSerialDescriptor("ColorDrawable", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ColorDrawable {
            return decoder.decodeInt().toDrawable()
        }

        override fun serialize(encoder: Encoder, value: ColorDrawable) {
            encoder.encodeInt(value.color)
        }
    }

}
