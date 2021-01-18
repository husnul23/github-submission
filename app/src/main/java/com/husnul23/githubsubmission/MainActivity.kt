package com.husnul23.githubsubmission

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private val USER_TOKEN: String = BuildConfig.USER_TOKEN
    private var username: String? = ""
    private var isLoading = false
    private var page = 1

    private val listUserAdapter = ListUsersAdapter()
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindSearchView()

        layoutManager = LinearLayoutManager(this)
        rv_github.layoutManager = layoutManager
        rv_github.setHasFixedSize(true)
        showRecycleList()

        isLoading = false
        progressBar.visibility = View.INVISIBLE

        rv_github.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val pastVisibleItem = layoutManager.findLastVisibleItemPosition()

                    if (!isLoading) {
                        if (visibleItemCount <= pastVisibleItem + 3) {
                            page++
                            searchUsers(username, page)
                        }
                        isLoading = true
                        progressBar.visibility = View.VISIBLE
                    }
                }

                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    private fun bindSearchView() {
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                page = 1
                username = query
                searchUsers(username, page)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun searchUsers(username: String?, page: Int) {
        val client = AsyncHttpClient()
        progressBar.visibility = View.VISIBLE

        val url = "https://api.github.com/search/users?q=$username&page=$page&per_page=10"
        client.addHeader("Authorization", USER_TOKEN)
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    responseBody: ByteArray
            ) {
                isLoading = false
                progressBar.visibility = View.INVISIBLE
                val listUser = ArrayList<Github>()
                val result = String(responseBody)
                try {
                    val responseObject = JSONObject(result)
                    val items = responseObject.getJSONArray("items") // JA

                    for (i in 0 until items.length()) {
                        val item = items.getJSONObject(i)
                        val userName = item.getString("login")
                        val avatar = item.getString("avatar_url")
                        val user = Github()

                        user.username = userName
                        user.avatar = avatar
                        listUser.add(user)
                    }

                    if (listUser.isEmpty()) {
                        Toast.makeText(this@MainActivity, "Username tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }

                    if (page == 1 ) {
                        listUserAdapter.setUsers(listUser)
                    } else {
                        listUserAdapter.addUsers(listUser)
                    }

                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Data Tidak ditemukan", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    responseBody: ByteArray?,
                    error: Throwable?
            ) {
                Toast.makeText(this@MainActivity, "Data Tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showRecycleList() {
        rv_github.layoutManager = LinearLayoutManager(this)
        rv_github.adapter = listUserAdapter
    }
}