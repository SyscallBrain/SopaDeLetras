package app.sopadeletras.data

import android.content.Context
import app.sopadeletras.core.MysteryEntry
import org.json.JSONArray
import org.json.JSONObject

data class WordCategoryData(
    val id: String,
    val name: String,
    val words: List<String>,
    val mysteries: Map<Int, MysteryEntry>,
)

class WordRepository(private val context: Context) {
    fun loadCategory(language: String, id: String): WordCategoryData {
        val text = context.assets.open("words/$language/$id.json").bufferedReader().use { it.readText() }
        return parseCategory(text)
    }

    fun availableCategories(language: String): List<WordCategoryData> =
        context.assets.list("words/$language").orEmpty()
            .filter { it.endsWith(".json") }
            .map { loadCategory(language, it.removeSuffix(".json")) }

    internal fun parseCategory(text: String): WordCategoryData {
        val root = JSONObject(text)
        val words = root.getJSONArray("words").toStringList()
        val mysteries = mutableMapOf<Int, MysteryEntry>()
        val array: JSONArray = root.optJSONArray("mystery") ?: JSONArray()
        for (i in 0 until array.length()) {
            val entry = array.getJSONObject(i)
            val word = entry.getString("word")
            mysteries[word.length] = MysteryEntry(word, entry.getString("clue"))
        }
        return WordCategoryData(
            id = root.getString("id"),
            name = root.getString("name"),
            words = words,
            mysteries = mysteries,
        )
    }

    private fun JSONArray.toStringList(): List<String> =
        (0 until length()).map { getString(it) }
}
