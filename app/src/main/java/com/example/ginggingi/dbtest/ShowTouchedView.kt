package com.example.ginggingi.dbtest

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.ginggingi.dbtest.DataBase.DBHelper
import com.example.ginggingi.dbtest.Model.DetailDataModels
import kotlinx.android.synthetic.main.tch_view.*

class ShowTouchedView: AppCompatActivity() {

    private var dataArr: ArrayList<DetailDataModels> = ArrayList()

    lateinit var tPanel : RelativeLayout
    lateinit var tv : TextView
    private val TableName = "test"
    private var detail = ""
    private val dbHelper = DBHelper(this, "Touch", null, 1)

    lateinit var imgv: ImageView
    lateinit var params: RelativeLayout.LayoutParams

    lateinit var h : Handler
    var dcount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tch_view)

        val i = intent
        detail = i.getStringExtra("detail_name")
        h = Handler()

        viewInit()
        makeView(i.getIntExtra("first_x", 0), i.getIntExtra("first_y", 0))
        DBConnAndGetData()
    }

    private fun viewInit() {
        tPanel = touch_panel
        tv = Txtv
    }

    fun makeView(x: Int, y: Int) {
        imgv = ImageView(this)
        imgv.setBackgroundColor(Color.RED)
        tPanel.addView(imgv)
        params = RelativeLayout.LayoutParams(200, 200)
        params.leftMargin = x - 100
        params.topMargin = y - 100
        imgv.layoutParams = params

        tv.setText(String.format("%d : %d : %d : %d",
                imgv.left, imgv.right, imgv.top, imgv.bottom))

    }

    private fun DBConnAndGetData() {
        //ChkData
        dbHelper.dbRead()
        dbHelper.ChkAndCreateDetailTable(TableName, object : DBHelper.ChkCreateTable{
            override fun AlreadyExist() {
                Log.i("dTable", "Already Exist")
                getData()
            }

            override fun SuccessToCreate() {
                Log.i("dTable", "SuccessToCreate")
                getData()
            }

            override fun FailedToCreate() {
                Log.i("dTable", "ErrorFound")
            }

        })
        dbHelper.dbclose()
    }

    private fun getData() {
        dbHelper.dbRead()
        dataArr = dbHelper.getDetailDataFromTable(TableName, detail)
        dbHelper.dbclose()

        Log.i("detailGet","-> "+dataArr.size)
        for (d in dataArr) {
            Log.i("1 :", d.detail_name)
            Log.i("2 :", d.x.toString())
            Log.i("3 :", d.y.toString())
            Log.i("4 :", d.Time.toString())
            Log.i("5 :", d.ordering.toString())
        }
        if (dataArr.size != 0) {
            h.postDelayed(moveView, dataArr.get(dcount).Time.toLong())
        }
    }

    val moveView = object : Runnable {
        override fun run() {
            dcount++
            if (dcount <= dataArr.size) {

                params.leftMargin = dataArr.get(dcount-1).x
                params.topMargin = dataArr.get(dcount-1).y
                imgv.layoutParams = params

                tv.setText(String.format("%d : %d : %d : %d\n%d : %d : %d : %d",
                        imgv.left, imgv.right, imgv.top, imgv.bottom,
                        dataArr.get(dcount-1).x, dataArr.get(dcount-1).y, tPanel.top, tPanel.height))

//                h.postDelayed(this, dataArr.get(dcount).Time.toLong())
                h.postDelayed(this, dataArr.get(dcount-1).Time.toLong())
            } else {
                finish()
            }
        }
    }
}