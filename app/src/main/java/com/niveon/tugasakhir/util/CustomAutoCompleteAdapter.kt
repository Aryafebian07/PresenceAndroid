package com.niveon.tugasakhir.util

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class CustomAutoCompleteAdapter(
    context: Context,
    private val items: List<CharSequence>
) : ArrayAdapter<CharSequence>(context, android.R.layout.simple_dropdown_item_1line, items) {

    private var originalData: List<CharSequence> = items.toList()

    override fun getFilter(): Filter {
        return CustomFilter()
    }

    private inner class CustomFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredResults = FilterResults()
            if (constraint.isNullOrBlank()) {
                filteredResults.values = originalData
            } else {
                val searchStr = constraint.toString().toLowerCase()
                val filteredList = items.filter {
                    it.toString().toLowerCase().contains(searchStr)
                }
                filteredResults.values = filteredList
            }
            return filteredResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            @Suppress("UNCHECKED_CAST")
            clear()
            addAll(results.values as List<CharSequence>)
            notifyDataSetChanged()
        }
    }
}