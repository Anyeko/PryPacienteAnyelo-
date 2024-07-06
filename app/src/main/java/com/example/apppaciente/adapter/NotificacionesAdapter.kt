package com.example.apppaciente.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.apppaciente.R
import com.example.apppaciente.model.NotificacionesResponse

class NotificacionesAdapter(
    private val context: Context,
    private val notificacionesList: List<NotificacionesResponse.Data>,
    private val listener: NotificacionesAdapterListener
): RecyclerView.Adapter<NotificacionesAdapter.NotificacionesViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificacionesAdapter.NotificacionesViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cardview_notificacion, parent, false)
        return NotificacionesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notificacionesList.size
    }

    override fun onBindViewHolder(holder: NotificacionesViewHolder, position: Int) {
        val notificacion = notificacionesList[position]

        holder.tvFechaValue.text = notificacion.fecha
        holder.tvMensajeValue.text = notificacion.mensaje
        holder.tvEstadoValue.text = notificacion.estado

        if (notificacion.estado?.uppercase() == "LEÍDO") {
            holder.estadoImageView.setImageResource(R.drawable.icono_leido)  // Imagen verde
        } else {
            holder.estadoImageView.setImageResource(R.drawable.icono_no_leido)  // Imagen roja
        }

        holder.itemView.setOnClickListener {
            listener.onCardViewClicked(notificacion)
        }
    }

    inner class NotificacionesViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvFechaValue: TextView = view.findViewById(R.id.tvFechaValue)
        val tvMensajeValue: TextView = view.findViewById(R.id.tvMensajeValue)
        val tvEstadoValue: TextView = view.findViewById(R.id.tvEstadoValue)
        val estadoImageView: ImageView = view.findViewById(R.id.estadoImageView)
    }

    interface NotificacionesAdapterListener{
        fun onCardViewClicked(notificacion: NotificacionesResponse.Data)
    }
}
