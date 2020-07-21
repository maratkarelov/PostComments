package test.coreteka.presentation.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.users.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import test.coreteka.data.User
import test.coreteka.databinding.UsersBinding
import test.coreteka.presentation.adapters.UsersAdapter
import test.coreteka.presentation.core.IConnected
import java.lang.Exception
import java.net.URL

class UsersFragment : Fragment(), IConnected {
    private lateinit var adapter: UsersAdapter
    private lateinit var binding: UsersBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = UsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = UsersAdapter(requireActivity())
        binding.rvUsers.adapter = adapter
        binding.rvUsers.apply {
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
                val jsonList = json.parse(User.serializer().list, response)
                val userList = mutableListOf<User>()
                userList.addAll(jsonList.sortedBy { it.username })
                activity?.runOnUiThread {
                    adapter.updateData(userList)
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
}