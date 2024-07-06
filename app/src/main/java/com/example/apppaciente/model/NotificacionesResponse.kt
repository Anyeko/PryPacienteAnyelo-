package com.example.apppaciente.model

class NotificacionesResponse {
    var status = false
    var message: String? = null
    var data: List<Data>? = null

    class Data {
        var notificacion_id = 0
        var mensaje: String?=null
        var fecha: String?=null
        var estado: String?=null
    }

}