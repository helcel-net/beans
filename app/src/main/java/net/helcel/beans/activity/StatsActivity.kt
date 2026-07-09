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
import androidx.compose.runtime.collectAsState
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
import net.helcel.beans.countries.Country
import net.helcel.beans.countries.GeoLoc
import net.helcel.beans.countries.GeoLoc.LocType
import net.helcel.beans.countries.World
import net.helcel.beans.helper.AUTO_GROUP
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.Groups
import net.helcel.beans.helper.NO_GROUP
import net.helcel.beans.helper.Settings
import net.helcel.beans.helper.Theme.getContrastColor

private val MODE_LIST = listOf(LocType.WORLD, LocType.COUNTRY, LocType.STATE)

@Composable
fun StatsScreen(
    onExit: ()-> Unit
) {
    val modes = if (Settings.isRegional(LocalContext.current)) MODE_LIST else MODE_LIST.take(2)
    var selectedTab by remember { mutableIntStateOf(0) }
    var countMode by remember { mutableStateOf(true) }


    SysTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically){
                            Text(text=stringResource(R.string.action_stat), modifier = Modifier.weight(1f))
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
    val isCascadeStats = remember { Settings.isCascadeStats(context) }
    val visits by Data.visits.visitsFlow.collectAsState()

    val continents = remember { World.WWW.children.toList() }
    val countries = remember {
        World.WWW.children.flatMap {
            if (it is Country) listOf(it) else it.children
        }
    }

    val visitedItems = remember(group, activeMode, isCascadeStats, visits) {
        val list = when (activeMode) {
            LocType.WORLD -> continents
            LocType.COUNTRY -> countries
            LocType.STATE -> countries.flatMap { it.children }
            else -> emptyList()
        }

        fun getExplicitColor(l: GeoLoc): Int? {
            val c = visits.getOrDefault(l.code, 0)
            return if (c != NO_GROUP && c != AUTO_GROUP) c else null
        }

        fun getEffectiveGroup(l: GeoLoc): Int {
            getExplicitColor(l)?.let { return it }

            if (isCascadeStats) {
                // Parent -> Child (closest explicit ancestor)
                val country = countries.find { it.children.contains(l) }
                val countryColor = country?.let { getExplicitColor(it) }
                if (countryColor != null) return countryColor

                val continent = continents.find {
                    it.children.contains(l) || (country != null && it.children.contains(country))
                }
                val continentColor = continent?.let { getExplicitColor(it) }
                if (continentColor != null) return continentColor

                val worldColor = getExplicitColor(World.WWW)
                if (worldColor != null) return worldColor

                // Child -> Parent (most common explicit descendant)
                val descendants = mutableListOf<GeoLoc>()
                fun collect(curr: GeoLoc) {
                    curr.children.forEach {
                        descendants.add(it)
                        collect(it)
                    }
                }
                collect(l)
                val descendantColors = descendants.mapNotNull { getExplicitColor(it) }
                if (descendantColors.isNotEmpty()) {
                    return descendantColors.groupBy { it }.maxByOrNull { it.value.size }?.key ?: AUTO_GROUP
                }
            } else {
                if (l.type == LocType.WORLD || l.type == LocType.COUNTRY) {
                    val descendants = mutableListOf<GeoLoc>()
                    fun collect(curr: GeoLoc) {
                        curr.children.forEach { descendants.add(it); collect(it) }
                    }
                    collect(l)
                    val dc = descendants.mapNotNull { getExplicitColor(it) }
                    if (dc.isNotEmpty()) return dc.groupBy { it }.maxByOrNull { it.value.size }?.key ?: AUTO_GROUP
                }
            }

            return AUTO_GROUP
        }

        list.filter { getEffectiveGroup(it) == group.key }
    }

    val count = visitedItems.size
    val area = visitedItems.sumOf { it.area.toLong() }

    val displayValue = if (countMode) count.toString() else stringResource(R.string.number_with_unit, area.toInt(), "km²")

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