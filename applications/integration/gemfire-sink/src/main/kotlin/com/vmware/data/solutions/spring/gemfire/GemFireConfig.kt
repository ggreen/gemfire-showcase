package com.vmware.data.solutions.spring.gemfire

import com.vmware.data.services.gemfire.client.GemFireClient
import com.vmware.data.services.gemfire.serialization.PDX
import org.apache.geode.cache.Region
import org.apache.geode.pdx.PdxInstance
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author Gregory Green
 */
@Configuration
public class GemFireConfig {

    @Value("\${regionName}")
    private lateinit var regionName:String;
    @Bean
    fun pdx() : PDX {
        return PDX();
    }

    @Bean
    fun createRegion() : Region<Any, PdxInstance>{

        println("Getting region: $regionName")

        return GemFireClient.connect().getRegion(regionName)
    }
}