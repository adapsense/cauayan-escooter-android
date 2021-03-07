package com.adapsense.escooter.threads.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapsense.escooter.R
import com.adapsense.escooter.api.models.objects.Thread
import com.adapsense.escooter.base.BaseActivity
import com.adapsense.escooter.base.BaseFragment
import com.adapsense.escooter.messages.views.MessagesActivity
import com.adapsense.escooter.threads.ThreadsContract
import com.adapsense.escooter.threads.ThreadsPresenter
import com.adapsense.escooter.threads.ThreadsRecyclerViewAdapter
import com.adapsense.escooter.util.AppLogger
import kotlinx.android.synthetic.main.fragment_threads.empty
import kotlinx.android.synthetic.main.fragment_threads.recyclerView
import kotlinx.android.synthetic.main.fragment_threads.swipeRefreshLayout
import java.util.*

class ThreadsFragment: BaseFragment(), ThreadsContract.View {

    private lateinit var threadsPresenter: ThreadsContract.Presenter

    private lateinit var adapter: ThreadsRecyclerViewAdapter

    companion object {

        @JvmStatic
        fun newInstance() = ThreadsFragment()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        threadsPresenter = ThreadsPresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_threads, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).showProgressDialog()
        threadsPresenter.start()
        swipeRefreshLayout.isRefreshing = true
        threadsPresenter.getThreads()
    }

    override fun setPresenter(presenter: ThreadsContract.Presenter?) {
        threadsPresenter = presenter!!
    }

    override fun setupViews() {
        swipeRefreshLayout.setOnRefreshListener {
            empty.visibility = View.GONE
            recyclerView.visibility = View.GONE
            threadsPresenter.getThreads()
        }

        adapter = ThreadsRecyclerViewAdapter(threadsPresenter)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = this@ThreadsFragment.adapter
        }
    }

    override fun showThreads(threads: LinkedList<Thread>) {
        (activity as BaseActivity).dismissDialogs()
        swipeRefreshLayout.isRefreshing = false
        if(threads.size > 0) {
            empty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        } else {
            empty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
        adapter.notifyDataSetChanged()
    }

    override fun setThread(thread: Thread) {

    }

    override fun showThread() {
        startActivity(Intent(activity, MessagesActivity::class.java))
        activity!!.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

}