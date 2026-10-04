package app.sopadeletras.ui.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sopadeletras.AppContainer
import app.sopadeletras.core.Language
import app.sopadeletras.ui.components.SegmentedPicker
import app.sopadeletras.ui.theme.Bricolage
import app.sopadeletras.ui.theme.Figtree
import app.sopadeletras.ui.theme.LocalSopaColors
import app.sopadeletras.ui.theme.SopaColors
import app.sopadeletras.ui.theme.TintaAmbar
import app.sopadeletras.ui.theme.TokyoNight
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    container: AppContainer,
    onBack: () -> Unit,
) {
    val colors = LocalSopaColors.current
    val scope = rememberCoroutineScope()
    val language by container.settings.language.collectAsState(initial = Language.PT_CODE)
    val theme by container.settings.theme.collectAsState(initial = "TINTA")
    val assist by container.settings.directionAssist.collectAsState(initial = true)
    val haptics by container.settings.haptics.collectAsState(initial = true)
    val sound by container.settings.sound.collectAsState(initial = true)
    val letterScale by container.settings.letterScale.collectAsState(initial = 1f)
    val colorblind by container.settings.colorblind.collectAsState(initial = false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp, bottom = 22.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(38.dp).clip(RoundedCornerShape(13.dp)).background(colors.surface).clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = colors.text)
            }
            Text("Definições", fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.text, modifier = Modifier.padding(start = 12.dp))
        }
        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(colors.surface).padding(horizontal = 14.dp, vertical = 4.dp),
        ) {
            SettingBlock {
                SettingLabel("Idioma das palavras")
                SegmentedPicker(
                    options = listOf("PT-PT", "EN-US"),
                    selected = if (language == Language.EN_CODE) 1 else 0,
                    onSelect = {
                        scope.launch {
                            container.settings.setLanguage(if (it == 1) Language.EN_CODE else Language.PT_CODE)
                        }
                    },
                )
                SettingCaption("Aplica-se aos próximos jogos.")
            }
            Divider()
            SettingBlock {
                SettingLabel("Tema")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ThemeCard(name = "Tinta & Âmbar", preview = TintaAmbar, selected = theme != "TOKYO", onClick = {
                        scope.launch { container.settings.setTheme("TINTA") }
                    }, modifier = Modifier.weight(1f))
                    ThemeCard(name = "Tokyo Night", preview = TokyoNight, selected = theme == "TOKYO", onClick = {
                        scope.launch { container.settings.setTheme("TOKYO") }
                    }, modifier = Modifier.weight(1f))
                }
            }
            Divider()
            SettingBlock {
                SwitchRow(
                    label = "Ajuda de direção",
                    checked = assist,
                    onChange = { scope.launch { container.settings.setDirectionAssist(it) } },
                )
                SettingCaption("Encaixa o arrasto na direção mais próxima.")
            }
            Divider()
            SettingBlock {
                SwitchRow(
                    label = "Vibração",
                    checked = haptics,
                    onChange = { scope.launch { container.settings.setHaptics(it) } },
                )
            }
            Divider()
            SettingBlock {
                SwitchRow(
                    label = "Som",
                    checked = sound,
                    onChange = { scope.launch { container.settings.setSound(it) } },
                )
            }
            Divider()
            SettingBlock {
                SettingLabel("Tamanho das letras")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(colors.cell),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("A", fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = (20 * letterScale).sp, color = colors.text)
                    }
                    Slider(
                        value = letterScale,
                        onValueChange = { scope.launch { container.settings.setLetterScale(it) } },
                        valueRange = 0.85f..1.25f,
                        colors = SliderDefaults.colors(
                            thumbColor = colors.accent,
                            activeTrackColor = colors.accent,
                            inactiveTrackColor = colors.cell,
                        ),
                        modifier = Modifier.weight(1f).padding(start = 12.dp),
                    )
                }
            }
            Divider()
            SettingBlock {
                SwitchRow(
                    label = "Modo para daltónicos",
                    checked = colorblind,
                    onChange = { scope.launch { container.settings.setColorblind(it) } },
                )
            }
        }
    }
}

@Composable
private fun SettingBlock(content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        content()
    }
}

@Composable
private fun SettingLabel(text: String) {
    val colors = LocalSopaColors.current
    Text(text, fontFamily = Figtree, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = colors.text)
}

@Composable
private fun SettingCaption(text: String) {
    val colors = LocalSopaColors.current
    Text(text, fontFamily = Figtree, fontSize = 13.sp, color = colors.mute)
}

@Composable
private fun Divider() {
    val colors = LocalSopaColors.current
    Box(Modifier.fillMaxWidth().height(1.dp).background(colors.cell))
}

@Composable
private fun SwitchRow(
    label: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
    val colors = LocalSopaColors.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontFamily = Figtree, fontSize = 14.sp, color = colors.text, modifier = Modifier.weight(1f))
        SopaSwitch(checked = checked, onChange = onChange)
    }
}

@Composable
fun SopaSwitch(
    checked: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalSopaColors.current
    val offset = animateDpAsState(
        targetValue = if (checked) 22.dp else 2.dp,
        animationSpec = tween(150),
        label = "switch",
    )
    Box(
        modifier = modifier
            .size(width = 44.dp, height = 26.dp)
            .clip(CircleShape)
            .background(if (checked) colors.accent else colors.cell)
            .clickable(role = Role.Switch) { onChange(!checked) },
    ) {
        Box(
            modifier = Modifier
                .offset(x = offset.value)
                .padding(top = 3.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(if (checked) colors.onAccent else colors.mute),
        )
    }
}

@Composable
private fun ThemeCard(
    name: String,
    preview: SopaColors,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalSopaColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.cell)
            .then(
                if (selected) {
                    Modifier.border(2.dp, colors.accent, RoundedCornerShape(14.dp))
                } else {
                    Modifier
                },
            )
            .clickable { onClick() }
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(preview.bg)
                .padding(6.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)).background(preview.surface))
                Box(Modifier.fillMaxWidth(0.6f).height(10.dp).clip(RoundedCornerShape(5.dp)).background(preview.accent))
            }
        }
        Text(name, fontFamily = Figtree, fontSize = 13.sp, color = colors.text)
    }
}
