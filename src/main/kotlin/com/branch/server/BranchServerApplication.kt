package com.branch.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BranchServerApplication

fun main(args: Array<String>) {
	runApplication<BranchServerApplication>(*args)
}
