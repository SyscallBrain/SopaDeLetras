package app.sopadeletras.ui.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.sopadeletras.ui.theme.LocalSopaColors

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AdaptiveGameScaffold(
    header: @Composable () -> Unit,
    progress: @Composable () -> Unit,
    board: @Composable () -> Unit,
    side: @Composable () -> Unit,
    actions: @Composable () -> Unit,
    overlays: @Composable BoxScope.() -> Unit = {},
) {
    val colors = LocalSopaColors.current
    val activity = LocalContext.current as Activity
    val wide = calculateWindowSizeClass(activity).widthSizeClass == WindowWidthSizeClass.Expanded
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        if (wide) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 18.dp, bottom = 22.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                header()
                progress()
                Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                    Box(modifier = Modifier.weight(0.6f), contentAlignment = Alignment.TopCenter) {
                        board()
                    }
                    Column(
                        modifier = Modifier.weight(0.4f),
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                    ) {
                        side()
                        Spacer(Modifier.weight(1f))
                        actions()
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 18.dp, bottom = 22.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                header()
                progress()
                board()
                side()
                Spacer(Modifier.weight(1f))
                actions()
            }
        }
        overlays()
    }
}
