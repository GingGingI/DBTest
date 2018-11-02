package com.example.ginggingi.dbtest.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import com.example.ginggingi.dbtest.DataBase.DBHelper
import com.example.ginggingi.dbtest.MainActivity
import com.example.ginggingi.dbtest.Model.DataModels
import com.example.ginggingi.dbtest.R

class RcAdapter : RecyclerView.Adapter<RcHolder>() {

    var arrlist: ArrayList<DataModels> = ArrayList()
    lateinit var context: Context
    lateinit var mAc: MainActivity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RcHolder {
        context = parent.context
        val itemView:View = LayoutInflater.from(context).inflate(R.layout.listitem, parent, false)
        return RcHolder(itemView)
    }

    override fun getItemCount(): Int {
        return arrlist.size
    }

    fun SetDataArray(arrayList: ArrayList<DataModels>, mAc: MainActivity) {
        this.arrlist = arrayList
        this.mAc = mAc
    }

    override fun onBindViewHolder(holder: RcHolder, position: Int) {
        holder.itemView.setOnClickListener { v -> sendToSTch(position) }
        holder.itemView.setOnLongClickListener { v -> chkDelete(arrlist.get(position).detail_name)}
        holder.rtxtv.setText("duration : "+arrlist.get(position).duration.toString())
        holder.rtype.setText("type : "+arrlist.get(position).type)
        holder.rx.setText("x : "+arrlist.get(position).x.toString())
        holder.ry.setText("y : "+arrlist.get(position).y.toString())
    }

    private fun sendToSTch(position: Int) {
        mAc.sendActivity(arrlist.get(position))
    }

    private fun sendDataChanged() {
        mAc.ChkDataChange()
    }

    private fun chkDelete(detail_name: String): Boolean{
        val dialogbuilder = AlertDialog.Builder(context)
        dialogbuilder.setTitle("delete")
        dialogbuilder.setMessage("해당 데이터를 삭제하시겠습니까?")
        dialogbuilder.setPositiveButton("delete", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val dbHelper = DBHelper(context, "Touch", null, 1)
                dbHelper.dbWrite()
                dbHelper.DeleteDataFromTable("test", detail_name, object : DBHelper.ChkDeleteTable {
                    override fun NotExist() {
                        Log.i("Ddelete", "DataNotFounded")
                    }
                    override fun DeleteSuccess() {
                        Log.i("Ddelete", "dataDeleted")
                        sendDataChanged()
                    }
                    override fun DeleteFailed() {
                        Log.i("Ddelete", "FailedToDelete")
                    }
                })
                dbHelper.DeleteDataFromDetailTable("test", detail_name, object : DBHelper.ChkDeleteTable {
                    override fun NotExist() {
                        Log.i("Dtdelete", "DataNotFounded")
                    }
                    override fun DeleteSuccess() {
                        Log.i("Dtdelete", "dataDeleted")
                    }
                    override fun DeleteFailed() {
                        Log.i("Dtdelete", "FailedToDelete")
                    }
                })

            }
        }).setNegativeButton("Cancel",object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
            }
        })
        val dialog = dialogbuilder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        return true
    }
}