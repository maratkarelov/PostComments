package test.coreteka.presentation.adapters

import android.content.Context
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class BaseFilterableAdapter<E>(protected var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    protected var list: MutableList<E> = mutableListOf()
    protected var originalList : MutableList<E> = mutableListOf()
    protected var dataLoaded: Boolean = false
    protected val VIEW_TYPE_ITEM = 1
    protected val VIEW_EMPTY = 2

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                list = results.values as MutableList<E>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {
                var filteredResults: List<E>? = null
                filteredResults = if (constraint.isEmpty()) {
                    originalList
                } else {
                    getFilteredResults(constraint.toString().toLowerCase(Locale.getDefault()))
                }
                val results = FilterResults()
                results.values = filteredResults
                return results
            }
        }
    }

    protected fun getFilteredResults(constraint: String?): List<E> {
        val results: MutableList<E> = ArrayList()
        for (item in originalList) {
            if (item.toString().toLowerCase(Locale.getDefault()).contains(constraint!!)) {
                results.add(item)
            }
        }
        return results
    }

    fun updateData(data: MutableList<E>) {
        dataLoaded = true
        list.clear()
        list.addAll(data)
        originalList.clear()
        originalList.addAll(data)
    }


}