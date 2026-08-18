package com.example.zabitadenetim.data

data class LoginResponse(
    val access_token: String,
    val token_type: String,

    // 18.08.2026
    // backendden gelen kullanıcı rolünü tutar
    val role: String
)