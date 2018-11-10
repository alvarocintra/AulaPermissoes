
package alvarocintra.com.aulapermissoes.adapter


import alvarocintra.com.aulapermissoes.CadastroAmigoActivity
import alvarocintra.com.aulapermissoes.R
import alvarocintra.com.aulapermissoes.models.Amigos
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.util.*

class AmigosRecycleAdapter(amigosList: List<Amigos>, internal var context: Context) : RecyclerView.Adapter<AmigosRecycleAdapter.TaskViewHolder>() {

    internal var amigosList: List<Amigos> = ArrayList()
    init {
        this.amigosList = amigosList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_amigos, parent, false)
        return TaskViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val amigos = amigosList[position]
        holder.nome.text = amigos.nome
        holder.telefone.text = amigos.telefone
        holder.email.text = amigos.email
        holder.facebook.text = amigos.facebook
        holder.amigoImg.setImageURI(Uri.parse(amigos.amigoImg))

        holder.itemView.setOnClickListener {
            val i = Intent(context, CadastroAmigoActivity::class.java)
            i.putExtra("Mode", "E")
            i.putExtra("Id", amigos.id)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return amigosList.size
    }

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nome: TextView = view.findViewById(R.id.txtNome) as TextView
        var telefone: TextView = view.findViewById(R.id.txtTelefone) as TextView
        var email: TextView = view.findViewById(R.id.txtEmail) as TextView
        var facebook: TextView = view.findViewById(R.id.txtFacebook) as TextView
        var amigoImg: ImageView = view.findViewById(R.id.photoImageView) as ImageView
       //var list_item: LinearLayout = view.findViewById(R.id.list_item) as LinearLayout
    }

}

