package test.coreteka.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import test.coreteka.R
import test.coreteka.data.User
import test.coreteka.databinding.ItemUserBinding

class UsersAdapter(context: Context) :
    BaseFilterableAdapter<User>(context) {

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: User) {
            binding.user = item
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
                val binding = ItemUserBinding.inflate(layoutInflater)
                UserViewHolder(binding)
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
        if (holder is UserViewHolder) {
            holder.bind(list[position])
        }
    }

    fun sort() {
        list.sortBy { it.username }
        originalList.sortBy { it.username }
        notifyDataSetChanged()
    }

    fun sortDescending() {
        list.sortByDescending { it.username }
        originalList.sortByDescending { it.username }
        notifyDataSetChanged()
    }
}