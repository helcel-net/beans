package net.helcel.beans.activity

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Colors
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.preference.PreferenceManager
import net.helcel.beans.R
import net.helcel.beans.countries.GeoLocImporter
import net.helcel.beans.helper.Settings
import androidx.core.content.edit
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.helcel.beans.activity.sub.AboutScreen
import net.helcel.beans.activity.sub.EditPlaceColorDialog
import net.helcel.beans.activity.sub.EditPlaceDialog
import net.helcel.beans.activity.sub.LicenseScreen
import net.helcel.beans.helper.Data

@Composable
fun SysTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val themeKey = prefs.getString(stringResource(R.string.key_theme), stringResource(R.string.system))
    val darkTheme = when (themeKey) {
        stringResource(R.string.system) -> isSystemInDarkTheme()
        stringResource(R.string.light) -> false
        stringResource(R.string.dark) -> true
        else -> isSystemInDarkTheme()
    }
    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if(darkTheme) dynamicDarkColorScheme(LocalContext.current ) else dynamicLightColorScheme(LocalContext.current )
    } else {
        if(darkTheme) darkColorScheme() else lightColorScheme()
    }
    val m2colors = Colors(
        primary = colorScheme.primary,
        primaryVariant = colorScheme.primaryContainer,
        secondary = colorScheme.secondary,
        background = colorScheme.background,
        surface = colorScheme.surface,
        onPrimary = colorScheme.onPrimary,
        onSecondary = colorScheme.onSecondary,
        onBackground = colorScheme.onBackground,
        onSurface = colorScheme.onSurface,
        secondaryVariant = colorScheme.secondary,
        error = colorScheme.error,
        onError = colorScheme.onError,
        isLight = !darkTheme,
    )

    MaterialTheme(
        colors = m2colors,
        content = content
    )
}


@Composable
fun settingsNav(): NavHostController {
    val navController = rememberNavController()
    NavHost(navController, startDestination= "settings"){
        composable("settings"){SettingsScreen(navController)}
        composable("licenses"){ LicenseScreen() }
        composable("about"){ AboutScreen() }
    }
    return navController
}

@Preview
@Composable
fun SettingsMainScreen(onExit: ()->Unit = {}) {
    val nav: NavHostController = settingsNav()
    SysTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.action_settings)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            if(!nav.popBackStack())
                                onExit()
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun SettingsScreen(navController: NavHostController = settingsNav()) {
    val context = LocalContext.current
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    var showEdit by remember { mutableStateOf(false) }

    var theme by remember { mutableStateOf(prefs.getString(context.getString(R.string.key_theme), context.getString(R.string.system))!!) }
    var projection by remember { mutableStateOf(prefs.getString(context.getString(R.string.key_projection), "default")!!) }
    var groups by remember { mutableStateOf(prefs.getString(context.getString(R.string.key_group), context.getString(R.string.off))!!) }

        if(showEdit)
            EditPlaceDialog(true) {
                showEdit = false
                val g = Data.selected_group
                if (it && g != null)
                    Data.visits.reassignAllVisitedToGroup(g.key)
            }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colors.background)
        ) {
            item {
                Text(
                    "Theme", style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onBackground,
                )
                MultiPreference(arrayOf(stringResource(R.string.system),stringResource(R.string.light),stringResource(R.string.dark)), theme) { newTheme ->
                    theme = newTheme
                    prefs.edit { putString(context.getString(R.string.key_theme), newTheme) }
                }
                HorizontalDivider()
            }
            item {
                Text(
                    "Map Projection",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(top = 16.dp)
                )
                MultiPreference(arrayOf(stringResource(R.string.mercator), stringResource(R.string.azimuthalequidistant)), projection) { newProj ->
                    projection = newProj
                    prefs.edit { putString(context.getString(R.string.key_projection), newProj) }
                    Settings.refreshProjection()
                }
                HorizontalDivider()
            }
            item {
                Text(
                    "Groups",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(top = 16.dp)
                )
                var showDialog by remember{mutableStateOf(false)}
                if(showDialog){
                    EditPlaceColorDialog(
                        deleteMode = true,
                        onDismiss = {
                            val g = Data.selected_group
                            if (g != null)
                                Data.visits.reassignAllVisitedToGroup(g.key)
                            showDialog = false
                        })
                }
                MultiPreference(
                    arrayOf(stringResource(R.string.on), stringResource(R.string.off)),
                    groups
                ) { key ->
                    if (key == context.getString(R.string.off)) {
                        showDialog=true
                    }
                    groups = key
                    prefs.edit { putString(context.getString(R.string.key_group), key) }
                }
                HorizontalDivider()
            }
            item {
                Text(
                    text = "Regional",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .clickable(onClick = {}),
                )
                RegionalScreen()
                HorizontalDivider()
            }
            item{
                val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument(),
                onResult = { uri -> Data.doImport(context, uri)   }
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = {
                        launcher.launch(arrayOf("*/*"))
                    }, modifier = Modifier
                        .fillMaxWidth(fraction = 0.4f)
                        .padding(vertical = 8.dp)) {
                        Text("Import")
                    }
                    Spacer(
                        modifier = Modifier.fillMaxWidth(0.4f)
                    )
                    Button(onClick = {
                        Data.doExport(context)
                    }, modifier = Modifier
                        .fillMaxWidth(fraction = 1f)
                        .padding(vertical = 8.dp)) {
                        Text("Export")
                    }
                }
                HorizontalDivider()
            }
            item {
                PreferenceButton("Licenses") {
                    if (navController.currentDestination?.route != "licenses")
                        navController.navigate("licenses")
                }
                PreferenceButton("About") {
                    if (navController.currentDestination?.route != "about")
                        navController.navigate("about")
                }
            }
        }
    }

