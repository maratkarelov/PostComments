package test.coreteka.presentation.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import test.coreteka.R
import test.coreteka.data.Comment
import test.coreteka.data.PostComments
import test.coreteka.databinding.ItemPostBinding

class PostsAdapter(context: Context, private val iShowComments: IShowComments) :
    BaseFilterableAdapter<PostComments>(context) {

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostComments) {
            binding.postComments = item
            binding.executePendingBindings()
            binding.lComments.removeAllViews()
            if (item.comments != null) {
                for (comment in item.comments!!) {
                    val tvEmail = TextView(context)
                    tvEmail.text = comment.email
                    val tvComment = TextView(context)
                    tvComment.text = comment.body
                    binding.lComments.addView(tvEmail)
                    binding.lComments.addView(tvComment)
                }
            }
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
            val postComments = list[position]
            Log.d(
                "qwerty onBindViewHolder",
                "post =${postComments.post.id}, comments = " + postComments.comments?.size
            )
            holder.bind(postComments)
            if (postComments.comments == null) {
                iShowComments.readComments(postComments.post.id)
            }
        }
    }

    fun sort() {
        list.sortBy { it.post.title }
        originalList.sortBy { it.post.title }
        notifyDataSetChanged()
    }

    fun sortDescending() {
        list.sortByDescending { it.post.title }
        originalList.sortByDescending { it.post.title }
        notifyDataSetChanged()
    }

    fun updateComments(postId: Int, listComments: List<Comment>) {
        if (listComments.isNotEmpty()) {
            val postComments: PostComments? = list.find { it.post.id == postId }
            postComments?.comments = listComments
            notifyItemChanged(list.indexOf(postComments))
        }
    }
}

interface IShowComments {
    fun readComments(postId: Int)
}
