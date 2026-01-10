package ru.sqy.model.dto

class TurnQueue<T>(
    private val list: MutableList<T>,
    private var index: Int = 0,
) {
    fun current(): T =
        list[index]

    fun next(): T {
        index = (index + 1) % list.size
        return current()
    }

    fun removeCurrent() {
        list.removeAt(index)
        if (list.isEmpty()) return
        if (index >= list.size) index = 0
    }

    fun remove(player: T) {
        val i = list.indexOf(player)
        if (i == -1) return

        list.removeAt(i)

        if (i < index) index--
        if (index >= list.size && list.isNotEmpty()) index = 0
    }

    fun size(): Int = list.size
}
