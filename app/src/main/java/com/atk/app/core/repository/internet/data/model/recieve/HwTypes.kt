package com.atk.app.core.repository.internet.data.model.recieve

import android.util.Log
import com.atk.app.core.repository.wialon.WialonResponse
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ListOfHwTypesDeserializer : JsonDeserializer<ListOfHwTypes> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ListOfHwTypes {
        var listOfHwTypes: List<HwTypes> = emptyList()
        var error: Long = -1
        try {
            listOfHwTypes = json.asJsonArray.map {
                val jsonObject = it.asJsonObject
                context.deserialize(jsonObject, object : TypeToken<HwTypes>() {}.type)
            }
        } catch (exception: IllegalStateException) {
            error = json.asJsonObject.get("error").asLong
        } catch (exception: NullPointerException) {
            Log.e("HwTypes", exception.toString())
            error = WialonResponse.JSON_PARSER_ERROR.code
        }
        return ListOfHwTypes(listOfHwTypes, error)
    }
}

data class ListOfHwTypes(
    val hwTypes: List<HwTypes>,
    override val error: Long = WialonResponse.NO_ERROR.code
) : HasError

data class HwTypes(
    val hw_category: String,
    val id: Int,
    val name: String,
    val tp: String,
    val uid2: Int,
    val up: String
)