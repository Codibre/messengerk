package io.codibre.messengerk

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.serialization.Serializable
import io.codibre.messengerk.stamp.Stamp
import kotlin.reflect.KClass

@Serializable
/**
 * Represents a collection of stamps associated with their fully qualified names.
 * Stamps can be added, removed, and queried based on their class.
 *
 * @property stamps The map of stamp lists, where the keys are the fully qualified names of the stamps.
 */
data class StampCollection(private val stamps: Map<String, List<Stamp>> = emptyMap()) : Iterator<Stamp> {

    companion object {
        /**
         * Builds a StampCollection from a list of stamps.
         *
         * @param stamps The list of stamps to build the collection from.
         * @return The built StampCollection.
         */
        fun buildFromList(stamps: List<Stamp>): StampCollection {
            val stampMap = stamps.groupBy { extractFQN(it) }
            return StampCollection(stampMap)
        }

        private fun extractFQN(stamp: Stamp): String {
            return stamp::class.qualifiedName ?: throw IllegalArgumentException("Could not extract FQN from stamp")
        }
    }

    /**
     * Returns the total count of stamps in the collection.
     *
     * @return The count of stamps.
     */
    fun count(): Int {
        return stamps.values.sumOf { it.size }
    }

    /**
     * Adds one or more stamps to the collection.
     *
     * @param stampArgs The stamps to add.
     * @return The new StampCollection with the added stamps.
     */
    fun add(vararg stampArgs: Stamp): StampCollection {
        val newMap = stamps.toMutableMap()

        stampArgs.forEach {
            val key = extractFQN(it)
            newMap[key] = newMap.getOrDefault(key, emptyList()) + it
        }

        return StampCollection(newMap)
    }

    /**
     * Removes one or more stamps from the collection.
     *
     * @param stampArgs The stamps to remove.
     * @return The new StampCollection with the removed stamps.
     */
    fun remove(vararg stampArgs: Stamp): StampCollection {
        val newMap = stamps.toMutableMap()

        stampArgs.forEach {
            val key = extractFQN(it)
            newMap[key]?.let { stampList ->
                newMap[key] = stampList - it
                if (newMap[key].isNullOrEmpty()) {
                    newMap.remove(key)
                }
            }
        }

        return StampCollection(newMap)
    }

    /**
     * Returns all the stamps of the specified class.
     *
     * @param classz The class of the stamps to retrieve.
     * @return The list of stamps matching the specified class.
     */
    fun allOf(classz: KClass<*>): List<Stamp> {
        return stamps[classz.qualifiedName] ?: emptyList()
    }

    /**
     * Returns the first stamp of the specified class, or null if not found.
     *
     * @param classz The class of the stamp to retrieve.
     * @return The first stamp matching the specified class, or null if not found.
     */
    fun firstOf(classz: KClass<*>): Stamp? {
        return allOf(classz).firstOrNull()
    }

    /**
     * Returns the last stamp of the specified class, or null if not found.
     *
     * @param classz The class of the stamp to retrieve.
     * @return The last stamp matching the specified class, or null if not found.
     */
    fun lastOf(classz: KClass<*>): Stamp? {
        return allOf(classz).lastOrNull()
    }

    /**
     * Checks if there is a next stamp in the collection.
     *
     * @return `true` if there is a next stamp, `false` otherwise.
     */

    override fun hasNext(): Boolean {
        return stamps.values.flatten().iterator().hasNext()
    }

    /**
     * Returns the next stamp in the collection.
     *
     * @return The next stamp.
     * @throws NoSuchElementException if there are no more stamps in the collection.
     */
    override fun next(): Stamp {
        return stamps.values.flatten().iterator().next()
    }

    /**
     * Checks if the collection is empty.
     *
     * @return `true` if the collection is empty, `false` otherwise.
     */
    @get:JsonIgnore
    val isEmpty: Boolean
        get() = stamps.isEmpty()


    /**
     * Checks if the collection is not empty.
     *
     * @return `true` if the collection is not empty, `false` otherwise.
     */
    @get:JsonIgnore
    val isNotEmpty: Boolean
        get() = stamps.isNotEmpty()
}
