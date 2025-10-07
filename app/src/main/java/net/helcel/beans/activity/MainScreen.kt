package net.helcel.beans.activity

import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.caverock.androidsvg.RenderOptions
import com.github.chrisbanes.photoview.PhotoView
import net.helcel.beans.BuildConfig
import net.helcel.beans.countries.GeoLocImporter
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.Settings
import net.helcel.beans.svg.CSSWrapper
import net.helcel.beans.svg.SVGWrapper


class MainScreen : ComponentActivity() {

    private lateinit var psvg: SVGWrapper
    private lateinit var css: CSSWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        Settings.start(this)
        GeoLocImporter.importStates(this)
        Data.loadData(this, Int.MIN_VALUE)

        setContent {
            SysTheme {
                Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
                    AppNavHost(psvg, css)
                }
            }
        }
        refreshProjection()
    }

    @Composable
    fun AppNavHost(psvg: SVGWrapper, css: CSSWrapper) {
        val navController = rememberNavController()
        NavHost(navController, startDestination = "main") {
            composable("main") { MainScreenC(psvg,css, navController) }
            composable("settings") { SettingsMainScreen { navController.navigate("main")} }
            composable("edit") { EditScreen { navController.navigate("main") } }
            composable("stats") { StatsScreen { navController.navigate("main") } }
        }
    }

    @Composable
    fun MainScreenC(psvg: SVGWrapper,css: CSSWrapper, nav: NavHostController){
        SysTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(BuildConfig.APP_NAME) },
                        actions = {
                            IconButton(onClick = { nav.navigate("edit") }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = {  nav.navigate("stats") }){
                                Icon(Icons.Default.Percent, contentDescription = "Stats")
                            }
                            IconButton(onClick = { nav.navigate("settings") }) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings")
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    MapScreen(psvg, css)
                }
            }
        }
    }

    @Composable
    fun MapScreen(psvg: SVGWrapper, css: CSSWrapper) {
        Box {
            val opt: RenderOptions = RenderOptions.create()
            opt.css(css.get())
            val drawable = remember(psvg, css) {
                PictureDrawable(psvg.get()?.renderToPicture(opt))
            }
            AndroidView(factory = { ctx ->
                PhotoView(ctx).apply {
                    setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null)
                    setImageDrawable(drawable)
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }
            }, modifier = Modifier.fillMaxSize())
        }
    }

    fun refreshProjection() {
        psvg = SVGWrapper(this)
        css = CSSWrapper(this)
    }
}
