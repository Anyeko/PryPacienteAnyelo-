package com.example.apppaciente.model

class CambioNotificacionResponse {
    var status = false
    var message: String? = null
    var data: Data? = null

    class  Data {
        var usuario_id = 0
    }
}