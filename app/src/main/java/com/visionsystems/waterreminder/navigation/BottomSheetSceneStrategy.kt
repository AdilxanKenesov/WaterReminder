package com.visionsystems.waterreminder.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

class BottomSheetSceneStrategy<T : Any> : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val lastEntry = entries.lastOrNull() ?: return null
        if (lastEntry.metadata[BOTTOM_SHEET_KEY] != true) return null
        return BottomSheetScene(
            key = lastEntry.contentKey,
            entry = lastEntry,
            previousEntries = entries.dropLast(1),
            overlaidEntries = entries.dropLast(1),
            onBack = onBack
        )
    }

    companion object {
        private const val BOTTOM_SHEET_KEY = "hydro_bottom_sheet"

        fun bottomSheet(): Map<String, Any> = mapOf(BOTTOM_SHEET_KEY to true)
    }
}

private class BottomSheetScene<T : Any>(
    override val key: Any,
    private val entry: NavEntry<T>,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val onBack: () -> Unit
) : OverlayScene<T> {

    override val entries: List<NavEntry<T>> = listOf(entry)

    @OptIn(ExperimentalMaterial3Api::class)
    override val content: @Composable () -> Unit = {
        ModalBottomSheet(
            onDismissRequest = onBack,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White
        ) {
            entry.Content()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BottomSheetScene<*>) return false
        return key == other.key &&
            entry == other.entry &&
            previousEntries == other.previousEntries &&
            overlaidEntries == other.overlaidEntries
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + entry.hashCode()
        result = 31 * result + previousEntries.hashCode()
        result = 31 * result + overlaidEntries.hashCode()
        return result
    }
}
