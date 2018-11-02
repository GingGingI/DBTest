package com.example.ginggingi.dbtest.DataBase

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.ginggingi.dbtest.Model.DataModels
import com.example.ginggingi.dbtest.Model.DetailDataModels

class DBHelper(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
        SQLiteOpenHelper(context, name, factory, version) {

//    Main: File_name
//    Detail: File_name + detail_name
//    context: Context, name: DBname, factory:?, version: version
    private lateinit var db: SQLiteDatabase
    private val sqls = DBSqlModels()

    override fun onCreate(database: SQLiteDatabase?) {
//        Main : rowid, position, x, y, detail_name, type, duration
//        Detail : detail_name, time, x, y, ordering
        db = database!!
    }

    fun dbWrite() {
        try{
            db = writableDatabase
        }catch (ne: NullPointerException) {
            ne.printStackTrace()
        }
    }

    fun dbRead() {
        try{
            db = readableDatabase
        }catch (ne: NullPointerException) {
            ne.printStackTrace()
        }
    }

    fun dbclose() {
        try{
            if (db != null)
                db.close()
        }catch (ne: NullPointerException){
            ne.printStackTrace()
        }
    }

//    And CreateTable if not exist
    fun ChkAndCreateTable(fName: String, listener: ChkCreateTable){
//        DB에는 특수기호 못들가게 막기.
        if (!ChkTableExist(fName)){
            try{
                if (!createTable(fName)) {
                    listener.FailedToCreate()
                }
            }catch (se: SQLiteException) {
                dbclose()
                se.printStackTrace()
                listener.FailedToCreate()
            }
            listener.SuccessToCreate()
        }else{
            listener.AlreadyExist()
        }
    }

    private fun createTable(table_name: String): Boolean {
//        SuccessToCreate = true, FailedToCreate = false
        try{
            db.execSQL(String.format(sqls.CreateTable, table_name))
        }catch (se: SQLiteException){
            dbclose()
            return false
        }
        return true
    }

    fun InsertTable(fName: String, data: DataModels, listener: ChkInsertTable) {
        if (ChkTableExist(fName)) {
            try{
                db.execSQL(String.format(sqls.InsertTable, fName, data.position, data.x, data.y, data.detail_name, data.type, data.duration))
            }catch (se: SQLiteException) {
                dbclose()
                se.printStackTrace()
                listener.FailedToInsert()
            }catch (ne: NullPointerException) {
                dbclose()
                ne.printStackTrace()
                listener.FailedToInsert()
            }
            listener.SuccessToInsert()
        }else{
            listener.TableNotExist()
        }
    }

    fun getDataFromTable(fName: String): ArrayList<DataModels> {
        val dataList: ArrayList<DataModels> = ArrayList()
        val cursor = db.rawQuery(String.format(sqls.getItemsFromTable, fName), null)

        while (cursor.moveToNext()){
            val dModel = DataModels()

            dModel.position = cursor.getInt(0)
            dModel.x = cursor.getInt(1)
            dModel.y = cursor.getInt(2)
            dModel.detail_name = cursor.getString(3)
            dModel.type = cursor.getString(4)
            dModel.duration = cursor.getInt(5)

            dataList.add(dModel)
        }
        return dataList
    }

    //DetailTable

    fun ChkAndCreateDetailTable(fName: String, listener: ChkCreateTable) {
        if (!ChkTableExist(fName+"_detail")){
            try {
                if (!CreateDetailTable(fName+"_detail")){
                    listener.FailedToCreate()
                }
            }catch (se: SQLiteException){
                dbclose()
                se.printStackTrace()
                listener.FailedToCreate()
            }
            listener.SuccessToCreate()
        }else{
            listener.AlreadyExist()
        }
    }

    fun InsertDetailTable(fName: String, dtdata: DetailDataModels, listener: ChkInsertTable) {
        if (ChkTableExist(fName+"_detail")) {
            try{
                db.execSQL(String.format(sqls.InsertDetailTable, fName+"_detail", dtdata.detail_name, dtdata.Time, dtdata.x, dtdata.y, dtdata.ordering))
            }catch (se: SQLiteException) {
                dbclose()
                se.printStackTrace()
                listener.FailedToInsert()
            }catch (ne: NullPointerException){
                dbclose()
                ne.printStackTrace()
                listener.FailedToInsert()
            }
            listener.SuccessToInsert()
        }else{
            listener.TableNotExist()
        }
    }

    private fun CreateDetailTable(table_name: String): Boolean {
        //        SuccessToCreate = true, FailedToCreate = false
        try{
            db.execSQL(String.format(sqls.CreateDetailTable, table_name))
        }catch (se: SQLiteException){
            dbclose()
            return false
        }
        return true
    }

    fun getDetailDataFromTable(fName: String, detail_name: String): ArrayList<DetailDataModels> {
        val detailDatalist: ArrayList<DetailDataModels> = ArrayList()
        val cursor = db.rawQuery(String.format(sqls.getDetailItems, fName+"_detail", detail_name), null)

        while (cursor.moveToNext()) {
            val dtModel = DetailDataModels()

            dtModel.detail_name = cursor.getString(0)
            dtModel.Time = cursor.getInt(1)
            dtModel.x = cursor.getInt(2)
            dtModel.y = cursor.getInt(3)
            dtModel.ordering = cursor.getInt(4)

            detailDatalist.add(dtModel)
        }
        return detailDatalist
    }

//    CheckTable
    private fun ChkTableExist(table_name: String): Boolean {
//        찾을때는 이름으로 찾기때문에 없으면 생성 있으면 이미있는거니 SameNameExist로 반환
        val cursor : Cursor = db.rawQuery(String.format(sqls.ChkTableExist, table_name), null)
        if (cursor.count > 0) {
            cursor.close()
            return true
        } else {
            cursor.close()
            return false
        }
    }

//    DataDrop

    fun DeleteDataFromTable(fName: String, detail_name: String, listener: ChkDeleteTable) {
        if (ChkTableExist(fName)) {
            try {
                db.execSQL(String.format(sqls.DeleteTable, fName, detail_name))
            } catch (se: SQLiteException) {
                listener.DeleteFailed()
                dbclose()
            }
            listener.DeleteSuccess()
        }else{
            listener.NotExist()
        }
    }

    fun DeleteDataFromDetailTable(fName: String, detail_name: String, listener: ChkDeleteTable) {
        if (ChkTableExist(fName+"_detail")) {
            try {
                db.execSQL(String.format(sqls.DeleteTable, fName + "_detail", detail_name))
            } catch (se: SQLiteException) {
                listener.DeleteFailed()
                dbclose()
            }
            listener.DeleteSuccess()
        }else{
            listener.NotExist()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    interface ChkCreateTable {
        fun AlreadyExist()
        fun SuccessToCreate()
        fun FailedToCreate()
    }

    interface ChkInsertTable {
        fun TableNotExist()
        fun SuccessToInsert()
        fun FailedToInsert()
    }

    interface ChkDeleteTable {
        fun NotExist()
        fun DeleteSuccess()
        fun DeleteFailed()
    }
}