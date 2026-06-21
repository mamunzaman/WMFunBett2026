package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.SurfaceDark

const val SAMPLE_DATA_NOTICE = "Offline mode · data saved in memory only"

val HierarchyListContentPadding = PaddingValues(
    start = 20.dp,
    end = 20.dp,
    top = 8.dp,
    bottom = 24.dp
)

fun hierarchyContentPadding(withFab: Boolean): PaddingValues {
    if (!withFab) return HierarchyListContentPadding
    return PaddingValues(
        start = 20.dp,
        end = 20.dp,
        top = 8.dp,
        bottom = 88.dp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HierarchyScreenLayout(
    title: String,
    breadcrumbs: List<String>,
    onBackClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    onFabClick: (() -> Unit)? = null,
    fabContentDescription: String = "Add",
    onSetResultClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    content: @Composable (Modifier) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val hasMenu = onSetResultClick != null || onDeleteClick != null

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    if (hasMenu) {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More options"
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                if (onSetResultClick != null) {
                                    DropdownMenuItem(
                                        text = { Text("Set Result") },
                                        onClick = {
                                            showMenu = false
                                            onSetResultClick()
                                        }
                                    )
                                }
                                if (onDeleteClick != null) {
                                    DropdownMenuItem(
                                        text = { Text("Delete", color = DangerRed) },
                                        onClick = {
                                            showMenu = false
                                            onDeleteClick()
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark,
                    titleContentColor = PrimaryText,
                    navigationIconContentColor = PrimaryText,
                    actionIconContentColor = PrimaryText
                )
            )
            HierarchyBreadcrumb(breadcrumbs = breadcrumbs)
            content(Modifier.fillMaxSize())
        }

        if (onFabClick != null) {
            FloatingActionButton(
                onClick = onFabClick,
                modifier = Modifier
                    .align(androidx.compose.ui.Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 16.dp),
                containerColor = PrimaryBlue,
                contentColor = PrimaryText
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = fabContentDescription,
                    tint = JackpotGold
                )
            }
        }
    }
}

@Composable
fun SampleDataNotice(modifier: Modifier = Modifier) {
    Text(
        text = SAMPLE_DATA_NOTICE,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        style = MaterialTheme.typography.bodySmall,
        color = SecondaryText
    )
}

@Composable
fun HierarchySectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = PrimaryText
    )
}

@Composable
fun HierarchyBreadcrumb(
    breadcrumbs: List<String>,
    modifier: Modifier = Modifier
) {
    if (breadcrumbs.isEmpty()) return

    Text(
        text = breadcrumbs.joinToString("  ›  "),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        style = MaterialTheme.typography.labelMedium,
        color = SecondaryText,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun <T> HierarchyListContent(
    items: List<T>,
    emptyMessage: String,
    modifier: Modifier = Modifier,
    sectionTitle: String? = null,
    showSampleNotice: Boolean = true,
    key: (T) -> Any = { it.hashCode() },
    itemContent: @Composable (T) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = HierarchyListContentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (showSampleNotice) {
            item(key = "sample_notice") {
                SampleDataNotice()
            }
        }
        if (sectionTitle != null) {
            item(key = "section_header") {
                HierarchySectionHeader(title = sectionTitle)
            }
        }
        if (items.isEmpty()) {
            item(key = "empty_state") {
                PlaceholderCard(message = emptyMessage)
            }
        } else {
            items(items, key = key) { item ->
                itemContent(item)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavListCard(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = SurfaceDark),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
            }
        }
    }
}

@Composable
fun EntryListCard(
    name: String,
    prediction: String,
    amount: String,
    note: String? = null,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    androidx.compose.material3.Card(
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = SurfaceDark),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 8.dp, top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = prediction,
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
                if (!note.isNullOrBlank()) {
                    Text(
                        text = note,
                        modifier = Modifier.padding(top = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )
                }
            }
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = JackpotGold
            )
            if (onDelete != null) {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Entry options"
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete", color = DangerRed) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}
