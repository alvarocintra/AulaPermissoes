package alvarocintra.com.aulapermissoes

import alvarocintra.com.aulapermissoes.db.DatabaseHandler
import alvarocintra.com.aulapermissoes.models.Amigos
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_cadastro_amigo.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class CadastroAmigoActivity : AppCompatActivity() {

    var dbHandler: DatabaseHandler? = null
    var isEditMode = false

    val CAMERA_REQUEST_CODE = 0
    lateinit var imageFilePath: String

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_amigo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initDB()
        initOperations()

        cameraButton?.setOnClickListener {
            //getPermissionImage()
            try {
                val imageFile = createImageFile()
                val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (callCameraIntent.resolveActivity(packageManager) != null) {
                    val authorities = packageName + ".fileprovider"
                    val imageUri = FileProvider.getUriForFile(this, authorities, imageFile)
                    callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
                }
            } catch (e: IOException) {
                Toast.makeText(this, "Não foi possível criar o arquivo.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*private fun getPermissionImage() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) == (PackageManager.PERMISSION_DENIED){
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permission, REQUEST_IMAGE_GALLERY)
            } else {}
        }else {
            Toast.makeText(this, "Não suportado para versão do seu Android.", Toast.LENGTH_LONG).show()
        }
    }*/

    //CAMERA OPERATIONS
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                /*if(resultCode == Activity.RESULT_OK && data != null) {
                    photoImageView.setImageBitmap(data.extras.get("data") as Bitmap)
                }*/
                if (resultCode == Activity.RESULT_OK) {
                    photoImageView.setImageBitmap(setScaledBitmap())
                }
            }
            else -> {
                Toast.makeText(this, "Unrecognized request code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName: String = "JPEG_" + timeStamp + "_"
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!storageDir.exists()) storageDir.mkdirs()
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        imageFilePath = imageFile.absolutePath
        //val amigo = dbHandler!!.getAmigo(intent.getIntExtra("Id", 0))
        //amigo.amigoImg = imageFilePath
        return imageFile
    }

    fun setScaledBitmap(): Bitmap {
        val imageViewWidth = photoImageView.width
        val imageViewHeight = photoImageView.height

        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imageFilePath, bmOptions)
        val bitmapWidth = bmOptions.outWidth
        val bitmapHeight = bmOptions.outHeight

        val scaleFactor = Math.min(bitmapWidth / imageViewWidth, bitmapHeight / imageViewHeight)

        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor

        return BitmapFactory.decodeFile(imageFilePath, bmOptions)

    }
    //FIM CAMERA

    private fun initDB() {
        dbHandler = DatabaseHandler(this)
        btnDelete.visibility = View.INVISIBLE
        if (intent != null && intent.getStringExtra("Mode") == "E") {
            isEditMode = true
            val amigos: Amigos = dbHandler!!.getAmigo(intent.getIntExtra("Id", 0))
            etxNome.setText(amigos.nome)
            etxPhone.setText(amigos.telefone)
            etxEmail.setText(amigos.email)
            etxFacebook.setText(amigos.facebook)
            photoImageView.setImageURI(Uri.parse(amigos.amigoImg))
            btnDelete.visibility = View.VISIBLE
            btnCadastrarAmigo.text = "Editar"
        }
    }

    private fun initOperations() {
        btnCadastrarAmigo.setOnClickListener {
            var success = false
            if (!isEditMode) {
                val amigos = Amigos()
                amigos.nome = etxNome.text.toString()
                amigos.telefone = etxPhone.text.toString()
                amigos.email = etxEmail.text.toString()
                amigos.facebook = etxFacebook.text.toString()
                amigos.amigoImg = imageFilePath
                success = dbHandler?.addAmigo(amigos) as Boolean
            } else {
                val amigos = Amigos()
                amigos.id = intent.getIntExtra("Id", 0)
                amigos.nome = etxNome.text.toString()
                amigos.telefone = etxPhone.text.toString()
                amigos.email = etxEmail.text.toString()
                amigos.facebook = etxFacebook.text.toString()
                amigos.amigoImg = imageFilePath
                success = dbHandler?.updateAmigo(amigos) as Boolean
            }

            if (success)
                Toast.makeText(this, "Operação concluída com sucesso.", Toast.LENGTH_SHORT).show()
                finish()
            //else Toast.makeText(this, "Houve algum erro.", Toast.LENGTH_SHORT).show()
        }

        btnDelete.setOnClickListener {
            val dialog =
                AlertDialog.Builder(this).setTitle("Info").setMessage("Aperte 'Sim' se deseja excluir o amigo.")
                    .setPositiveButton("SIM") { dialog, i ->
                        val success = dbHandler?.deleteAmigo(intent.getIntExtra("Id", 0)) as Boolean
                        if (success)
                            Toast.makeText(this, "Operação concluída com sucesso.", Toast.LENGTH_SHORT).show()
                            finish()
                        dialog.dismiss()
                    }
                    .setNegativeButton("NÃO") { dialog, i ->
                        dialog.dismiss()
                    }
            dialog.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}