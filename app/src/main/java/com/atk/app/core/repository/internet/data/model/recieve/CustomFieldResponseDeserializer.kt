package com.atk.app.core.repository.internet.data.model.recieve

import com.atk.app.core.repository.wialon.WialonResponse
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class CustomFieldResponseDeserializer : JsonDeserializer<CustomFieldResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext
    ): CustomFieldResponse {
        return try {
            if (json!!.isJsonArray) {
                CustomFieldResponse(json.asJsonArray.first { it.isJsonPrimitive }.asLong)
            } else {
                CustomFieldResponse(
                    null,
                    json.asJsonObject.get("error").asLong
                )
            }
        } catch (exception: NullPointerException) {
            CustomFieldResponse(null, WialonResponse.JSON_PARSER_ERROR.code)
        }
    }
}