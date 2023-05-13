package io.codibre.messengerk

import io.codibre.messengerk.stamp.FooStamp
import io.codibre.messengerk.stamp.JohnStamp
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class StampCollectionTest {

    @Test
    fun `it can create an empty collection`() {
        val collection = StampCollection()
        assertTrue(collection.isEmpty)
    }

    @Test
    fun `it can add stamps to the collection`() {
        val resultCollection = StampCollection().add(FooStamp(), JohnStamp())
        assertTrue(resultCollection.count() == 2)
    }

    @Test
    fun `it can remove stamps from the collection`() {
        val stamp1 = FooStamp()
        val stamp2 = JohnStamp()

        val collection = StampCollection().add(stamp1, stamp2)
        assertTrue(collection.count() == 2)
        assertInstanceOf(StampCollection::class.java, collection)

        val removedCollection = collection.remove(stamp1)
        assertTrue(removedCollection.count() == 1)
        assertInstanceOf(StampCollection::class.java, removedCollection)

        val fullRemovedCollection = collection.remove(stamp1, stamp2)
        assertTrue(fullRemovedCollection.count() == 0)
        assertInstanceOf(StampCollection::class.java, fullRemovedCollection)
    }

    @Test
    fun `it can filter stamps from the collection`() {
        val stamp1 = FooStamp("john")
        val stamp2 = FooStamp("doe")
        val collection = StampCollection().add(stamp1, stamp2)

        assertTrue(collection.allOf(FooStamp::class).count() == 2)
    }
}
