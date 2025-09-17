package net.helcel.beans.activity.sub

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.Theme.colorToHex6
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import net.helcel.beans.R


@Preview
@Composable
fun EditGroupPreview(){
    EditGroupDialog(0,true,{},{},{})
}
@Composable
fun EditGroupDialog(
    key: Int = 0,
    deleteEnabled: Boolean = true,
    onAddCb: (Int) -> Unit,
    onDelCb: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val group by remember { mutableStateOf(Data.groups.getGroupFromKey(key)) }
    var name by remember { mutableStateOf(group.name) }
    var colorHex by remember {
        mutableStateOf(colorToHex6(group.color.color.toDrawable()).substring(1))
    }

    // Convert hex to Color safely
    var color = remember {try {
        Color("#$colorHex".toColorInt())
    } catch (_: Exception) {
        Color.Gray
    }}
    var r by remember { mutableIntStateOf((color.red *255).toInt()) }
    var g by remember { mutableIntStateOf((color.green*255).toInt()) }
    var b by remember { mutableIntStateOf((color.blue*255).toInt()) }

    fun updateHexFromSliders() {
        val newColor = Color(r, g, b)
        colorHex = colorToHex6(newColor.toArgb().toDrawable()).substring(1)
        color = newColor
    }
        Dialog(
            onDismissRequest = onDismiss,
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
                        color = MaterialTheme.colors.onBackground,
                        style = MaterialTheme.typography.h6,
                        text = if (key == 0) stringResource(R.string.action_add)
                        else stringResource(R.string.action_edit),
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Group name
                        OutlinedTextField(
                            value = name,
                            onValueChange = { it: String -> name = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(stringResource(R.string.name)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedTextColor = MaterialTheme.colors.onBackground,
                                focusedTextColor = MaterialTheme.colors.onBackground,
                            ),
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // Color preview
                            Box(
                                modifier = Modifier
                                    .size(96.dp, (96).dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color),
                                propagateMinConstraints = true,

                                content = {}
                            )
                            Column {
                                ColorSlider(
                                    r.toFloat(),
                                    { r = it.toInt(); updateHexFromSliders() },
                                    Color(255, 0, 0)
                                )
                                ColorSlider(
                                    g.toFloat(),
                                    { g = it.toInt(); updateHexFromSliders() },
                                    Color(0, 255, 0)
                                )
                                ColorSlider(
                                    b.toFloat(),
                                    { b = it.toInt(); updateHexFromSliders() },
                                    Color(0, 0, 255)
                                )
                            }
                        }
                        // Hex input
                        OutlinedTextField(
                            value = colorHex,
                            onValueChange = { n:String->
                                colorHex = n.filter { it.isLetterOrDigit() }
                            },
                            label = { Text(text="Color (hex)", color=MaterialTheme.colors.onBackground) },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedTextColor =MaterialTheme.colors.onBackground,
                                focusedTextColor = MaterialTheme.colors.onBackground,
                            ),

                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = {
                            val newKey = if (key != 0) key else Data.groups.genKey()
                            Data.groups.setGroup(
                                newKey,
                                name,
                                "#$colorHex".toColorInt().toDrawable()
                            )
                            Data.saveData()
                            onAddCb(newKey)
                            onDismiss()
                        }, ) {
                            Text("OK")
                        }
                        if (key != 0 && deleteEnabled) {
                            TextButton(onClick = {
                                val pos = Data.groups.findGroupPos(key)
                                Data.visits.deleteVisited(key)
                                Data.groups.deleteGroup(key)
                                Data.saveData()
                                onDelCb(pos)
                                onDismiss()
                            }) {
                                Text("Delete")
                            }
                        }
                        TextButton(onClick = { onDismiss() }) {
                            Text("Cancel")
                        }

                    }
                }
            },
        )
}

@Composable
fun ColorSlider(v: Float, onChange:(Float)->Unit, c:Color ){
    Slider(
        value = v,
        onValueChange = onChange,
        valueRange = 0f..255f,
        steps = 255,
        modifier = Modifier.height(32.dp),
        colors = SliderDefaults.colors(
            thumbColor = c,
            activeTickColor = c,
            inactiveTickColor = MaterialTheme.colors.onBackground,
        )
    )
}
