package com.example.cvguillermomontenegro.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.annotation.StringRes
import com.example.cvguillermomontenegro.feature.onboarding.R
import com.example.cvguillermomontenegro.ui.i18n.localizedStringResource

private data class OnboardingStep(
    @StringRes val title: Int,
    @StringRes val technology: Int,
    @StringRes val description: Int,
    @StringRes val hint: Int
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val steps = listOf(
        OnboardingStep(
            title = R.string.onboarding_step_1_title,
            technology = R.string.onboarding_step_1_technology,
            description = R.string.onboarding_step_1_description,
            hint = R.string.onboarding_step_1_hint
        ),
        OnboardingStep(
            title = R.string.onboarding_step_2_title,
            technology = R.string.onboarding_step_2_technology,
            description = R.string.onboarding_step_2_description,
            hint = R.string.onboarding_step_2_hint
        ),
        OnboardingStep(
            title = R.string.onboarding_step_3_title,
            technology = R.string.onboarding_step_3_technology,
            description = R.string.onboarding_step_3_description,
            hint = R.string.onboarding_step_3_hint
        )
    )
    var currentStep by remember { mutableIntStateOf(0) }
    val step = steps[currentStep]
    val isLast = currentStep == steps.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text(
                text = localizedStringResource(R.string.onboarding_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = localizedStringResource(R.string.onboarding_step_counter, currentStep + 1, steps.size),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DemoPanel(index = currentStep)
                    Text(
                        text = localizedStringResource(step.title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = localizedStringResource(R.string.onboarding_technology, localizedStringResource(step.technology)),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = localizedStringResource(step.description),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = localizedStringResource(step.hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(steps.size) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                if (index <= currentStep) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { currentStep -= 1 },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(localizedStringResource(R.string.onboarding_back))
                    }
                }
                Button(
                    onClick = {
                        if (isLast) onFinish() else currentStep += 1
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text(localizedStringResource(if (isLast) R.string.onboarding_enter else R.string.onboarding_next))
                }
            }
        }
    }
}

@Composable
private fun DemoPanel(index: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .padding(16.dp)
        ) {
            when (index) {
                0 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = null)
                            Text(localizedStringResource(R.string.onboarding_demo_open_drawer), fontWeight = FontWeight.SemiBold)
                        }
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                            )
                        }
                    }
                }
                1 -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Icon(
                            Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.TopStart)
                        )
                        Button(
                            onClick = {},
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Article, contentDescription = null)
                            Text(" ${localizedStringResource(R.string.onboarding_demo_view_articles)}")
                        }
                    }
                }
                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null)
                            Text(localizedStringResource(R.string.onboarding_demo_user_create))
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Group, contentDescription = null)
                            Text(localizedStringResource(R.string.onboarding_demo_user_list_edit))
                        }
                    }
                }
            }
        }
    }
}
