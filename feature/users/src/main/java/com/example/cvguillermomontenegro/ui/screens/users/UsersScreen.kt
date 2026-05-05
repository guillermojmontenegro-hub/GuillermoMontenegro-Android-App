package com.example.cvguillermomontenegro.ui.screens.users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cvguillermomontenegro.feature.users.R
import com.example.cvguillermomontenegro.ui.components.SectionCard
import com.example.cvguillermomontenegro.ui.components.UserAvatar
import com.example.cvguillermomontenegro.ui.screens.collectAsStateWithLifecycleCompat
import com.example.cvguillermomontenegro.ui.users.UserViewModel

@Composable
fun UsersScreen(
    onCreateUser: () -> Unit,
    onEditUser: (Long) -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val users by viewModel.users.collectAsStateWithLifecycleCompat()
    var pendingDelete by remember { mutableStateOf<Long?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionCard(title = stringResource(R.string.users_admin_title)) {
                Text(
                    text = stringResource(R.string.users_admin_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (users.isEmpty()) {
            item {
                SectionCard(title = stringResource(R.string.users_empty_title)) {
                    Text(stringResource(R.string.users_empty_message))
                    TextButton(onClick = onCreateUser) {
                        Text(stringResource(R.string.users_create_first))
                    }
                }
            }
        } else {
            items(users) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        UserAvatar(label = user.name.take(1))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = user.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (user.role.isNotBlank()) {
                                Text(
                                    text = user.role,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (user.phone.isNotBlank()) {
                                Text(text = user.phone, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Column {
                            IconButton(onClick = { onEditUser(user.id) }) {
                                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.users_edit_content_description))
                            }
                            IconButton(onClick = { pendingDelete = user.id }) {
                                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.users_delete_content_description))
                            }
                        }
                    }
                }
            }
        }
    }

    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteUser(pendingDelete ?: return@TextButton)
                        pendingDelete = null
                    }
                ) {
                    Text(stringResource(R.string.users_delete_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text(stringResource(R.string.users_delete_cancel))
                }
            },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            title = { Text(stringResource(R.string.users_delete_title)) },
            text = { Text(stringResource(R.string.users_delete_message)) }
        )
    }
}
