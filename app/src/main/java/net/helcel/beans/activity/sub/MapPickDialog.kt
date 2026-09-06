package net.helcel.beans.activity.sub

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import net.helcel.beans.R
import net.helcel.beans.activity.SysTheme
import net.helcel.beans.countries.GeoLoc
import net.helcel.beans.countries.GeoLocTree
import net.helcel.beans.helper.AUTO_GROUP
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.NO_GROUP

private class PickRow(val loc: GeoLoc, val depth: Int)

/**
 * Turns the shapes a tap landed near into an indented list.
 *
 * Each candidate brings its continent and country along, so a tap that is
 * ambiguous between two regions can still be answered with "the whole country"
 * or "the whole continent" instead of only the regions themselves.
 */
private fun rowsFor(candidates: List<GeoLoc>): List<PickRow> {
    val rows = LinkedHashMap<String, PickRow>()
    candidates.forEach { candidate ->
        GeoLocTree.chain(candidate).forEachIndexed { depth, loc ->
            rows.getOrPut(loc.code) { PickRow(loc, depth) }
        }
    }
    return rows.values.toList()
}

@Composable
fun MapPickDialog(
    candidates: List<GeoLoc>,
    onPick: (GeoLoc) -> Unit,
    onDismiss: () -> Unit,
) {
    val visits by Data.visits.visitsFlow.collectAsState()
    val rows = remember(candidates) { rowsFor(candidates) }

    SysTheme {
        Dialog(
            onDismissRequest = onDismiss,
            content = {
                Column(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colors.background,
                            RoundedCornerShape(corner = CornerSize(16.dp)),
                        )
                        .padding(16.dp),
                ) {
                    Text(
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onBackground,
                        text = stringResource(R.string.select_place),
                    )
                    Text(
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onBackground,
                        text = stringResource(R.string.select_place_sub),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 360.dp),
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(rows, key = { it.loc.code }) { row ->
                                val group = visits.getOrElse(row.loc.code) { NO_GROUP }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onPick(row.loc) }
                                        .background(
                                            Color(88, 88, 88, 88),
                                            RoundedCornerShape(corner = CornerSize(16.dp)),
                                        )
                                        .padding(
                                            start = 8.dp + 16.dp * row.depth,
                                            top = 8.dp,
                                            end = 8.dp,
                                            bottom = 8.dp,
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(
                                                if (group == NO_GROUP || group == AUTO_GROUP) {
                                                    MaterialTheme.colors.onBackground
                                                } else {
                                                    Color(
                                                        Data.groups.getGroupFromKey(group).color.color
                                                    )
                                                },
                                                CircleShape,
                                            ),
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        color = MaterialTheme.colors.onBackground,
                                        style = MaterialTheme.typography.body2,
                                        text = row.loc.fullName,
                                        // Long names wrap inside the row rather
                                        // than running off the end of it.
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                }
            },
        )
    }
}
