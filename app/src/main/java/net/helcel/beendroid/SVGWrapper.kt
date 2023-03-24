package net.helcel.beendroid

import android.content.Context
import java.nio.charset.StandardCharsets


class SVGWrapper(private val c: Context, private val country: Country, private var level: Level) {
    var data = ""

    fun load(): SVGWrapper {
        data = try {
            String(
                c.assets.open(country.code + "_" + level.id + ".psvg").readBytes(),
                StandardCharsets.UTF_8
            )
        }catch(e: Exception){
            ""
        }
        return this;
    }

    fun changeLevel(level: Level): SVGWrapper {
        this.level = level
        this.load()
        return this;
    }
}