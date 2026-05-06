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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.example.cvguillermomontenegro.ui.i18n.localizedStringResource
import com.example.cvguillermomontenegro.feature.users.R
import com.example.cvguillermomontenegro.ui.components.SectionCard
import com.example.cvguillermomontenegro.ui.screens.collectAsStateWithLifecycleCompat
import com.example.cvguillermomontenegro.ui.users.PhoneNumberFormatter
import com.example.cvguillermomontenegro.ui.users.UserFormState
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
        SectionCard(title = localizedStringResource(if ((userId ?: 0) > 0) R.string.user_form_edit_title else R.string.user_form_new_title)) {
            UserFormFields(
                state = state,
                onNameChange = viewModel::updateName,
                onEmailChange = viewModel::updateEmail,
                onRoleChange = viewModel::updateRole,
                onPhoneChange = viewModel::updatePhone
            )

            Button(
                onClick = { viewModel.saveUser(onSaved) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(localizedStringResource(if (state.id > 0) R.string.user_form_save_changes else R.string.user_form_create))
            }
        }
    }
}

@Composable
internal fun UserFormFields(
    state: UserFormState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onRoleChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit
) {
    OutlinedTextField(
        value = state.name,
        onValueChange = onNameChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(localizedStringResource(R.string.user_form_name)) },
        isError = state.nameError != null
    )
    state.nameError?.let { Text(text = localizedStringResource(it)) }

    OutlinedTextField(
        value = state.email,
        onValueChange = onEmailChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(localizedStringResource(R.string.user_form_email)) },
        isError = state.emailError != null
    )
    state.emailError?.let { Text(text = localizedStringResource(it)) }

    OutlinedTextField(
        value = state.role,
        onValueChange = onRoleChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(localizedStringResource(R.string.user_form_role)) }
    )

    OutlinedTextField(
        value = state.phone,
        onValueChange = onPhoneChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(localizedStringResource(R.string.user_form_phone)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        visualTransformation = PhoneNumberVisualTransformation,
        supportingText = { Text(localizedStringResource(R.string.user_form_phone_hint)) }
    )
}

private object PhoneNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = PhoneNumberFormatter.normalize(text.text)
        val formatted = PhoneNumberFormatter.format(digits)

        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return PhoneNumberFormatter.originalToTransformed(offset, digits)
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return PhoneNumberFormatter.transformedToOriginal(offset, digits)
                }
            }
        )
    }
}
