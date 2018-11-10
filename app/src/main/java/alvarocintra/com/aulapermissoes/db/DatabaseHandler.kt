package alvarocintra.com.aulapermissoes.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import alvarocintra.com.aulapermissoes.models.Amigos
import java.util.*

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DatabaseHandler.DB_NAME, null, DatabaseHandler.DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME ($ID INTEGER PRIMARY KEY, $NOME TEXT,$TELEFONE TEXT,$EMAIL TEXT,$FACEBOOK TEXT,$AMIGOIMG TEXT);"
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
        db.execSQL(DROP_TABLE)
        onCreate(db)
    }

    fun addAmigo(amigos: Amigos): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(NOME, amigos.nome)
        values.put(TELEFONE, amigos.telefone)
        values.put(EMAIL, amigos.email)
        values.put(FACEBOOK, amigos.facebook)
        values.put(AMIGOIMG, amigos.amigoImg)
        val _success = db.insert(TABLE_NAME, null, values)
        db.close()
        Log.v("InsertedId", "$_success")
        return (Integer.parseInt("$_success") != -1)
    }

    fun getAmigo(_id: Int): Amigos {
        val amigos = Amigos()
        val db = writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_NAME WHERE $ID = $_id"
        val cursor = db.rawQuery(selectQuery, null)

        cursor?.moveToFirst()
        amigos.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)))
        amigos.nome = cursor.getString(cursor.getColumnIndex(NOME))
        amigos.telefone = cursor.getString(cursor.getColumnIndex(TELEFONE))
        amigos.email = cursor.getString(cursor.getColumnIndex(EMAIL))
        amigos.facebook = cursor.getString(cursor.getColumnIndex(FACEBOOK))
        amigos.amigoImg = cursor.getString((cursor.getColumnIndex(AMIGOIMG)))
        cursor.close()
        return amigos
    }

    fun amigo(): List<Amigos> {
        val amigoList = ArrayList<Amigos>()
        val db = writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val amigos = Amigos()
                    amigos.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)))
                    amigos.nome = cursor.getString(cursor.getColumnIndex(NOME))
                    amigos.telefone = cursor.getString(cursor.getColumnIndex(TELEFONE))
                    amigos.email = cursor.getString(cursor.getColumnIndex(EMAIL))
                    amigos.facebook = cursor.getString(cursor.getColumnIndex(FACEBOOK))
                    amigos.amigoImg = cursor.getString(cursor.getColumnIndex(AMIGOIMG))
                    amigoList.add(amigos)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return amigoList
    }

    fun updateAmigo(amigos: Amigos): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(NOME, amigos.nome)
        values.put(TELEFONE, amigos.telefone)
        values.put(EMAIL, amigos.email)
        values.put(FACEBOOK, amigos.facebook)
        values.put(AMIGOIMG, amigos.amigoImg)
        val _success = db.update(TABLE_NAME, values, ID + "=?", arrayOf(amigos.id.toString())).toLong()
        db.close()
        return Integer.parseInt("$_success") != -1
    }

    fun deleteAmigo(_id: Int): Boolean {
        val db = this.writableDatabase
        val _success = db.delete(TABLE_NAME, ID + "=?", arrayOf(_id.toString())).toLong()
        db.close()
        return Integer.parseInt("$_success") != -1
    }

    fun deleteTodosAmigos(): Boolean {
        val db = this.writableDatabase
        val _success = db.delete(TABLE_NAME, null, null).toLong()
        db.close()
        return Integer.parseInt("$_success") != -1
    }

    companion object {
        private val DB_VERSION = 2
        private val DB_NAME = "DBAula01"
        private val TABLE_NAME = "Amigos"
        private val ID = "Id"
        private val NOME = "Nome"
        private val TELEFONE = "Telefone"
        private val EMAIL = "Email"
        private val FACEBOOK = "Facebook"
        private val AMIGOIMG = "AmigoImg"
    }
}