package mx.tecnm.tepic.ladm_u4_practica2_cesar_alejandro_alvarez_rodriguez

import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Build
import android.telephony.SmsMessage
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

/*
 RECEIVER = EVENTO U OYENTE DE ANDROID QUE PERMITE LA LECTURA DE EVENTOS DEL SISTEMA OPERATIVO.
 */
class SmsReceiver :BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras

        if(extras!=null){
            var sms = extras.get("pdus") as Array<Any>

            for(indice in sms.indices){
                var formato = extras.getString("format")

                var smsMensaje = if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                    SmsMessage.createFromPdu(sms[indice] as ByteArray,formato)
                }else{
                    SmsMessage.createFromPdu(sms[indice] as ByteArray)
                }

                var celularOrigen= smsMensaje.originatingAddress
                var contenidoSMS= smsMensaje.messageBody.toString()
                try{
                    var baseDatos = BaseDatos(context,"basedatos1",null,1)
                    var trans = baseDatos.writableDatabase
                    var variables= ContentValues()

                    variables.put("CELULAR",celularOrigen)
                    variables.put("MENSAJE",contenidoSMS)
                    var respuesta = trans.insert("ENTRANTES",null,variables)
                    if(respuesta==-1L){
                        Toast.makeText(context,"ERROR NO SE PUDO INSERTAR",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(context,"SE INSERTO CON EXITO",Toast.LENGTH_LONG).show()
                    }
                    baseDatos.close()
                }catch (err:SQLiteException){
                    Toast.makeText(context, err.message, Toast.LENGTH_LONG).show()
                }


                Toast.makeText(context, "ENTRO CONTENIDO ${contenidoSMS}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
/*
The message format is passed in the Telephony.SMS.Intent.SMS_RECEIVED_ACTION
as the format String extra, and will be either "3gpp" for GSM/UMTS/LTE messages
in 3GPP format or "3gpp2"
for CDMA/LTE messages in 3GPP2 format.
 */