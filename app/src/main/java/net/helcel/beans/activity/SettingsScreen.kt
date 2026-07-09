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
import androidx.core.content.edit
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.helcel.beans.R
import net.helcel.beans.activity.sub.AboutScreen
import net.helcel.beans.activity.sub.EditPlaceColorDialog
import net.helcel.beans.activity.sub.LicenseScreen
import net.helcel.beans.countries.GeoLocImporter
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.Settings

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
    val keyTheme = stringResource(R.string.key_theme)
    val defaultTheme = stringResource(R.string.system)
    val keyProjection = stringResource(R.string.key_projection)
    val keyGroup = stringResource(R.string.key_group)
    val keyRegional = stringResource(R.string.key_regional)
    val keyRegionalStats = stringResource(R.string.key_regional_stats)
    val offString = stringResource(R.string.off)
    val onString = stringResource(R.string.on)

    var theme by remember { mutableStateOf(prefs.getString(keyTheme, defaultTheme)!!) }
    var projection by remember { mutableStateOf(prefs.getString(keyProjection, "default")!!) }
    var groups by remember { mutableStateOf(prefs.getString(keyGroup, offString)!!) }
    var regional by remember { mutableStateOf(prefs.getString(keyRegional, offString)!!) }
    var regionalStats by remember { mutableStateOf(prefs.getString(keyRegionalStats, offString)!!) }

    var showGroupDialog by remember { mutableStateOf(false) }
    var showRegionalDialog by remember { mutableStateOf(false) }
    var showLoad by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (showGroupDialog) {
        EditPlaceColorDialog(
            deleteMode = true,
            onDismiss = {
                val g = Data.selected_group
                if (g != null) {
                    Data.visits.reassignAllVisitedToGroup(g.key)
                    Data.saveData()
                }
                showGroupDialog = false
            })
    }

    if (showRegionalDialog) {
        Dialog(
            content = {
                Column(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colors.background,
                            RoundedCornerShape(corner = CornerSize(16.dp))
                        )
                        .padding(16.dp),
                ) {
                    Text(
                        style = MaterialTheme.typography.caption,
                        text = stringResource(R.string.delete_regions)
                    )
                    Button(onClick = {
                        GeoLocImporter.clearStates()
                        regional = offString
                        prefs.edit { putString(keyRegional, offString) }
                        showRegionalDialog = false
                    }) {
                        Text(stringResource(R.string.ok))
                    }
                }
            },
            onDismissRequest = { showRegionalDialog = false }
        )
    }

    if (showLoad) {
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colors.background)
    ) {
        item {
            Text(
                stringResource(R.string.key_theme),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onBackground,
            )
            MultiPreference(
                arrayOf(
                    stringResource(R.string.system),
                    stringResource(R.string.light),
                    stringResource(R.string.dark)
                ), theme
            ) { newTheme ->
                theme = newTheme
                prefs.edit { putString(keyTheme, newTheme) }
            }
            HorizontalDivider()
        }
        item {
            Text(
                stringResource(R.string.key_projection),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(top = 16.dp)
            )
            MultiPreference(
                arrayOf(stringResource(R.string.mercator), stringResource(R.string.azimuthalequidistant)),
                projection
            ) { newProj ->
                projection = newProj
                prefs.edit { putString(keyProjection, newProj) }
                Settings.refreshProjection()
            }
            HorizontalDivider()
        }
        item {
            SettingSwitch(
                label = stringResource(R.string.key_group),
                subtitle = stringResource(R.string.key_group_desc),
                isChecked = groups == onString,
                onCheckedChange = { isChecked ->
                    if (!isChecked) {
                        showGroupDialog = true
                    } else {
                        groups = onString
                        prefs.edit { putString(keyGroup, onString) }
                    }
                }
            )
            HorizontalDivider()
        }
        item {
            SettingSwitch(
                label = stringResource(R.string.key_regional),
                subtitle = stringResource(R.string.key_regional_desc),
                isChecked = regional == onString,
                onCheckedChange = { isChecked ->
                    if (isChecked) {
                        regional = onString
                        prefs.edit { putString(keyRegional, onString) }
                        showLoad = true
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                GeoLocImporter.importStates(context, true)
                            }
                            showLoad = false
                        }
                    } else {
                        showRegionalDialog = true
                    }
                }
            )
            if (regional == onString) {
                SettingSwitch(
                    label = stringResource(R.string.pref_regional_stats),
                    subtitle = stringResource(R.string.pref_regional_stats_desc),
                    isChecked = regionalStats == onString,
                    onCheckedChange = { isChecked ->
                        regionalStats = if (isChecked) onString else offString
                        prefs.edit { putString(keyRegionalStats, regionalStats) }
                    }
                )
            }
            HorizontalDivider()
        }
        item {
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument(),
                onResult = { uri -> Data.doImport(context, uri) }
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Button(
                    onClick = {
                        launcher.launch(arrayOf("*/*"))
                    }, modifier = Modifier
                        .weight(1f)
                ) {
                    Text(stringResource(R.string.action_import))
                }
                Spacer(modifier = Modifier.size(16.dp))
                Button(
                    onClick = {
                        Data.doExport(context)
                    }, modifier = Modifier
                        .weight(1f)
                ) {
                    Text(stringResource(R.string.action_export))
                }
            }
            HorizontalDivider()
        }
        item {
            PreferenceButton(stringResource(R.string.licenses)) {
                if (navController.currentDestination?.route != "licenses")
                    navController.navigate("licenses")
            }
            PreferenceButton(stringResource(R.string.about)) {
                if (navController.currentDestination?.route != "about")
                    navController.navigate("about")
            }
        }
    }
}

@Composable
fun MultiPreference(list: Array<String>, selected: String, onSelected: (String) -> Unit) {
    Column(Modifier.padding(vertical = 2.dp)) {
        list.forEach { value ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clickable { onSelected(value) }) {
                RadioButton(selected = selected == value, onClick = { onSelected(value) })
                Text(
                    value, modifier = Modifier.padding(start = 12.dp),
                    style = MaterialTheme.typography.body1,
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

@Composable
fun SettingSwitch(
    label: String,
    subtitle: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text=label,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onBackground,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        androidx.compose.material.Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}
