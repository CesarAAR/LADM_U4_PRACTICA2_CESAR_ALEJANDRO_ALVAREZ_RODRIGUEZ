package mx.tecnm.tepic.ladm_u4_practica2_cesar_alejandro_alvarez_rodriguez

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE ENTRANTES(ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,CELULAR VARCHAR(200), MENSAJE VARCHAR(2000))")
        db.execSQL("CREATE TABLE ALUMNOS(NC VARCHAR(9), UNID VARCHAR(2), CALIF FLOAT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

}