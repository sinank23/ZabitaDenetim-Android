package com.example.zabitadenetim.ui.model


// apiye gönderilecek verinin şablonu

data class InspectionAnswerRequest(
    val criterion_id: Int,
    val is_yes: Boolean
)

data class InspectionRequest(
    val businessName: String,
    val address: String,
    val answers: List<Boolean>, // denetim sorularının evet-hayır cevapları
    val answer_records: List<InspectionAnswerRequest> = emptyList(),
    // 30.07.2026 tarihinde eklendi
    val inspector_notes: String? = null,
    val business_id: Int? = null,

    // konum bilgileri 31.07.2026

    val latitude: Double?,
    val longitude: Double?
)
