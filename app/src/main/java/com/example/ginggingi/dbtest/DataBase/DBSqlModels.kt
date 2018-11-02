package com.example.ginggingi.dbtest.DataBase

import com.example.ginggingi.dbtest.DataBase.DBHelper

/** ********************************** **
 * MainTable                            *
 ** ********************************** **
 * position int,                        *
 * x int,                               *
 * y int,                               *
 * detail_name varchar,                 *
 * type varchar,                        *
 * duration int                         *
 ** ********************************** **
 * DynamicDetailData Table              *
 ** ********************************** **
 * detail_name varchar,                 *
 * time int,                            *
 * x int,                               *
 * y int,                               *
 * ordering int                         *
 ** ********************************** **/

class DBSqlModels {
    val ChkTableExist     = "Select Distinct tbl_name from sqlite_master where tbl_name = '%s' and type = 'table'"
    val CreateTable       = "Create Table %s (position int, x int, y int, detail_name varchar, type varchar, duration int);"
    val InsertTable       = "Insert Into %s Values('%d', '%d', '%d', '%s', '%s', '%d');"
    val CreateDetailTable = "Create Table %s (detail_name varchar, time int, x int, y int, ordering int);"
    val InsertDetailTable = "Insert Into %s Values('%s', '%d', '%d', '%d', '%d');"
    val getItemsFromTable = "Select * From %s"
    val getDetailItems    = "Select * From %s where detail_name = '%s' order by ordering ASC"
    val DeleteTable       = "Delete From %s where detail_name = '%s'"
}