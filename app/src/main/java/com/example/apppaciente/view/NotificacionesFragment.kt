package com.example.apppaciente.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppaciente.R
import com.example.apppaciente.adapter.NotificacionesAdapter
import com.example.apppaciente.api.ApiClient
import com.example.apppaciente.databinding.FragmentNotificacionesBinding
import com.example.apppaciente.model.CambioEstadoNotificacionResponse
import com.example.apppaciente.model.CambioNotificacionResponse
import com.example.apppaciente.model.NotificacionesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NotificacionesFragment : Fragment(), NotificacionesAdapter.NotificacionesAdapterListener {

    private var _binding: FragmentNotificacionesBinding? = null

    private val binding get() = _binding!!

    private lateinit var notificacionesAdapter: NotificacionesAdapter

    private var notificacionesList: List<NotificacionesResponse.Data> = emptyList()

    private lateinit var check: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNotificacionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val pacienteId = sharedPreferences.getInt("paciente_id", -1)
        val notificacionUser = sharedPreferences.getInt("notificacion", -1)

        check = view.findViewById(R.id.checkBoxNotificacion)

        check.isChecked = if (notificacionUser == 1) true else false

        binding.checkBoxNotificacion.setOnCheckedChangeListener { _, isChecked ->
            val notificacionEstado = if (isChecked) 1 else 0
            actualizarEstadoNotificacion(pacienteId, notificacionEstado)

        }

        // Inicialmente, obtener notificaciones si el checkbox está marcado
        if (binding.checkBoxNotificacion.isChecked) {
            obtenerNotificaciones(pacienteId)
        }

    }

    private fun actualizarEstadoNotificacion(pacienteId: Int, notificacionEstado: Int) {
        val apiService = ApiClient.apiService
        val call = apiService.actualizarNotificacion(pacienteId, notificacionEstado)

        call.enqueue(object : Callback<CambioNotificacionResponse> {
            override fun onResponse(
                call: Call<CambioNotificacionResponse>,
                response: Response<CambioNotificacionResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val cambioNotificacionResponse = response.body()!!
                    if (cambioNotificacionResponse.status) {
                        Toast.makeText(context, "${cambioNotificacionResponse.message}", Toast.LENGTH_SHORT).show()
                        if (notificacionEstado == 1) {
                            obtenerNotificaciones(pacienteId)
                        } else {
                            limpiarNotificaciones()
                        }
                    } else {
                        Toast.makeText(context, "Error: ${cambioNotificacionResponse.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<CambioNotificacionResponse>, t: Throwable) {
                Log.e("ActualizarEstado", "Error: ${t.message}")
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun limpiarNotificaciones() {
        notificacionesList = emptyList()
        notificacionesAdapter = NotificacionesAdapter(requireContext(), notificacionesList, this@NotificacionesFragment)
        binding.recyclerViewNotificaciones.adapter = notificacionesAdapter
    }

    private fun obtenerNotificaciones(pacienteId: Int) {
        val apiService = ApiClient.apiService
        val call = apiService.getNotificaciones(pacienteId)
        call.enqueue(object : Callback<NotificacionesResponse>{
            override fun onResponse(
                call: Call<NotificacionesResponse>,
                response: Response<NotificacionesResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val notificacionesResponse = response.body()!!
                    if (notificacionesResponse.status){
                        notificacionesList = notificacionesResponse.data ?: emptyList()
                        notificacionesAdapter = NotificacionesAdapter(requireContext(), notificacionesList, this@NotificacionesFragment)
                        binding.recyclerViewNotificaciones.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = notificacionesAdapter
                        }
                    }else{
                        Toast.makeText(context, "Error segundo: ${notificacionesResponse.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Error primero: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NotificacionesResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCardViewClicked(notificacion: NotificacionesResponse.Data) {
        val apiService = ApiClient.apiService
        val nuevoEstado = if (notificacion.estado?.uppercase() == "LEÍDO") 0 else 1

        val call = apiService.actualizarEstadoAEA(notificacion.notificacion_id, nuevoEstado)

        call.enqueue(object : Callback<CambioEstadoNotificacionResponse> {
            override fun onResponse(
                call: Call<CambioEstadoNotificacionResponse>,
                response: Response<CambioEstadoNotificacionResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val cambioEstadoResponse = response.body()!!
                    if (cambioEstadoResponse.status) {
                        Toast.makeText(context, "${cambioEstadoResponse.message}", Toast.LENGTH_SHORT).show()
                        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        val pacienteId = sharedPreferences.getInt("paciente_id", -1)
                        obtenerNotificaciones(pacienteId)
                    } else {
                        Toast.makeText(context, "Error: ${cambioEstadoResponse.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CambioEstadoNotificacionResponse>, t: Throwable) {
                Log.e("ActualizarEstado", "Error: ${t.message}")
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}