package com.example.ginggingi.dbtest

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.ginggingi.dbtest.DataBase.DBHelper
import com.example.ginggingi.dbtest.Model.DataModels
import com.example.ginggingi.dbtest.Model.DetailDataModels

import kotlinx.android.synthetic.main.tch_view.*

class TouchView: AppCompatActivity(), View.OnTouchListener {
    lateinit var tPanel : RelativeLayout
    lateinit var tv : TextView
    var x:Int = 0
    var y:Int = 0
    var prex:Int = 0
    var prey:Int = 0
    var cnt:Int = 0
    var sec:Long = 0
    var fsec:Long = 0
    var save = false
    var position = 0
    var ordering = 1
    val dModel = DataModels()
    val dbHelper = DBHelper(this, "Touch", null, 1)
    private val TableName = "test"
    lateinit var imgv: ImageView
    lateinit var params: RelativeLayout.LayoutParams

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tch_view)

        viewInit()

    }

    fun makeView(x: Int, y: Int) {
        imgv = ImageView(this)
        imgv.setBackgroundColor(Color.RED)
        tPanel.addView(imgv)
        params = RelativeLayout.LayoutParams(200, 200)
        params.leftMargin = x - 100
        params.topMargin = y - 100
        imgv.layoutParams = params
    }

    private fun viewInit() {
        tPanel = touch_panel
        tv = Txtv
        tPanel.setOnTouchListener(this)
        dbHelper.dbRead()
        position = dbHelper.getDataFromTable(TableName).size
        dbHelper.dbclose()
    }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when(event!!.action) {
            MotionEvent.ACTION_DOWN ->{
                prex = event.x.toInt()
                prey = event.y.toInt()
                x = prex
                y = prey
//                Log.i("Move", String.format("%d : %d", event.x.toInt(), event.y.toInt()))
                tv.setText(String.format("%d : %d\n%d : %d.down", event.x.toInt(), event.y.toInt(), x, y))
                sec = System.currentTimeMillis()
                fsec = sec
                makeView(x, y)
                PrepareDB()
            }
            MotionEvent.ACTION_MOVE ->{
                //tPanel을 벗어나지않으면
                if (save){
                    if (cnt > 0){
                        save = false
                        cnt = 0
                    }else{
                        cnt++
                        tv.setText(String.format("%d.cnt", cnt))
                    }
                }else {
                    if (imgv.top > 0 && imgv.left > 0 && imgv.right < tPanel.width && imgv.bottom < tPanel.height) {
                        x = event!!.x.toInt() - prex
                        y = event!!.y.toInt() - prey
//                  Log.i("Move", String.format("%d : %d", event.x.toInt(), event.y.toInt()))
                        tv.setText(String.format("%d : %d : %d : %d\n%d : %d : %d : %d",
                                imgv.left, imgv.right, imgv.top, imgv.bottom,
                                tPanel.left, tPanel.width, tPanel.top, tPanel.height))

                        params.leftMargin = imgv.left + x
                        params.topMargin = imgv.top + y

                        prex = x + prex
                        prey = y + prey

                        saveDetailDB()
                    } else {
//                    벗어나면
//                    상하 체크.
                        if (imgv.top <= 0) {
                            params.topMargin = 10
                        } else if (imgv.bottom >= tPanel.height) {
                            params.topMargin = imgv.top - 10
                        }
//                    좌우 체크.
                        if (imgv.left <= 0) {
                            params.leftMargin = 10
                        } else if (imgv.right >= tPanel.width) {
                            params.leftMargin = imgv.left - 10
                        }
                    }
                    imgv.layoutParams = params
                    save = true
                }
            }
            MotionEvent.ACTION_UP ->{
                if (ordering != 1) {
                    dModel.type = "d"
                    dModel.duration = (System.currentTimeMillis() - fsec).toInt()
                }
                saveDB()
                tPanel.removeView(imgv)
                ordering = 1
            }
        }
        return true
    }

    private fun PrepareDB() {
        position++
        dModel.position = 0
        dModel.x = x
        dModel.y = y
        dModel.duration = 0
        dModel.type = "s"
        dModel.detail_name = "detail_"+position
    }

    private fun saveDB() {
        dbHelper.dbWrite()
        dbHelper.InsertTable(TableName, dModel, object : DBHelper.ChkInsertTable{
            override fun TableNotExist() {
                Log.e("Table", "NotExists")
            }

            override fun SuccessToInsert() {
                Log.e("Table", "Inserted")
            }

            override fun FailedToInsert() {
                Log.i("Table", "InsertFailed")
            }

        })
        dbHelper.close()
    }
    private fun saveDetailDB() {
//        DetailData
        val DtModel = DetailDataModels()
        DtModel.ordering = ordering
        DtModel.Time = (System.currentTimeMillis() - sec).toInt()
        sec = System.currentTimeMillis()
        DtModel.detail_name = "detail_"+position
        DtModel.x = imgv.left
        DtModel.y = imgv.top
        dbHelper.dbWrite()
        dbHelper.InsertDetailTable(TableName, DtModel, object : DBHelper.ChkInsertTable {
            override fun TableNotExist() {
                dbHelper.ChkAndCreateDetailTable(TableName, object : DBHelper.ChkCreateTable{
                    override fun AlreadyExist() {}
                    override fun SuccessToCreate() {
                        Log.e("Table22", "createdd")
                    }
                    override fun FailedToCreate() {
                        Log.e("Table22", "Failedd")
                    }
                })
            }
            override fun SuccessToInsert() {
                Log.e("Table2", "Inserted")
                ordering++
            }
            override fun FailedToInsert() {
                Log.i("Table3", "InsertFailed")
            }
        })
        dbHelper.close()
    }
}