package com.example.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.MayaCardBorder
import com.example.ui.theme.MayaCardSurface
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaPurpleNeon
import com.example.ui.theme.MayaTextMuted
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary

@Composable
fun MayaDrawerContent(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onCloseDrawer: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight()
            .testTag("maya_navigation_drawer"),
        color = MayaNavyBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF0E223D),
                                MayaNavyBackground
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.maya_avatar),
                            contentDescription = "Maya Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Maya",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MayaTextPrimary
                        )
                        Text(
                            text = "by The Hunter AI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MayaCyanNeon
                        )
                    }
                }
            }

            HorizontalDivider(color = MayaCardBorder.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(12.dp))

            // SECTION: HOME
            DrawerSectionHeader(title = "HOME")
            DrawerItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = currentRoute == "home",
                tag = "drawer_item_home",
                onClick = { onNavigate("home"); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.AutoAwesome,
                label = "Memories",
                isSelected = currentRoute == "memories",
                tag = "drawer_item_memories",
                onClick = { onNavigate("memories"); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.ChatBubbleOutline,
                label = "Chat",
                isSelected = currentRoute == "chat",
                tag = "drawer_item_chat",
                onClick = { onNavigate("chat"); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.VolunteerActivism,
                label = "Journal & Mood",
                isSelected = currentRoute == "journal",
                tag = "drawer_item_journal",
                onClick = { onNavigate("journal"); onCloseDrawer() }
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MayaCardBorder.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(12.dp))

            // SECTION: PRODUCTIVITY
            DrawerSectionHeader(title = "PRODUCTIVITY")
            DrawerItem(
                icon = Icons.Default.ShowChart,
                label = "Markets",
                isSelected = currentRoute == "markets",
                tag = "drawer_item_markets",
                onClick = { onNavigate("markets"); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.FolderOpen,
                label = "Documents",
                isSelected = currentRoute == "documents",
                tag = "drawer_item_documents",
                onClick = { onNavigate("documents"); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.Draw,
                label = "Study & Whiteboard",
                isSelected = currentRoute == "study",
                tag = "drawer_item_study",
                onClick = { onNavigate("study"); onCloseDrawer() }
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MayaCardBorder.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(12.dp))

            // SECTION: SYSTEM
            DrawerSectionHeader(title = "SYSTEM")
            DrawerItem(
                icon = Icons.Default.Settings,
                label = "Settings",
                isSelected = currentRoute == "settings",
                tag = "drawer_item_settings",
                onClick = { onNavigate("settings"); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.Gavel,
                label = "Maya Rules",
                isSelected = currentRoute == "rules",
                tag = "drawer_item_rules",
                onClick = { onNavigate("rules"); onCloseDrawer() }
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MayaCardBorder.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(12.dp))

            // SECTION: OTHER
            DrawerSectionHeader(title = "OTHER")
            DrawerItem(
                icon = Icons.Default.Star,
                label = "Upgrade to Pro",
                isSelected = currentRoute == "upgrade",
                tag = "drawer_item_upgrade",
                accentColor = MayaPurpleNeon,
                onClick = { onNavigate("upgrade"); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.Notifications,
                label = "Notifications",
                isSelected = currentRoute == "notifications",
                tag = "drawer_item_notifications",
                onClick = { onNavigate("notifications"); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.PrivacyTip,
                label = "Privacy Policy",
                isSelected = currentRoute == "privacy",
                tag = "drawer_item_privacy",
                onClick = { onNavigate("privacy"); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.Info,
                label = "About Maya",
                isSelected = currentRoute == "about",
                tag = "drawer_item_about",
                onClick = { onNavigate("about"); onCloseDrawer() }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = MayaTextMuted,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
    )
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    tag: String,
    accentColor: Color = MayaCyanNeon,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MayaCardSurface else Color.Transparent
    val contentColor = if (isSelected) accentColor else MayaTextSecondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) MayaTextPrimary else MayaTextSecondary
        )
    }
}
