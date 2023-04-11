package com.vmware.data.solutions.spring.gemfire

import com.vmware.data.services.gemfire.serialization.PDX
import org.apache.geode.cache.Region
import org.apache.geode.pdx.PdxInstance
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.function.Consumer

/**
 * @author Gregory Green
 */
@Component
public open class GemFireSink(
    val region: Region<Any, PdxInstance>,
    val pdx: PDX,
    @Value("\${keyFieldExpression:id}") val keyFieldExpression: String,
    @Value("\${valuePdxClassName:java.lang.Object}") val valuePdxClassName: String)
    : Consumer<String>
{
    override fun accept(json: String) {
           println(json);
        val formattedType = pdx.addTypeToJson(json,valuePdxClassName);
        var pdxInstance = pdx.fromJSON(formattedType);
        var key = pdxInstance.getField(keyFieldExpression);
        region[key] = pdxInstance;
    }
}