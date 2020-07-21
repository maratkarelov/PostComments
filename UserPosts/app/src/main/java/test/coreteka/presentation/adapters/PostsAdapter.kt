package test.coreteka.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import test.coreteka.R
import test.coreteka.data.Post
import test.coreteka.databinding.ItemPostBinding
import test.coreteka.databinding.ItemUserBinding

class PostsAdapter(context: Context) :
    BaseFilterableAdapter<Post>(context) {

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Post) {
            binding.post = item
            binding.executePendingBindings()
        }
    }

    class VHEmpty(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_EMPTY -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_empty, parent, false)
                VHEmpty(view)
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPostBinding.inflate(layoutInflater)
                PostViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (list.size == 0 && dataLoaded) 1 else list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list.size == 0) VIEW_EMPTY else VIEW_TYPE_ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostViewHolder) {
            holder.bind(list[position])
        }
    }

    fun sort() {
        list.sortBy { it.title }
        originalList.sortBy { it.title }
        notifyDataSetChanged()
    }

    fun sortDescending() {
        list.sortByDescending { it.title }
        originalList.sortByDescending { it.title }
        notifyDataSetChanged()
    }
}