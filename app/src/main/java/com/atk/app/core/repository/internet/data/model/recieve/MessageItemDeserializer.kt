package com.atk.app.core.repository.internet.data.model.recieve

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class MessageItemDeserializer : JsonDeserializer<MessageItem> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext
    ): MessageItem {

        val params = json.asJsonObject.getAsJsonObject("prms")
        val keys = params.keySet()
        val hashMap = HashMap<String, MessageData>()
        keys.forEach {
            val paramObject = params.get(it).asJsonObject
            val paramV = paramObject.get("v")
            val paramValue =
                if (paramV.isJsonPrimitive) paramV.asString else paramV.asJsonObject.toString()
            val item = MessageData(
                paramValue,
                paramObject.getAsJsonPrimitive("ct").asLong,
                paramObject.getAsJsonPrimitive("at").asLong
            )
            hashMap[it] = item
        }
        return MessageItem(hashMap)
    }
}