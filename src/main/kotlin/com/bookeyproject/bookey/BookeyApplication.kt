package com.bookeyproject.bookey

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class BookeyApplication

fun main(args: Array<String>) {
    runApplication<BookeyApplication>(*args)
}
