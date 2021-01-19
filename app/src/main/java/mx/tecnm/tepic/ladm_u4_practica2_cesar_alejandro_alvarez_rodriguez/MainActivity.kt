package mx.tecnm.tepic.ladm_u4_practica2_cesar_alejandro_alvarez_rodriguez

import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    var baseDatos = BaseDatos(this,"basedatos1",null,1)
    var listaID = ArrayList<String>()
    val sipermiso=1
    val sipermisorecibe=2
    val sipermisolectura=3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS)!=
            PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECEIVE_SMS),sipermisorecibe)
        }

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)!=
            PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_SMS),sipermisolectura)
        }
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.SEND_SMS),sipermiso)
        }
        else{
            //envioSMS()
        }

        nuevo.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)!=
                PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_SMS),sipermisolectura)
            }
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.SEND_SMS),sipermiso)
            }
            else{
                comprobar()
            }
        }
        agregar.setOnClickListener {
            insertar()
        }
        comprobar()
    }

    override fun onResume() {
        super.onResume()
        cargarContactos()
        comprobar()
    }

    private fun comprobar() {
        var dato1=""
        var dato2=""
        var dato3=""
        try {
            var trans = baseDatos.readableDatabase
            var sql = "SELECT MENSAJE, CELULAR FROM ENTRANTES  ORDER BY ID DESC LIMIT 1;"
            var respuesta = trans.rawQuery(sql, null)
            if (respuesta.moveToFirst()) {
                do {
                    var sql2 = "SELECT ('CALIFICACION' || ' ' || NC || ' ' || UNID), CALIF FROM ALUMNOS"
                    var respuesta2 = trans.rawQuery(sql2,null)
                    if(respuesta2.moveToFirst()){
                        do{
                            if(respuesta.getString(0)==respuesta2.getString(0)){
                                SmsManager.getDefault().sendTextMessage(respuesta.getString(1),null
                                    , respuesta2.getFloat(1).toString(),null,null)
                                Toast.makeText(this, "SE ENVIO EL SMS", Toast.LENGTH_LONG).show()
                                //mensaje("${respuesta.getString(1)}, ${respuesta2.getFloat(1)}")
                            }else{
                                SmsManager.getDefault().sendTextMessage(respuesta.getString(1),null
                                    , "NO EXISTE: La sintaxis es CALIFICACION NC U? (DONDE ? ES EL NUMERO DE UNIDAD)",null,null)
                                Toast.makeText(this, "SE ENVIO EL SMS", Toast.LENGTH_LONG).show()
                            }
                        }while (respuesta2.moveToNext())
                    }
                    respuesta2.close()
                    //mensaje("LO CONTIENE \nID: ${respuesta.getInt(0)}\nTelefono: ${respuesta.getString(1)}\nMensaje: ${respuesta.getString(2)}")
                } while (respuesta.moveToNext())
            }else{
                mensaje("No existe")
            }
            respuesta.close()
            trans.close()
        }catch (e:SQLiteException){
            mensaje(e.message!!)
        }
    }

    private fun insertar() {
        try {
            /*
            * 1.-apertura de BD en modo LECTURA o ESCRITURA
            * 2.-construccion de sentencias SQL
            * 3.-Ejecucion y mostrado de Resultados
            * */
            var trans = baseDatos.writableDatabase //permite leer y escribir
            var variables= ContentValues()
            variables.put("NC",numeroControl.text.toString())
            variables.put("UNID",unidad.text.toString())
            variables.put("CALIF",calificacion.text.toString().toFloat())

            var respuesta = trans.insert("ALUMNOS",null,variables)
            if(respuesta==-1L){
                mensaje("ERROR NO SE PUDO INSERTAR")
            }else{
                mensaje("SE INSERTO CON EXITO")
                limpiarCampos()
            }
            trans.close()
        }catch (e: SQLiteException){
            mensaje(e.message!!)
        }
        cargarContactos()
    }
    public fun cargarContactos() {
        try{
            var trans = baseDatos.readableDatabase
            var personas = ArrayList<String>()

            var respuesta = trans.query("ALUMNOS", arrayOf("*"),null,null,null,null,null)

            listaID.clear()
            if(respuesta.moveToFirst()){
                //hay 1 o mas filas
                do{
                    var concatenacion = "NC: ${respuesta.getString(0)}\nUnidad: " +
                            "${respuesta.getString(1)}\nCalificacion: ${respuesta.getFloat(2)}"
                    personas.add(concatenacion)
                    listaID.add(respuesta.getInt(0).toString())
                }while (respuesta.moveToNext())
            }else{
                personas.add("NO HAY PERSONAS INSERTADAS")
            }
            //2 posibles resultados dentro de arraylist
            // 1= todas las tuplas de resultados
            // 2= no hay personas insertadas
            listaAlumnos.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,personas)
            trans.close()
        }catch (e:SQLiteException){
            mensaje("Error: "+e.message!!)
        }
    }
    private fun mensaje(s:String) {
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK"){d,i-> d.dismiss()}.show()
    }
    private fun limpiarCampos() {
        numeroControl.setText("")
        unidad.setText("")
        calificacion.setText("")
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==sipermiso){
            //envioSMS()
        }
        if(requestCode==sipermisorecibe){
            mensajeRecibir()
        }
        if(requestCode==sipermisolectura){
            leermensajesms()
        }
    }
    private fun mensajeRecibir() {
        AlertDialog.Builder(this)
            .setMessage("SE OTORGO RECIBIR")
            .show()
    }
    private fun leermensajesms() {
        var cursor = contentResolver.query(
            Uri.parse("content://sms/"),
            null,null,null,null
        )
        var resultado=""
        if(cursor!!.moveToFirst()){
            var posColumnacelularOrigen = cursor.getColumnIndex("address")
            var posColumnaMensaje = cursor.getColumnIndex("body")
            var posColumnaFecha = cursor.getColumnIndex("date")
            do {
                val fechamensaje = cursor.getString(posColumnaFecha)
                resultado+="Origen: "+cursor.getString(posColumnacelularOrigen)+
                        "\nMensaje: "+cursor.getString(posColumnaMensaje)+
                        "\nFecha: "+ Date(fechamensaje.toLong())
            }while (cursor.moveToNext())
        }else{
            resultado="NO HAY SMS EN BANDEJA DE ENTRADA"
        }
        Toast.makeText(this,resultado,Toast.LENGTH_LONG).show()
    }

}