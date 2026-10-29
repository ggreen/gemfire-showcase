package com.vmware.data.solutions.spring.gemfire

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GemFireSinkApp

fun main(args: Array<String>) {
	runApplication<GemFireSinkApp>(*args)
}
