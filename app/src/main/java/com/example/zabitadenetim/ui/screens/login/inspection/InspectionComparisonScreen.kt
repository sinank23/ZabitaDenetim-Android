package com.example.zabitadenetim.ui.screens.login.inspection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zabitadenetim.data.InspectionAnswerResponse
import com.example.zabitadenetim.presentation.InspectionViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionComparisonScreen(
    currentInspectionId: Int,
    previousInspectionId: Int,
    onNavigateBack: () -> Unit,
    viewModel: InspectionViewModel = viewModel()
) {

    val currentAnswers by
    viewModel.currentComparisonAnswers.collectAsState()

    val previousAnswers by
    viewModel.previousComparisonAnswers.collectAsState()

    val isLoadingComparison by
    viewModel.isLoadingComparison.collectAsState()


    //17.08.2026
    // karşılaştırılacak iki denetimin soru-cevap kayıtlarını getir
    LaunchedEffect(
        currentInspectionId,
        previousInspectionId
    ) {
        viewModel.fetchComparisonAnswers(
            currentInspectionId = currentInspectionId,
            previousInspectionId = previousInspectionId
        )
    }


    // eski denetimdeki cevapları criterionId üzerinden eşleştir
    val previousAnswersMap =
        previousAnswers.associateBy {
            it.criterionId
        }


    // sadece iki denetimde de bulunan kriterleri karşılaştır
    val comparisonItems =
        currentAnswers.mapNotNull { currentAnswer ->

            val previousAnswer =
                previousAnswersMap[currentAnswer.criterionId]

            if (previousAnswer != null) {
                ComparisonItem(
                    questionText =
                        currentAnswer.questionText,

                    previousValue =
                        previousAnswer.isYes,

                    currentValue =
                        currentAnswer.isYes
                )

            } else {
                null
            }
        }


    val improvedCount =
        comparisonItems.count {
            !it.previousValue &&
                    it.currentValue
        }


    val worsenedCount =
        comparisonItems.count {
            it.previousValue &&
                    !it.currentValue
        }


    val unchangedCount =
        comparisonItems.count {
            it.previousValue ==
                    it.currentValue
        }


    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Denetim Karşılaştırması")
                },

                navigationIcon = {

                    IconButton(
                        onClick = onNavigateBack
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,

                            contentDescription = "Geri"
                        )
                    }
                },

                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor =
                            MaterialTheme.colorScheme.primary,

                        titleContentColor =
                            MaterialTheme.colorScheme.onPrimary,

                        navigationIconContentColor =
                            MaterialTheme.colorScheme.onPrimary
                    )
            )
        }

    ) { paddingValues ->


        when {

            isLoadingComparison -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(40.dp)
                    )

                    CircularProgressIndicator()

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Text(
                        text =
                            "Denetimler karşılaştırılıyor..."
                    )
                }
            }


            comparisonItems.isEmpty() -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(40.dp)
                    )

                    Text(
                        text =
                            "Karşılaştırılabilecek denetim kriteri bulunamadı."
                    )
                }
            }


            else -> {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {


                    item {

                        Text(
                            text =
                                "Denetim #$previousInspectionId → Denetim #$currentInspectionId",

                            style =
                                MaterialTheme.typography.titleMedium,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                MaterialTheme.colorScheme.primary
                        )


                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )


                        Card(
                            modifier =
                                Modifier.fillMaxWidth(),

                            elevation =
                                CardDefaults.cardElevation(
                                    defaultElevation = 3.dp
                                )
                        ) {

                            Column(
                                modifier =
                                    Modifier.padding(16.dp)
                            ) {

                                Text(
                                    text = "Karşılaştırma Özeti",

                                    style =
                                        MaterialTheme.typography.titleMedium,

                                    fontWeight =
                                        FontWeight.Bold
                                )


                                Spacer(
                                    modifier =
                                        Modifier.height(8.dp)
                                )


                                Text(
                                    text =
                                        "İyileşen kriter: $improvedCount"
                                )


                                Text(
                                    text =
                                        "Kötüleşen kriter: $worsenedCount"
                                )


                                Text(
                                    text =
                                        "Değişmeyen kriter: $unchangedCount"
                                )
                            }
                        }


                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )


                        Text(
                            text = "Kriter Değişimleri",

                            style =
                                MaterialTheme.typography.titleMedium,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )
                    }


                    items(
                        items = comparisonItems
                    ) { item ->

                        ComparisonCriterionCard(
                            item = item
                        )
                    }
                }
            }
        }
    }
}


data class ComparisonItem(
    val questionText: String,
    val previousValue: Boolean,
    val currentValue: Boolean
)


@Composable
fun ComparisonCriterionCard(
    item: ComparisonItem
) {

    val resultText =
        when {

            !item.previousValue &&
                    item.currentValue ->
                "İyileşti"

            item.previousValue &&
                    !item.currentValue ->
                "Kötüleşti"

            else ->
                "Değişmedi"
        }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(14.dp)
        ) {

            Text(
                text =
                    item.questionText,

                style =
                    MaterialTheme.typography.bodyMedium,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            Row(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        "Eski: ${
                            if (item.previousValue) {
                                "Evet"
                            } else {
                                "Hayır"
                            }
                        }",

                    modifier =
                        Modifier.weight(1f)
                )


                Text(
                    text =
                        "Yeni: ${
                            if (item.currentValue) {
                                "Evet"
                            } else {
                                "Hayır"
                            }
                        }",

                    modifier =
                        Modifier.weight(1f)
                )
            }


            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )


            HorizontalDivider()


            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )


            Text(
                text =
                    "Sonuç: $resultText",

                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}