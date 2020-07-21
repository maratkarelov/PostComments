package test.coreteka.presentation.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.search_list.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import test.coreteka.R
import test.coreteka.data.Comment
import test.coreteka.data.Post
import test.coreteka.data.PostComments
import test.coreteka.databinding.SearchListBinding
import test.coreteka.presentation.adapters.IShowComments
import test.coreteka.presentation.adapters.PostsAdapter
import test.coreteka.presentation.core.IConnected
import java.net.URL

class PostsFragment : Fragment(), IConnected, IShowComments {
    private var userId: Int = 0
    private lateinit var adapter: PostsAdapter
    private lateinit var binding: SearchListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = SearchListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userId = arguments?.getInt(USER_ID) ?: 0
        binding.tvTitle.text = getString(R.string.posts)
        adapter = PostsAdapter(requireActivity(), this)
        binding.rv.adapter = adapter
        binding.rv.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        binding.etSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                // task HERE
                return false
            }

        })
        binding.setOnSortDirectionChange { _, isChecked ->
            if (isChecked) {
                adapter.sort()
            } else {
                adapter.sortDescending()
            }
            adapter.filter.filter(et_search.query.toString())
        }
        sendRequest()
    }

    private fun sendRequest() {
        GlobalScope.async {
            val responsePosts =
                URL("https://jsonplaceholder.typicode.com/posts?userId=$userId").readText()
            val json = Json(JsonConfiguration.Stable)
            // serializing lists
            val listPosts = json.parse(Post.serializer().list, responsePosts)
            val listPostComments = mutableListOf<PostComments>()
            for (post in listPosts) {
                listPostComments.add(PostComments(post, null))
            }
            activity?.runOnUiThread {
                adapter.updateData(listPostComments)
                if (binding.switchSort.isChecked) {
                    adapter.sort()
                } else {
                    adapter.sortDescending()
                }
                adapter.filter.filter(et_search.query.toString())
            }
        }
    }

    override fun onNetworkAvailable() {
        sendRequest()
    }

    override fun readComments(postId: Int) {
        Log.d("qwerty readComments", "post =$postId")
        GlobalScope.async {
            val responseComments =
                URL("https://jsonplaceholder.typicode.com/comments?postId=$postId").readText()

            val json = Json(JsonConfiguration.Stable)
            // serializing lists
            val listComments = json.parse(Comment.serializer().list, responseComments)
            activity?.runOnUiThread {
                adapter.updateComments(postId, listComments)
            }
        }
    }
}