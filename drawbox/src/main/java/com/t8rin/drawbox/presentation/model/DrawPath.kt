package com.t8rin.drawbox.presentation.model

import android.graphics.Path
import com.t8rin.drawbox.domain.Action
import java.io.ObjectInputStream
import java.io.Serializable
import java.util.LinkedList

class DrawPath : Path(), Serializable {
    val actions = LinkedList<Action>()

    private fun readObject(inputStream: ObjectInputStream) {
        inputStream.defaultReadObject()

        val copiedActions = actions.map { it }
        copiedActions.forEach {
            it.perform(this)
        }
    }

    override fun reset() {
        actions.clear()
        super.reset()
    }

    override fun moveTo(x: Float, y: Float) {
        actions.add(Move(x, y))
        super.moveTo(x, y)
    }

    override fun lineTo(x: Float, y: Float) {
        actions.add(Line(x, y))
        super.lineTo(x, y)
    }

    override fun quadTo(x1: Float, y1: Float, x2: Float, y2: Float) {
        actions.add(Quad(x1, y1, x2, y2))
        super.quadTo(x1, y1, x2, y2)
    }
}