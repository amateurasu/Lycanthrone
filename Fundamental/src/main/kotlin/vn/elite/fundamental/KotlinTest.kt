package vn.elite.fundamental

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun sum(a: Int, b: Int): Int = a + b

fun maxOf(a: Int, b: Int): Int = if (a > b) a else b

fun main() {
    GlobalScope.launch {
        // launch a new coroutine in background and continue
        delay(1500) // non-blocking delay for 1 second (default time unit is ms)
        println("World!") // print after delay
    }

    print("Hello, ") // vn.elite.fundamental.main thread continues while coroutine is delayed
    Thread.sleep(2000) // block vn.elite.fundamental.main thread for 2 seconds to keep JVM alive

    test()
}

fun test() {
    val mood = "I am sad"

    run {
        "I am happy".also { println(it) } // I am happy
    }
    println(mood)  // I am sad
}
