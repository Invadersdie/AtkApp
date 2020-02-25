package com.atk.app.core.repository.internet.data.model.recieve

import com.atk.app.core.repository.wialon.WialonResponse

data class SearchResult<T : Item>(
    val searchSpec: SearchSpec,
    val dataFlags: Int, /* примененные флаги видимости */
    val totalItemsCount: Int, /* количество найденных элементов*/
    val indexFrom: Int, /* начальный индекс */
    val indexTo: Int, /* конечный индекс */
    val items: List<T>, /* найденные элементы */
    override val error: Long = WialonResponse.NO_ERROR.code
) : HasError

data class SearchSpec(
    val itemsType: String, /* тип элементов*/
    val propName: String, /* имя свойства */
    val propValueMask: String, /* значение свойства */
    val sortType: String, /* свойство для сортировки*/
    val propType: String? = null /* тип свойства */
)