@Composable
fun RegionalScreen() {
    val context = LocalContext.current
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    var selected by remember {  mutableStateOf(prefs.getString(context.getString(R.string.key_regional),context.getString(R.string.off))!!)}
    var regional by remember{ mutableStateOf(prefs.getString(context.getString(R.string.key_regional), context.getString(R.string.off))!!)}
    var showDialog by remember{mutableStateOf(false)}
    var showLoad by remember{mutableStateOf(false)}

    if(showDialog)
        Dialog(
            content = {
                Column(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colors.background,
                            RoundedCornerShape(corner = CornerSize(16.dp))
                        )
                        .padding(16.dp),){
                Text(style=MaterialTheme.typography.caption, text=  stringResource(R.string.delete_regions))
                    Button(onClick = {
                        GeoLocImporter.clearStates()
                        regional= selected
                        prefs.edit {
                            putString(
                                context.getString(R.string.key_regional),
                                regional
                            )
                        }
                        showDialog=false
                    }){
                       Text(stringResource(R.string.ok))
                    }
                }
            },
            onDismissRequest = { showDialog=false }
        )
    if(showLoad){
        Dialog(
            content = {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.primary,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(50.dp)
                )
            },
            onDismissRequest = {}
        )
    }
    val scope = rememberCoroutineScope()
    MultiPreference(arrayOf(stringResource(R.string.on),stringResource(R.string.off)),regional) { key ->
                when (key) {
                    context.getString(R.string.off) -> { showDialog=true
                        selected=key
                    }
                    context.getString(R.string.on) -> {
                        regional = key
                        prefs.edit { putString(context.getString(R.string.key_regional), key) }
                        showLoad=true
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                GeoLocImporter.importStates(context, true)
                            }
                            showLoad = false
                        }
                    }
                }
            }
}


@Composable
fun MultiPreference(list: Array<String>, selected: String, onSelected: (String) -> Unit) {
    Column(Modifier.padding(2.dp)) {
        list.map { value ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clickable { onSelected(value) }) {
                RadioButton(selected = selected == value, onClick = { onSelected(value) })
                Text(
                    value, modifier = Modifier.padding(start = 8.dp),
                    color = MaterialTheme.colors.onBackground,
                )
            }
        }
    }
}

@Composable
fun PreferenceButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Text(text)
    }
}