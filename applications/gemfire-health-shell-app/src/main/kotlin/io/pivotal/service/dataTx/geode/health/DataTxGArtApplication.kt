package io.pivotal.service.dataTx.geode.health

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableAutoConfiguration(exclude = arrayOf(GsonAutoConfiguration::class))
class DataTxGArtApplication

fun main(args: Array<String>) {
	runApplication<DataTxGArtApplication>(*args)
}
