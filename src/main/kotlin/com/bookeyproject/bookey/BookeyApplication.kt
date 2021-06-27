package com.bookeyproject.bookey

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookeyApplication

fun main(args: Array<String>) {
    runApplication<BookeyApplication>(*args)
}