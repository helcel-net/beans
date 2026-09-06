package net.helcel.beans.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.helcel.beans.BuildConfig
import net.helcel.beans.activity.sub.EditPlaceDialog
import net.helcel.beans.activity.sub.MapPickDialog
import net.helcel.beans.activity.sub.applyDirectVisit
import net.helcel.beans.activity.sub.commitVisitDialog
import net.helcel.beans.countries.GeoLoc
import net.helcel.beans.countries.GeoLocImporter
import net.helcel.beans.countries.GeoLocTree
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.Settings
import net.helcel.beans.map.MapAssets
import net.helcel.beans.map.MapStyle
import net.helcel.beans.map.MapView
import net.helcel.beans.map.MapWorld
import net.helcel.beans.map.MapReader


class MainScreen : ComponentActivity() {

    private var world by mutableStateOf<MapWorld?>(null)
    private var loadToken = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        Settings.start(this)
        Data.loadData(this, Int.MIN_VALUE)
        GeoLocImporter.importStates(this)

        refreshProjection()

        setContent {
            SysTheme {
                // Both bars: without the navigation inset the system buttons sit on top of
                // whatever is at the bottom of a screen, such as the About button.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.primary)
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                ) {
                    AppNavHost()
                }
            }
        }
    }

    @Composable
    fun AppNavHost() {
        val navController = rememberNavController()
        NavHost(navController, startDestination = "main") {
            composable("main") { MainScreenC(world, navController) }
            composable("settings") { SettingsMainScreen { navController.navigate("main") } }
            composable("edit") { EditScreen { navController.navigate("main") } }
            composable("stats") { StatsScreen { navController.navigate("main") } }
        }
    }

    @Composable
    fun MainScreenC(world: MapWorld?, nav: NavHostController){
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
                Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                    if (world == null) LoadingMap() else MapScreen(world)
                }
            }
        }
    }

    @Composable
    fun LoadingMap() {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.primary,
                strokeWidth = 4.dp,
                modifier = Modifier.size(50.dp),
            )
        }
    }

    @Composable
    fun MapScreen(world: MapWorld) {
        val ctx = LocalContext.current
        val visits by Data.visits.visitsFlow.collectAsState()
        val groups by Data.groups.groupsFlow.collectAsState()
        val land = MaterialTheme.colors.onBackground.toArgb()
        val background = MaterialTheme.colors.background.toArgb()
        val style = remember(visits, groups, land, background) {
            MapStyle.build(ctx, land, background)
        }
        val touchRadius = Settings.getTouchRadius(ctx)

        var candidates by remember { mutableStateOf<List<GeoLoc>>(emptyList()) }
        var showColor by remember { mutableStateOf(false) }

        // A place is either applied straight away, or the colour dialog picks
        // the group for it, exactly as it does from the edit list.
        fun select(loc: GeoLoc) {
            if (!applyDirectVisit(ctx, loc)) showColor = true
        }

        if (candidates.isNotEmpty()) {
            MapPickDialog(
                candidates = candidates,
                onPick = { candidates = emptyList(); select(it) },
                onDismiss = { candidates = emptyList() },
            )
        }
        if (showColor) {
            EditPlaceDialog(false) { cleared ->
                showColor = false
                commitVisitDialog(cleared)
            }
        }

        AndroidView(
            factory = { MapView(it) },
            update = { view ->
                view.world = world
                view.style = style
                view.touchRadiusDp = touchRadius
                // Always the tree, even for a single hit: landing on a region is
                // just as often a way of reaching the country around it.
                view.onPick = { picks ->
                    val locs = picks.mapNotNull { GeoLocTree.find(it.code) }
                    if (locs.isNotEmpty()) candidates = locs
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    /** Reloads the map asset, on a worker thread since it is a few megabytes. */
    fun refreshProjection() {
        val asset = MapAssets.assetFor(this)
        val token = ++loadToken
        world = null
        Thread {
            val parsed = assets.open(asset).use { MapReader.read(it) }
            runOnUiThread { if (token == loadToken) world = parsed }
        }.start()
    }
}
