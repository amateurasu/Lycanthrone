package io.gatling.jsonpath

import com.fasterxml.jackson.databind.JsonNode

import scala.collection.AbstractIterator

abstract class RecursiveIterator[T](root: JsonNode) extends AbstractIterator[JsonNode] {

    protected var nextNode: JsonNode = _
    protected var finished: Boolean = _
    protected var pause: Boolean = _
    protected var stack: List[T] = _

    override def hasNext(): Boolean =
        (nextNode != null && !finished) || {
            pause = false
            if (stack == null) {
                // first access
                stack = Nil
                visitNode(root)
            } else {
                // resuming
                while (!pause && stack.nonEmpty) {
                    visit(stack.head)
                }
            }

            finished = nextNode == null
            !finished
        }

    override def next(): JsonNode =
        if (finished) {
            throw new UnsupportedOperationException("Can't call next on empty Iterator")
        } else if (nextNode == null) {
            throw new UnsupportedOperationException("Can't call next without calling hasNext first")
        } else {
            val consumed = nextNode
            nextNode = null
            consumed
        }

    protected def visitNode(node: JsonNode): Unit

    protected def visit(t: T): Unit
}
