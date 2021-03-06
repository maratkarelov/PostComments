package test.coreteka.presentation.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.transaction
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.search_list.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import test.coreteka.R
import test.coreteka.data.User
import test.coreteka.databinding.SearchListBinding
import test.coreteka.presentation.adapters.UsersAdapter
import test.coreteka.presentation.core.IConnected
import test.coreteka.presentation.core.ItemClick
import java.net.URL

const val USER_ID = "userId"

class UsersFragment : Fragment(), IConnected, ItemClick<User> {

    private lateinit var adapter: UsersAdapter
    private lateinit var binding: SearchListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = SearchListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = UsersAdapter(requireActivity(), this)
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
        GlobalScope.async() {
            val response = URL("https://jsonplaceholder.typicode.com/users").readText()
            val json = Json(JsonConfiguration.Stable)
            // serializing lists
            try {
                val list = json.parse(User.serializer().list, response).toMutableList()
                activity?.runOnUiThread {
                    adapter.updateData(list)
                    if (binding.switchSort.isChecked) {
                        adapter.sort()
                    } else {
                        adapter.sortDescending()
                    }
                    adapter.filter.filter(et_search.query.toString())
                }
            } catch (exception: Exception) {
                print(exception.localizedMessage)
            }
        }
    }

    override fun onNetworkAvailable() {
        sendRequest()
    }

    override fun onItemClick(item: User) {
        val frag = PostsFragment()
        val args = Bundle()
        args.putInt(USER_ID, item.id)
        frag.arguments = args
        fragmentManager?.transaction {
            replace(R.id.fragmentContainer, frag)
            addToBackStack(null)
        }
    }
}