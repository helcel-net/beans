package net.helcel.beans.activity.sub

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import net.helcel.beans.R
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.Groups
import androidx.core.graphics.drawable.toDrawable
import net.helcel.beans.activity.SysTheme

@Composable
fun EditPlaceDialog(delete: Boolean, onDialogDismiss: (Boolean)->Unit){
    SysTheme {
        var showEditGroupDialog by remember { mutableStateOf(false) }
        var showEditPlaceColorDialog by remember { mutableStateOf(true) }
        var showSelectedKey by remember { mutableIntStateOf(-1) }
        var showDelete by remember { mutableStateOf(false) }
        if (showEditGroupDialog)
            EditGroupDialog(
                key = showSelectedKey,
                deleteEnabled = showDelete,
                onAddCb = { },
                onDelCb = {

                },
                onDismiss = {
                    showEditGroupDialog = false
                },
            )
        if (showEditPlaceColorDialog)
            EditPlaceColorDialog(
                delete,
                onAdd = {
                    showSelectedKey = it
                    showDelete = false
                    showEditGroupDialog = true
                },
                onDelete = {
                    showSelectedKey = it
                    showDelete = true
                    showEditGroupDialog = true
                },
                onClear = {
                    showEditPlaceColorDialog = false
                    onDialogDismiss(true)
                },
                onDismiss = {
                    showEditPlaceColorDialog = false
                    onDialogDismiss(false)
                }
            )
    }
}

@Preview
@Composable
fun GroupListPreview() {
    Data.groups = Groups(0, HashMap())
    Data.groups.setGroup(0, "Testing", Color.Red.toArgb().toDrawable())
    Data.groups.setGroup(1, "Testing", Color.Blue.toArgb().toDrawable())
    EditPlaceColorDialog(false,{},{},{},{})
}

@Composable
fun EditPlaceColorDialog(
    deleteMode: Boolean = false,
    onAdd: (Int) -> Unit = {},
    onDelete: (Int) -> Unit= {},
    onClear: () -> Unit= {},
    onDismiss: () -> Unit= {},
) {
    val groups by Data.groups.groupsFlow.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        content = {
            Column(
                modifier = Modifier
                    .background(
                        MaterialTheme.colors.background,
                        RoundedCornerShape(corner = CornerSize(16.dp)))
                    .padding(16.dp)
                ,

                ) {
                Text(
                    style = MaterialTheme.typography.h6,
                    color=MaterialTheme.colors.onBackground,
                    text = if (deleteMode) stringResource(R.string.select_group)
                    else stringResource(R.string.edit_group)
                )
                Text(
                    style = MaterialTheme.typography.caption,
                    color=MaterialTheme.colors.onBackground,
                    text = if (deleteMode) stringResource(R.string.select_group_sub)
                    else stringResource(R.string.edit_group_sub)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp) // cap dialog growth
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                        //.weight(1f)
                    ) {
                        items(groups, key = { it.key }) { group ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = { Data.selected_group = group; onDismiss() },
                                        onLongClick = { onDelete(group.key) })
                                    .background(
                                        Color(88, 88, 88, 88),
                                        RoundedCornerShape(corner = CornerSize(16.dp))
                                    )
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,

                                ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(group.color.color), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(color=MaterialTheme.colors.onBackground,text=group.name)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End) {
                    if (!deleteMode) {
                        Button(onClick = { onAdd(0) }) {
                            Text(stringResource(R.string.action_add))
                        }
                    }
                    TextButton(onClick = {
                        if (deleteMode) onDismiss() else onClear()
                    }) {
                        Text(
                            text = if (deleteMode) stringResource(R.string.cancel)
                            else stringResource(R.string.action_clear)
                        )
                    }
                }
            }
        },
    )
}