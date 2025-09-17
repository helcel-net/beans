package net.helcel.beans.activity.sub


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.helcel.beans.countries.GeoLoc
import net.helcel.beans.countries.Group
import net.helcel.beans.countries.World
import net.helcel.beans.helper.AUTO_GROUP
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.NO_GROUP
import net.helcel.beans.helper.Settings
import kotlin.math.min

@Preview
@Composable
fun EditPlaceScreenPreview(){
    EditPlaceScreen(Group.EEE)
}

fun syncVisited(loc: GeoLoc?=World.WWW){
    loc?.children?.forEach { tt ->
        tt.children.forEach {itc->
            if(Data.visits.getVisited(itc) in listOf(AUTO_GROUP,NO_GROUP)) {
                if(itc.children.any { itcc -> Data.visits.getVisited(itcc) != NO_GROUP })
                    Data.visits.setVisited(itc, AUTO_GROUP)
                else
                    Data.visits.setVisited(itc, NO_GROUP)
            }
        }
        if(Data.visits.getVisited(tt) in listOf(AUTO_GROUP,NO_GROUP)) {
            if(tt.children.any { itc -> Data.visits.getVisited(itc) != NO_GROUP })
                Data.visits.setVisited(tt, AUTO_GROUP)
            else
                Data.visits.setVisited(tt, NO_GROUP)
        }
    }
}

@Composable
fun EditPlaceScreen(loc: GeoLoc, onExit:()->Unit={}) {
    var showEdit by remember { mutableStateOf(false) }
    val tabs : SnapshotStateList<GeoLoc> = remember { mutableStateListOf(loc) }
    val ctx = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(tabs.size) {
        selectedTab = tabs.lastIndex
    }
    SideEffect {
        syncVisited()
    }
    BackHandler {
        if (tabs.size > 1) tabs.removeAt(tabs.lastIndex)
        else onExit()
    }
    if(showEdit)
        EditPlaceDialog(false) {
            showEdit = false
            if (it) {
                Data.visits.setVisited(Data.selected_geoloc, NO_GROUP)
                Data.saveData()

                if (Data.selected_geoloc!=null && Data.selected_geoloc!!.children.any { itc-> Data.visits.getVisited(itc) != NO_GROUP }) {
                    Data.clearing_geoloc = Data.selected_geoloc
                }
            }
            if (Data.selected_group != null && Data.selected_geoloc != null) {
                Data.visits.setVisited(Data.selected_geoloc, Data.selected_group!!.key)
                Data.saveData()
            }
            Data.selected_geoloc = null
            Data.selected_group = null
        }

    Column {
        val currentTab = tabs.getOrNull(selectedTab) ?: return@Column

        ScrollableTabRow(
            selectedTabIndex = min(tabs.lastIndex,selectedTab)
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {
                        while (tabs.size > index + 1)
                            tabs.removeAt(tabs.lastIndex)
                    },
                    text = { Text(tab.fullName) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(currentTab.children.toList(), key= {it.code}) { loc ->

                GeoLocRow(loc, {
                        if (loc.children.isNotEmpty()){
                            tabs.add(loc)
                        }
                    }, {
                        Data.selected_geoloc = loc
                        if (Data.groups.size() == 1 && Settings.isSingleGroup(ctx)) {
                            Data.visits.setVisited(Data.selected_geoloc,
                                if (it != ToggleableState.On) Data.groups.getUniqueEntry()!!.key
                                else if(Data.selected_geoloc?.children?.any{ itc->
                                        Data.visits.getVisited(itc)!= NO_GROUP } == true) AUTO_GROUP
                                else NO_GROUP
                            )
                            Data.selected_group = null
                        } else {
                            Data.selected_group = null
                            showEdit=true
                        }

                    })

            }
        }

    }
}


@Composable
fun GeoLocRow(
    loc: GeoLoc,
    onClick: () -> Unit,
    onCheckedChange: (ToggleableState) -> Unit
) {
    val visits by Data.visits.visitsFlow.collectAsState()
    val checked by remember(visits, loc) {
        derivedStateOf {
            when (visits.getOrElse(loc.code) { NO_GROUP }) {
                NO_GROUP -> ToggleableState.Off
                AUTO_GROUP -> ToggleableState.Indeterminate
                else -> ToggleableState.On
            }
        }
    }

    val color = if (Data.visits.getVisited(loc) !in listOf(NO_GROUP, AUTO_GROUP))
        Color(Data.groups.getGroupFromKey(Data.visits.getVisited(loc)).color.color)
    else MaterialTheme.colors.onBackground
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable(onClick = onClick) // whole row clickable
            .padding(horizontal = 20.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = loc.fullName,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "",//loc.children.size.toString(),
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(end = 16.dp)
        )

        TriStateCheckbox(
            state = checked,
            onClick= { onCheckedChange(checked) },
            colors = CheckboxDefaults.colors(
                checkedColor = color,
            ),

            modifier = Modifier.size(24.dp)
        )
    }
    Spacer(modifier = Modifier
        .height(2.dp)
        .fillMaxWidth()
        .background(MaterialTheme.colors.onBackground))
}