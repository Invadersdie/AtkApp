package com.atk.app.screens.createunit

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.atk.app.core.repository.model.PickableObject
import java.util.*
import kotlin.collections.ArrayList

class ContainsArrayAdapter(
    context: Context,
    @LayoutRes resource: Int,
    items: List<PickableObject> = listOf()
) : ArrayAdapter<PickableObject>(context, resource, items.toMutableList()) {

    internal var tempItems: MutableList<PickableObject> = mutableListOf()
    internal var suggestions: MutableList<PickableObject> = mutableListOf()

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    private var filter: Filter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return if (constraint != null) {
                suggestions.clear()
                tempItems.forEach {
                    if (it.showField.toLowerCase(Locale.getDefault()).contains(
                            constraint.toString().toLowerCase(Locale.getDefault())
                        )
                    ) {
                        suggestions.add(it)
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                filterResults
            } else {
                FilterResults()
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            val filterList = results.values as? List<PickableObject>
            if (results.count > 0) {
                clear()
                filterList?.listIterator()?.forEach {
                    add(it)
                }.also {
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        view.text = getItem(position)?.showField
        return view
    }

    init {
        tempItems = items.toMutableList()
        suggestions = ArrayList()
    }

    override fun getFilter(): Filter {
        return filter
    }
}