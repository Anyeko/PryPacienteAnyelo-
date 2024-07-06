package com.example.apppaciente.model

class CambioEstadoNotificacionResponse {
    var status = false
    var message: String? = null
    var data: Data? = null

    class  Data {
        var notificacion_id = 0
    }
}