package com.github.minhnguyen31093.trackme.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.github.minhnguyen31093.trackme.R
import com.github.minhnguyen31093.trackme.model.*
import com.github.minhnguyen31093.trackme.service.LocationService
import com.github.minhnguyen31093.trackme.utils.DialogUtils
import com.github.minhnguyen31093.trackme.utils.MapUtils
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_record.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*
import kotlin.concurrent.timerTask


class RecordActivity : BaseMapActivity() {

    private var mDb: RecordDatabase? = null
    private lateinit var mDbWorkerThread: DbWorkerThread
    private val compositeDisposable = CompositeDisposable()
    private var timer = Timer()
    var recordLocations = ArrayList<RecordLocation>()
    var googleMap: GoogleMap? = null

    private val onMapReady = OnMapReadyCallback { googleMap ->
        this@RecordActivity.googleMap = googleMap
        if (mLastLocation != null) {
            googleMap.clear()
            MapUtils.drawMarker(googleMap, LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude), R.drawable.ic_location_start)
            MapUtils.moveAndZoomMapTo(googleMap, mLastLocation!!.latitude, mLastLocation!!.longitude, 15)
        }
    }

    @Subscribe
    fun onEvent(recordEvent: RecordEvent) {
        recordLocations = recordEvent.points
        redrawMap()
    }

    override fun onLoadLocationCompleted() {
        if (recordLocations.size == 0 && mLastLocation != null) {
            recordLocations.add(RecordLocation(mLastLocation!!.latitude, mLastLocation!!.longitude, Date().time))
            MapUtils.drawMarker(googleMap, LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude), R.drawable.ic_location_start)
            MapUtils.moveAndZoomMapTo(googleMap, mLastLocation!!.latitude, mLastLocation!!.longitude, 15)
            startTimer()
        }
        startService(Intent(baseContext, LocationService::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.fMap) as SupportMapFragment
        mapFragment.getMapAsync(onMapReady)

        initEvent()
        mDbWorkerThread = DbWorkerThread("dbWorkerThread")
        mDbWorkerThread.start()
        mDb = RecordDatabase.getInstance(this)
    }

    override fun onResume() {
        super.onResume()
        if (recordLocations.size > 0) {
            startTimer()
            EventBus.getDefault().post(RecordRequestListEvent())
        }
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        RecordDatabase.destroyInstance()
        mDbWorkerThread.quit()
        super.onDestroy()
    }

    override fun onBackPressed() {
        stopService(Intent(baseContext, LocationService::class.java))
        DialogUtils.alertForce(this, R.string.confirm_stop_record, R.string.yes, R.string.no, DialogInterface.OnClickListener { dialog, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                addNewRecord()
            } else {
                super.onBackPressed()
            }
        })
    }

    private fun initEvent() {
        btnPause.setOnClickListener {
            stopTimer()
            btnPause.visibility = View.GONE
            gPause.visibility = View.VISIBLE
            EventBus.getDefault().post(RecordPauseEvent())

        }
        btnResume.setOnClickListener {
            startTimer()
            gPause.visibility = View.GONE
            btnPause.visibility = View.VISIBLE
            EventBus.getDefault().post(RecordResumeEvent())
        }
        btnStop.setOnClickListener {
            stopTimer()
            addNewRecord()
        }
    }

    private fun startTimer() {
        timer = Timer()
        timer.schedule(timerTask {
            runOnUiThread {
                val recordTime = RecordTime.convertToRecordTime(Date().time - recordLocations.first().dateTime)
                tvTime.text = getString(R.string.format_time,
                        MapUtils.numberToString(recordTime.hours),
                        MapUtils.numberToString(recordTime.minutes),
                        MapUtils.numberToString(recordTime.seconds))
            }
        }, 0, 1000)
    }

    private fun stopTimer() {
        timer.cancel()
    }

    private fun redrawMap() {
        if (googleMap != null && recordLocations.size > 0) {
            val km = getString(R.string.format_km, MapUtils.round(MapUtils.calculateDistance(recordLocations)))
            val kmH = getString(R.string.format_km_h, MapUtils.round(MapUtils.calculateSpeed(recordLocations)))
            val recordTime = RecordTime.convertToRecordTime(MapUtils.calculateTime(recordLocations))
            tvDistance.text = getString(R.string.format_distance, km)
            tvSpeed.text = getString(R.string.format_speed, kmH)
            tvTime.text = getString(R.string.format_time,
                    MapUtils.numberToString(recordTime.hours),
                    MapUtils.numberToString(recordTime.minutes),
                    MapUtils.numberToString(recordTime.seconds))

            googleMap!!.clear()
            MapUtils.drawMarker(googleMap, LatLng(recordLocations.first().lat, recordLocations.first().lng), R.drawable.ic_location_start)
            MapUtils.drawMarker(googleMap, LatLng(recordLocations.last().lat, recordLocations.last().lng), R.drawable.ic_location)
            MapUtils.drawPolyLines(googleMap!!, recordLocations)
            MapUtils.moveToBounds(googleMap!!, recordLocations)
        }
    }

    private fun addNewRecord() {
        stopService(Intent(baseContext, LocationService::class.java))
        if (recordLocations.size > 1) {
            pbRecord.visibility = View.VISIBLE
            val record = Record(Record.convertPointsToString(recordLocations),
                    MapUtils.calculateDistance(recordLocations),
                    MapUtils.calculateAvgSpeed(recordLocations),
                    MapUtils.calculateTime(recordLocations),
                    MapUtils.getStaticMapImage(recordLocations),
                    Date().time)
            if (MapUtils.round(record.distance) == "NaN") {
                record.distance = 0.0
            }
            if (MapUtils.round(record.avgSpeed) == "NaN") {
                record.avgSpeed = 0.0
            }
            compositeDisposable.add(Completable.fromAction({ mDb!!.weatherDataDao().insertRecord(record) })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        pbRecord.visibility = View.GONE
                        setResult(Activity.RESULT_OK)
                        super.onBackPressed()
                    }))
        } else {
            super.onBackPressed()
        }
    }
}