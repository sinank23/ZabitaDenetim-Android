package com.example.zabitadenetim.ui.model


// apiye gönderilecek verinin şablonu

data class InspectionRequest(
    val businessName: String,
    val address: String,
    val answers: List<Boolean> // denetim sorularının evet-hayır cevapları
)
