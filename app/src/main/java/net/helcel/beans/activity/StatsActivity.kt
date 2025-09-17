package net.helcel.beans.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.helcel.beans.R
import net.helcel.beans.countries.GeoLoc.LocType
import net.helcel.beans.countries.World
import net.helcel.beans.helper.AUTO_GROUP
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.Groups
import net.helcel.beans.helper.Settings.isRegional
import net.helcel.beans.helper.Theme.getContrastColor

private val MODE_LIST = listOf(LocType.WORLD, LocType.COUNTRY, LocType.STATE)

@Composable
fun StatsScreen(
    onExit: ()-> Unit
) {
    val modes = if (isRegional(LocalContext.current)) MODE_LIST else MODE_LIST.take(2)
    var selectedTab by remember { mutableIntStateOf(0) }
    var countMode by remember { mutableStateOf(true) }


    SysTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically){
                            Text(text=stringResource(R.string.action_edit), modifier = Modifier.weight(1f))
                            Button(onClick = { countMode = !countMode }) {
                                Text(if (countMode) "Count" else "Area")
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onExit) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },

                )
            },
        ) { padding ->
            Column(Modifier.padding(padding)) {
                TabRow(selectedTabIndex = selectedTab) {
                    modes.forEachIndexed { index, mode ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(mode.txt) }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                }

                val activeMode = modes.getOrNull(selectedTab) ?: LocType.WORLD
                StatsList(activeMode, countMode)
            }
        }
    }
}

@Composable
fun StatsList(activeMode: LocType, countMode: Boolean) {
    val groups = remember { Data.groups.groupsFlow }
    val unCat = stringResource(R.string.uncategorized)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(groups.value + listOf(Groups.Group(AUTO_GROUP, unCat))) { group ->
            StatsRow(group, activeMode, countMode)
        }
    }
}

@Composable
fun StatsRow(group: Groups.Group, activeMode: LocType, countMode: Boolean) {
    val context = LocalContext.current

    val visited = remember(group, activeMode) {
        Data.visits.getVisitedByValue(group.key)
    }

    val count = when (activeMode) {
        LocType.WORLD -> World.WWW.children.filter { it.code in visited }.size
        LocType.COUNTRY -> World.WWW.children.flatMap { it.children.filter { c -> c.code in visited } }.size
        LocType.STATE -> World.WWW.children.flatMap { itc->itc.children.flatMap { it.children.filter { it.code in visited } } }.size
        else -> 0
    }

    val area = when (activeMode) {
        LocType.WORLD -> World.WWW.children.filter { it.code in visited }.sumOf { it.area }
        LocType.COUNTRY -> World.WWW.children.flatMap { it.children.filter { c -> c.code in visited } }.sumOf { it.area }
        LocType.STATE -> World.WWW.children.flatMap { it.children.flatMap { it.children.filter { it.code in visited } } }.sumOf { it.area }
        else -> 0
    }

    val displayValue = if (countMode) count.toString() else context.getString(R.string.number_with_unit, area, "km²")

    val backgroundColor = group.color.color
    val textColor = getContrastColor(backgroundColor)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(backgroundColor))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text=group.name,
            modifier= Modifier.weight(1f),
            color = Color(textColor)
        )
        Text(text=displayValue,
            color = Color(textColor)
        )
    }
}