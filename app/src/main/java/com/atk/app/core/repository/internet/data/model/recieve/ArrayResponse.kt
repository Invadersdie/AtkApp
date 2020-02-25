package com.atk.app.core.repository.internet.data.model.recieve

import com.atk.app.core.repository.wialon.WialonResponse
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

data class ArrayPrimitiveAndObjectResponse<T, Y>(
    val dataPrimitive: T?,
    val dataObject: Y?,
    override val error: Long = WialonResponse.NO_ERROR.code
) : HasError

class CustomPrimitiveObjectDeserializer<T, Y> constructor(
    private val classPrimitive: Class<T>,
    private val classObject: Class<Y>
) : JsonDeserializer<ArrayPrimitiveAndObjectResponse<T, Y>> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext
    ): ArrayPrimitiveAndObjectResponse<T, Y> {
        return try {
            if (json!!.isJsonArray) {
                val dataPrimitive: T = context.deserialize(
                    json.asJsonArray.first { it.isJsonPrimitive },
                    classPrimitive
                )
                val dataObject: Y =
                    context.deserialize(json.asJsonArray.first { it.isJsonObject }, classObject)
                ArrayPrimitiveAndObjectResponse(dataPrimitive, dataObject)
            } else {
                ArrayPrimitiveAndObjectResponse(null, null, json.asJsonObject.get("error").asLong)
            }
        } catch (exception: NullPointerException) {
            ArrayPrimitiveAndObjectResponse(null, null, WialonResponse.JSON_PARSER_ERROR.code)
        }
    }
}

data class ArrayResponse<T>(
    val data: List<T>,
    override val error: Long = WialonResponse.NO_ERROR.code
) : HasError

class ArrayResponseDeserializer<T> constructor(private val clazz: Class<T>) :
    JsonDeserializer<ArrayResponse<T>> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext
    ): ArrayResponse<T> {
        return try {
            if (json!!.isJsonArray) {
                ArrayResponse(data = context.deserialize(json, clazz))
            } else {
                ArrayResponse(emptyList(), json.asJsonObject.get("error").asLong)
            }
        } catch (exception: NullPointerException) {
            ArrayResponse(emptyList(), WialonResponse.JSON_PARSER_ERROR.code)
        }
    }
}