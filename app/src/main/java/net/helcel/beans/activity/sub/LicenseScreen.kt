package net.helcel.beans.activity.sub

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mikepenz.aboutlibraries.ui.compose.DefaultChipColors
import com.mikepenz.aboutlibraries.ui.compose.DefaultLibraryColors
import com.mikepenz.aboutlibraries.ui.compose.android.rememberLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import net.helcel.beans.R
import net.helcel.beans.activity.SysTheme


@Preview
@Composable
fun LicenseScreen() {
    val libraries = rememberLibraries(R.raw.aboutlibraries)
    SysTheme {
        LibrariesContainer(
            libraries = libraries.value,
            modifier = Modifier.fillMaxSize(),
            colors = DefaultLibraryColors(
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onBackground,
                licenseChipColors = DefaultChipColors(
                    containerColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary,
                ),
                versionChipColors = DefaultChipColors(
                    containerColor = MaterialTheme.colors.secondary,
                    contentColor = MaterialTheme.colors.onSecondary,
                ),
                fundingChipColors = DefaultChipColors(
                    containerColor = MaterialTheme.colors.secondary,
                    contentColor = MaterialTheme.colors.onSecondary,
                ),
                dialogConfirmButtonColor = MaterialTheme.colors.primary,
            )
        )
    }
}