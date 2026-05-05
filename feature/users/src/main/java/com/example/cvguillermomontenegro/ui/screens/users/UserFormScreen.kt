package com.example.cvguillermomontenegro.ui.screens.users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.stringResource
import com.example.cvguillermomontenegro.feature.users.R
import com.example.cvguillermomontenegro.ui.components.SectionCard
import com.example.cvguillermomontenegro.ui.screens.collectAsStateWithLifecycleCompat
import com.example.cvguillermomontenegro.ui.users.UserViewModel

@Composable
fun UserFormScreen(
    userId: Long?,
    onSaved: () -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val state by viewModel.formState.collectAsStateWithLifecycleCompat()

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionCard(title = stringResource(if ((userId ?: 0) > 0) R.string.user_form_edit_title else R.string.user_form_new_title)) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::updateName,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.user_form_name)) },
                isError = state.nameError != null
            )
            state.nameError?.let { Text(text = stringResource(it)) }

            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::updateEmail,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.user_form_email)) },
                isError = state.emailError != null
            )
            state.emailError?.let { Text(text = stringResource(it)) }

            OutlinedTextField(
                value = state.role,
                onValueChange = viewModel::updateRole,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.user_form_role)) }
            )

            OutlinedTextField(
                value = state.phone,
                onValueChange = viewModel::updatePhone,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.user_form_phone)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                supportingText = { Text(stringResource(R.string.user_form_phone_hint)) }
            )

            Button(
                onClick = { viewModel.saveUser(onSaved) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(if (state.id > 0) R.string.user_form_save_changes else R.string.user_form_create))
            }
        }
    }
}
