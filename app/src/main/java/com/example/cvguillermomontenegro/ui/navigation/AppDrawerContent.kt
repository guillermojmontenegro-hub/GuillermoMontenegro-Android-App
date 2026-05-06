package com.example.cvguillermomontenegro.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cvguillermomontenegro.R
import com.example.cvguillermomontenegro.domain.model.User
import com.example.cvguillermomontenegro.ui.components.UserAvatar
import com.example.cvguillermomontenegro.ui.i18n.localizedStringResource

@Composable
internal fun AppDrawerContent(
    activeUser: User?,
    currentRoute: String,
    darkModeEnabled: Boolean,
    onClose: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateUsers: () -> Unit,
    onNavigateAbout: () -> Unit,
    onToggleDarkMode: (User) -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.fillMaxHeight(),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                UserAvatar(
                    label = activeUser?.name?.take(1) ?: localizedStringResource(R.string.drawer_default_avatar),
                    modifier = Modifier.size(52.dp)
                )
                Text(
                    text = activeUser?.name ?: localizedStringResource(R.string.drawer_no_active_user),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = activeUser?.email ?: localizedStringResource(R.string.drawer_no_active_user_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                activeUser?.role?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            NavigationDrawerItem(
                label = { Text(localizedStringResource(R.string.drawer_home)) },
                selected = currentRoute == Routes.HOME,
                onClick = onNavigateHome,
                icon = { Icon(Icons.Default.Home, contentDescription = null) }
            )
            NavigationDrawerItem(
                label = { Text(localizedStringResource(R.string.drawer_users_list)) },
                selected = currentRoute == Routes.USERS,
                onClick = onNavigateUsers,
                icon = { Icon(Icons.Default.Group, contentDescription = null) }
            )

            val darkModeLabel = if (darkModeEnabled) {
                localizedStringResource(R.string.drawer_light_mode)
            } else {
                localizedStringResource(R.string.drawer_dark_mode)
            }
            NavigationDrawerItem(
                label = { Text(darkModeLabel) },
                selected = darkModeEnabled,
                onClick = { activeUser?.let(onToggleDarkMode) },
                icon = { Icon(Icons.Default.DarkMode, contentDescription = null) }
            )

            NavigationDrawerItem(
                label = { Text(localizedStringResource(R.string.drawer_about_app)) },
                selected = currentRoute == Routes.ONBOARDING,
                onClick = onNavigateAbout,
                icon = { Icon(Icons.Default.Info, contentDescription = null) }
            )

            Spacer(modifier = Modifier.weight(1f))
            NavigationDrawerItem(
                label = { Text(localizedStringResource(R.string.drawer_close)) },
                selected = false,
                onClick = onClose,
                icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) }
            )
        }
    }
}
