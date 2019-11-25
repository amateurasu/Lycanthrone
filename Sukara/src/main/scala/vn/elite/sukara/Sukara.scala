package vn.elite.sukara

import scala.language.postfixOps

object Sukara {
    def main(args: Array[String]): Unit = {
        println("a")
        val sukara = new Sukara {
            {
                add1(1)
                add1(2)
                add1(3)
                noArgDef()
            }
        }
    }
}

class Sukara {
    var a = 0
    val add1: Unit => Int = (_: Unit) => {
        println(a + 1)
        a += 1
        a
    }
    val isEven: Int => Boolean = (i: Int) => {i % 2 == 0}

    def noArgDef(): Unit = {
        println("noArgs")
    }
}
