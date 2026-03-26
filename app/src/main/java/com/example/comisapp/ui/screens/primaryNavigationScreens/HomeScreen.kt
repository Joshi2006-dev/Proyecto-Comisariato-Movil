package com.example.comisapp.ui.screens.primaryNavigationScreens

import android.service.autofill.OnClickAction
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.comisapp.R
import com.example.comisapp.ui.theme.CardBorderColor
import com.example.comisapp.ui.theme.TextPurple
import kotlinx.coroutines.launch

data class DrawerMenuItem(
    val icon: ImageVector,
    val label: String,
    val onClick: ()-> Unit
)

data class ProfileMenuItem(
    val icon: ImageVector,
    val label: String,
    val onClick: ()-> Unit
)

@Composable
fun HomeScreen(
    onNavigateToCuenta: ()->Unit = {},
    onNavigateToCentroAyuda: ()->Unit = {},
    onNavigateToSugerencias: ()->Unit = {},
    onLogOut: ()-> Unit = {},
    onNotificationsClick: () -> Unit = {},
    profileImageUrl: String? = null,
    content: @Composable (PaddingValues)-> Unit
){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val drawerMenuItems = listOf(
        DrawerMenuItem(
            icon = ImageVector.vectorResource(R.drawable.ic_profile),
            label = "Cuenta",
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToCuenta()
                }
            }
        ) ,
        DrawerMenuItem(
            icon = ImageVector.vectorResource(R.drawable.ic_support),
            label = "Centro de Ayuda",
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToCentroAyuda()
                }
            }
        ),
        DrawerMenuItem(
            icon = ImageVector.vectorResource(R.drawable.ic_ideas),
            label = "Segurencias",
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToSugerencias()
                }
            }
        ),
        DrawerMenuItem(
            icon = ImageVector.vectorResource(R.drawable.ic_logout),
            label = "Cerrar Sesion",
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToCentroAyuda()
                }
            }
        )
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppSidebarContent(
                items = drawerMenuItems,
                onClose = { scope.launch { drawerState.close() } },
                profileImageUrl = profileImageUrl
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppHeader(
                    profileImageUrl = profileImageUrl,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onNavigateToCuenta = onNavigateToCuenta,
                    onCerrarSesion = onLogOut,
                    onNotificationsClick = onNotificationsClick
                )
            },
            content = content
        )
    }


}

@Composable
fun AppSidebarContent(
    items: List<DrawerMenuItem>,
    onClose: () -> Unit = {},
    profileImageUrl: String?
) {
    ModalDrawerSheet(
        modifier = Modifier.width(280.dp),
        drawerContainerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 5.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Cerrar menú",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileImageUrl != null) {
                            AsyncImage(
                                model = profileImageUrl,
                                contentDescription = "Foto de perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "Perfil",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                }

            }
            Spacer(modifier = Modifier.height(10.dp))
            Column (
            ) {
                Text("Josué Vasquez"
                , fontWeight = FontWeight.Bold,
                    fontSize = 20.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Surface(
                    shape = RoundedCornerShape(15.dp),
                    color = TextPurple.copy(alpha = 0.05f)
                ) {Text("vasquezponcejosuedaniel@gmail.com", modifier = Modifier.padding(horizontal = 10.dp, vertical = 1.dp),
                    color = TextPurple,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.sp
                    )
                }

            }
            Spacer(modifier = Modifier.height(10.dp))



        }


        Spacer(modifier = Modifier.height(12.dp))

        items.forEach { item ->
            val isDestructive = item.label == "Cerrar Sesión"

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isDestructive)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            color = if (isDestructive)
                                MaterialTheme.colorScheme.error
                            else
                                CardBorderColor
                        )
                    )
                },
                selected = false,
                onClick = item.onClick,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent
                )
            )

            if (isDestructive.not() && item == items[2]) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(
    profileImageUrl: String? = null,
    onMenuClick: () -> Unit = {},
    onNavigateToCuenta: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}

) {
    CenterAlignedTopAppBar(
        modifier = Modifier.shadow(elevation = 0.5.dp).padding(horizontal = 5.dp),
        windowInsets = WindowInsets(0),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        ),
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Abrir menú",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Comis",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                )
                Text(
                    text = "App",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPurple
                    )
                )
            }
        },
        actions = {

            IconButton(
                onClick = {
                    onNotificationsClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notificaciones",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            ProfilePopover(
                profileImageUrl = profileImageUrl,
                onNavigateToCuenta = onNavigateToCuenta,
                onCerrarSesion = onCerrarSesion
            )

            Spacer(modifier = Modifier.width(4.dp))
        }
    )
}

@Composable
fun ProfilePopover(
    profileImageUrl: String? = null,
    onNavigateToCuenta: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    val profileMenuItems = listOf(
        ProfileMenuItem(
            icon = Icons.Outlined.ManageAccounts,
            label = "Cuenta",
            onClick = {
                expanded = false
                onNavigateToCuenta()
            }
        ),
        ProfileMenuItem(
            icon = Icons.Outlined.Logout,
            label = "Cerrar Sesión",
            onClick = {
                expanded = false
                onCerrarSesion()
            }
        )
    )

    Box {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.Black)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { expanded = true },
            contentAlignment = Alignment.Center
        ) {
            if (profileImageUrl != null) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Perfil",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = 0.dp,y = 15.dp),
            modifier = Modifier
                .width(180.dp)
                .background(Color.White)
                .clip(RoundedCornerShape(15.dp))
        ) {
            profileMenuItems.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (item.label == "Cerrar Sesión")
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    onClick = item.onClick,
                    colors = MenuDefaults.itemColors(
                        textColor = if (item.label == "Cerrar Sesión")
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}
