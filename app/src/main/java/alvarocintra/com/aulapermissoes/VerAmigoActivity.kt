package alvarocintra.com.aulapermissoes

import alvarocintra.com.aulapermissoes.adapter.AmigosRecycleAdapter
import alvarocintra.com.aulapermissoes.db.DatabaseHandler
import alvarocintra.com.aulapermissoes.models.Amigos
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast


class VerAmigoActivity : AppCompatActivity() {

    var dbHandler: DatabaseHandler? = null
    var listAmigos: List<Amigos> = ArrayList()
    var amigosRecycleAdapter : AmigosRecycleAdapter? = null
    var recyclerView: RecyclerView? = null
    var linearLayoutManager: LinearLayoutManager? = null

    init {
        this.listAmigos = listAmigos
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_amigo)

        //initDB()
        initViews()
/*
        txtTelefone.setOnClickListener {
            makePhoneCall(txtTelefone.text.toString())
        }
        txtTelefone.setOnLongClickListener {
            sendSMS(txtTelefone.text.toString())
        }
        txtEmail.setOnClickListener {
            sendEmail(txtEmail.text.toString())
        }
        txtFacebook.setOnClickListener {
            openSite(txtFacebook.text.toString())
        }*/
    }

    //subir o menu na tela da activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_amigo, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.menuAmigoEditar -> {
                val intent = Intent(this, CadastroAmigoActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menuAmigoExcluir -> {
                val dialog = AlertDialog.Builder(this).setTitle("Info").setMessage("Aperte 'Sim' se deseja excluir todos os amigos (atenção isso não pode ser desfeito).")
                    .setPositiveButton("SIM") { dialog, i ->
                        val success = dbHandler?.deleteTodosAmigos() as Boolean
                        if (success)
                            finish()
                        dialog.dismiss()
                    }
                    .setNegativeButton("NÃO") { dialog, i ->
                        dialog.dismiss()
                    }
                dialog.show()

                return true
            }
            R.id.menuAmigoFavorito -> {
                Toast.makeText(this, "Colocar método de favorito", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    fun initDB(){
        dbHandler = DatabaseHandler(this)

        listAmigos = (dbHandler as DatabaseHandler).amigo()

        amigosRecycleAdapter = AmigosRecycleAdapter(amigosList = listAmigos, context = applicationContext)
        (recyclerView as RecyclerView).adapter = amigosRecycleAdapter
    }

    fun initViews() {
        recyclerView = findViewById(R.id.recycler_view) as RecyclerView
        amigosRecycleAdapter = AmigosRecycleAdapter(amigosList = listAmigos, context = applicationContext)
        linearLayoutManager = LinearLayoutManager(applicationContext)
        (recyclerView as RecyclerView).layoutManager = linearLayoutManager
    }

    override fun onResume() {
        super.onResume()
        initDB()
    }
}
