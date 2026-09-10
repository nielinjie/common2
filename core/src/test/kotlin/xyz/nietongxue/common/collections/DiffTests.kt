package xyz.nietongxue.common.collections

import org.junit.jupiter.api.Test
import xyz.nietongxue.common.base.IdGetter
import kotlin.test.assertEquals
import kotlin.test.assertTrue

data class TestItem(val id: String, val name: String)

class DiffTests {

    private val idGetter: IdGetter<TestItem> = { it.id }
    private val isChanged: (TestItem, TestItem) -> Boolean = { a, b -> a.name != b.name }

    @Test
    fun `diff with empty lists should return empty diffs`() {
        val result = diff(emptyList(), emptyList(), idGetter, isChanged)
        assertTrue(result.removed.ids.isEmpty())
        assertTrue(result.added.ids.isEmpty())
        assertTrue(result.changed.ids.isEmpty())
    }

    @Test
    fun `diff should detect added items`() {
        val old = listOf(TestItem("1", "a"))
        val new = listOf(TestItem("1", "a"), TestItem("2", "b"))
        val result = diff(old, new, idGetter, isChanged)
        assertTrue(result.removed.ids.isEmpty())
        assertEquals(listOf("2"), result.added.ids)
        assertTrue(result.changed.ids.isEmpty())
    }

    @Test
    fun `diff should detect removed items`() {
        val old = listOf(TestItem("1", "a"), TestItem("2", "b"))
        val new = listOf(TestItem("1", "a"))
        val result = diff(old, new, idGetter, isChanged)
        assertEquals(listOf("2"), result.removed.ids)
        assertTrue(result.added.ids.isEmpty())
        assertTrue(result.changed.ids.isEmpty())
    }

    @Test
    fun `diff should detect changed items`() {
        val old = listOf(TestItem("1", "a"))
        val new = listOf(TestItem("1", "b"))
        val result = diff(old, new, idGetter, isChanged)
        assertTrue(result.removed.ids.isEmpty())
        assertTrue(result.added.ids.isEmpty())
        assertEquals(listOf("1"), result.changed.ids)
    }

    @Test
    fun `diff should detect no changes for identical lists`() {
        val old = listOf(TestItem("1", "a"), TestItem("2", "b"))
        val new = listOf(TestItem("1", "a"), TestItem("2", "b"))
        val result = diff(old, new, idGetter, isChanged)
        assertTrue(result.removed.ids.isEmpty())
        assertTrue(result.added.ids.isEmpty())
        assertTrue(result.changed.ids.isEmpty())
    }

    @Test
    fun `diff should detect mixed changes`() {
        val old = listOf(TestItem("1", "a"), TestItem("2", "b"))
        val new = listOf(TestItem("1", "a1"), TestItem("3", "c"))
        val result = diff(old, new, idGetter, isChanged)
        assertEquals(listOf("2"), result.removed.ids)
        assertEquals(listOf("3"), result.added.ids)
        assertEquals(listOf("1"), result.changed.ids)
    }

    @Test
    fun `changes with empty lists should return empty`() {
        val result = changes(emptyList(), emptyList(), idGetter, isChanged)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `changes should detect added items`() {
        val old = listOf(TestItem("1", "a"))
        val new = listOf(TestItem("1", "a"), TestItem("2", "b"))
        val result = changes(old, new, idGetter, isChanged)
        assertEquals(3, result.size)
        val added = result.filterIsInstance<Change.Added<TestItem>>()
        assertEquals(2, added.size)
        assertTrue(added.any { it.value.id == "2" })
    }

    @Test
    fun `changes should detect removed items`() {
        val old = listOf(TestItem("1", "a"), TestItem("2", "b"))
        val new = listOf(TestItem("1", "a"))
        val result = changes(old, new, idGetter, isChanged)
        assertEquals(3, result.size)
        val removed = result.filterIsInstance<Change.Removed<TestItem>>()
        assertEquals(2, removed.size)
        assertTrue(removed.any { it.id == "2" })
    }

    @Test
    fun `changes should detect changed items`() {
        val old = listOf(TestItem("1", "a"))
        val new = listOf(TestItem("1", "b"))
        val result = changes(old, new, idGetter, isChanged)
        assertEquals(3, result.size)
        val changed = result.filterIsInstance<Change.Changed<TestItem>>()
        assertEquals(1, changed.size)
        assertEquals("b", changed[0].value.name)
    }

    @Test
    fun `changes should detect mixed changes`() {
        val old = listOf(TestItem("1", "a"), TestItem("2", "b"))
        val new = listOf(TestItem("1", "a1"), TestItem("3", "c"))
        val result = changes(old, new, idGetter, isChanged)
        val removed = result.filterIsInstance<Change.Removed<TestItem>>()
        val added = result.filterIsInstance<Change.Added<TestItem>>()
        val changed = result.filterIsInstance<Change.Changed<TestItem>>()
        assertEquals(2, removed.size)
        assertTrue(removed.any { it.id == "2" })
        assertEquals(2, added.size)
        assertTrue(added.any { it.value.id == "3" })
        assertEquals(1, changed.size)
        assertEquals("a1", changed[0].value.name)
    }

    @Test
    fun `patch with empty changes should return original list`() {
        val old = listOf(TestItem("1", "a"))
        val result = patch(old, emptyList(), idGetter)
        assertEquals(1, result.size)
        assertEquals("a", result[0].name)
    }

    @Test
    fun `patch should add items`() {
        val old = listOf(TestItem("1", "a"))
        val changes = listOf(Change.Added(TestItem("2", "b")))
        val result = patch(old, changes, idGetter)
        assertEquals(2, result.size)
        assertEquals("b", result.first { it.id == "2" }.name)
    }

    @Test
    fun `patch should remove items`() {
        val old = listOf(TestItem("1", "a"), TestItem("2", "b"))
        val changes = listOf(Change.Removed<TestItem>("2"))
        val result = patch(old, changes, idGetter)
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
    }

    @Test
    fun `patch should change items`() {
        val old = listOf(TestItem("1", "a"))
        val changes = listOf(Change.Changed(TestItem("1", "b")))
        val result = patch(old, changes, idGetter)
        assertEquals(1, result.size)
        assertEquals("b", result[0].name)
    }

    @Test
    fun `patch should apply mixed changes`() {
        val old = listOf(TestItem("1", "a"), TestItem("2", "b"))
        val changes: List<Change<TestItem>> = listOf(
            Change.Removed("2"),
            Change.Added(TestItem("3", "c")),
            Change.Changed(TestItem("1", "a1"))
        )
        val result = patch(old, changes, idGetter)
        assertEquals(2, result.size)
        assertEquals("a1", result.first { it.id == "1" }.name)
        assertEquals("c", result.first { it.id == "3" }.name)
        assertTrue(result.none { it.id == "2" })
    }

    @Test
    fun `changes and patch are inverse operations`() {
        val old = listOf(TestItem("1", "a"), TestItem("2", "b"))
        val new = listOf(TestItem("1", "a1"), TestItem("3", "c"))
        val changeList = changes(old, new, idGetter, isChanged)
        val patched = patch(old, changeList, idGetter)
        assertEquals(2, patched.size)
        assertEquals("a1", patched.first { it.id == "1" }.name)
        assertEquals("c", patched.first { it.id == "3" }.name)
        assertTrue(patched.none { it.id == "2" })
    }
}
