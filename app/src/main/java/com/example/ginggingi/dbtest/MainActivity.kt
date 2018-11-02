package com.example.ginggingi.dbtest

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.ginggingi.dbtest.Adapter.RcAdapter
import com.example.ginggingi.dbtest.DataBase.DBHelper
import com.example.ginggingi.dbtest.Model.DataModels

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private var dataArr: ArrayList<DataModels> = ArrayList()

    private lateinit var rcyView: RecyclerView
    private lateinit var AddBtn: Button
    private val dbHelper = DBHelper(this, "Touch", null, 1)
    private val TableName = "test"
    private lateinit var adapter: RcAdapter
    private lateinit var lm: RecyclerView.LayoutManager
    private lateinit var swipe: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rcyView = rc
        AddBtn = Btn
        AddBtn.setOnClickListener(this)

        swipe = Swipe
        swipe.setOnRefreshListener(this)

        DBConnAndGetData()
    }

    override fun onRefresh() {
        ChkDataChange()
    }

    private fun DBConnAndGetData() {
        //ChkData
        swipe.isRefreshing = true
        dbHelper.dbRead()
        dbHelper.ChkAndCreateTable(TableName, object : DBHelper.ChkCreateTable{
            override fun AlreadyExist() {
                Log.i("Table", "Already Exist")
                getData()
            }

            override fun SuccessToCreate() {
                Log.i("Table", "SuccessToCreate")
                getData()
            }

            override fun FailedToCreate() {
                Log.i("Table", "ErrorFound")
            }

        })
        dbHelper.dbclose()
    }

    override fun onClick(v: View?) {
        when(v) {
            AddBtn -> {
                val i = Intent(this, TouchView::class.java)
                startActivity(i)
            }
        }
    }

    private fun getData() {
        dataArr = dbHelper.getDataFromTable(TableName)
        Log.i("dataget","-> "+dataArr.size)

        adapter = RcAdapter()
        adapter.SetDataArray(dataArr, this)
        adapter.notifyDataSetChanged()
        lm = LinearLayoutManager(this)

        rcyView.adapter = adapter
        rcyView.layoutManager = lm
        swipe.isRefreshing = false
    }

    fun sendActivity(dModel: DataModels) {
        val i = Intent(this, ShowTouchedView::class.java)
        i.putExtra("detail_name", dModel.detail_name)
        i.putExtra("first_x", dModel.x)
        i.putExtra("first_y", dModel.y)
        i.putExtra("Type",dModel.type)
        startActivity(i)
    }

    fun ChkDataChange() {
        DBConnAndGetData()
    }
}
