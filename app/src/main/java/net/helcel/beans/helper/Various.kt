package net.helcel.beans.helper

import android.graphics.drawable.ColorDrawable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ColorDrawableSerializer : KSerializer<ColorDrawable> {
    override val descriptor = PrimitiveSerialDescriptor("ColorDrawable", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): ColorDrawable {
        return ColorDrawable(decoder.decodeInt())
    }

    override fun serialize(encoder: Encoder, value: ColorDrawable) {
        encoder.encodeInt(value.color)
    }
}