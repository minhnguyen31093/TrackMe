package com.github.minhnguyen31093.trackme.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import com.github.minhnguyen31093.trackme.R
import com.github.minhnguyen31093.trackme.adapter.RecordLocationAdapter
import com.github.minhnguyen31093.trackme.helper.PermissionHelper
import com.github.minhnguyen31093.trackme.model.DbWorkerThread
import com.github.minhnguyen31093.trackme.model.Record
import com.github.minhnguyen31093.trackme.model.RecordDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_record_history.*

class MainActivity : BaseActivity() {

    private val REQUEST_RECORD: Int = 1 /* 2 sec */

    var recordLocationAdapter = RecordLocationAdapter(ArrayList())
    private val compositeDisposable = CompositeDisposable()
    private var mDb: RecordDatabase? = null
    private lateinit var mDbWorkerThread: DbWorkerThread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_history)

        mDbWorkerThread = DbWorkerThread("dbWorkerThread")
        mDbWorkerThread.start()
        mDb = RecordDatabase.getInstance(this)

        loadRecords()

        rvRecord.layoutManager = LinearLayoutManager(this)
        rvRecord.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        rvRecord.adapter = recordLocationAdapter

        btnRecord.setOnClickListener {
            intent = Intent(this, RecordActivity::class.java)
            startActivityForResult(intent, REQUEST_RECORD)
        }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        RecordDatabase.destroyInstance()
        mDbWorkerThread.quit()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_RECORD) {
            loadRecords()
        }
    }

    private fun loadRecords() {
        pbRecord.visibility = View.VISIBLE
        compositeDisposable.add(mDb!!.weatherDataDao().getAllRecords()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    pbRecord.visibility = View.GONE
                    recordLocationAdapter.clear()
                    recordLocationAdapter.add(it)
                }))
    }
}