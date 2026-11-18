package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.SavedAddress
import com.example.data.ZivoraCatalog
import com.example.ui.theme.ZivoraOrange
import com.example.ui.theme.ZivoraPeachContainer
import com.example.ui.theme.ZivoraPureWhite
import kotlin.math.roundToInt

@Composable
fun ZivoraLogoBadge(
    size: Dp = 44.dp,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(size),
        shape = RoundedCornerShape(size * 0.28f),
        color = ZivoraOrange,
        shadowElevation = 4.dp
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_icon_zivora),
            contentDescription = "ZIVORA Logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(size)
        )
    }
}

@Composable
fun ZivoraStatusPill(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = containerColor
    ) {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

fun formatRupees(amount: Double): String {
    val rounded = amount.roundToInt()
    return "₹$rounded"
}

@Composable
fun ZivoraQuantityStepper(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    itemTagPrefix: String,
    modifier: Modifier = Modifier
) {
    if (quantity <= 0) {
        Button(
            onClick = onIncrement,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier = modifier
                .minimumInteractiveComponentSize()
                .testTag("${itemTagPrefix}_add_btn")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add item",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    } else {
        Surface(
            modifier = modifier
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, ZivoraOrange, RoundedCornerShape(10.dp)),
            color = ZivoraOrange
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                IconButton(
                    onClick = onDecrement,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("${itemTagPrefix}_dec_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease quantity",
                        tint = ZivoraPureWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = quantity.toString(),
                    color = ZivoraPureWhite,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                IconButton(
                    onClick = onIncrement,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("${itemTagPrefix}_inc_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase quantity",
                        tint = ZivoraPureWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun VegIndicator(isVeg: Boolean, modifier: Modifier = Modifier) {
    val color = if (isVeg) Color(0xFF16A34A) else Color(0xFFDC2626)
    Box(
        modifier = modifier
            .size(14.dp)
            .border(1.5.dp, color, RoundedCornerShape(3.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}

@Composable
fun AddressPickerDialog(
    currentAddress: String,
    savedAddresses: List<SavedAddress>,
    onSelectAddress: (String) -> Unit,
    onAddAddress: (label: String, address: String, landmark: String) -> Unit,
    onDismiss: () -> Unit
) {
    var showAddForm by remember { mutableStateOf(false) }
    var newLabel by remember { mutableStateOf("Home") }
    var newFullAddress by remember { mutableStateOf("") }
    var newLandmark by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = if (showAddForm) "Add New Address" else "Select Active Location",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            if (showAddForm) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newLabel,
                        onValueChange = { if (it.length <= 30) newLabel = it },
                        label = { Text("Label (e.g. Home, Work)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_address_label_input")
                    )
                    OutlinedTextField(
                        value = newFullAddress,
                        onValueChange = { if (it.length <= 200) newFullAddress = it },
                        label = { Text("Full Street / Sector Address") },
                        maxLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_address_full_input")
                    )
                    OutlinedTextField(
                        value = newLandmark,
                        onValueChange = { if (it.length <= 100) newLandmark = it },
                        label = { Text("Landmark (optional)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_address_landmark_input")
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 340.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (savedAddresses.isNotEmpty()) {
                        item {
                            Text(
                                "Saved Addresses",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(savedAddresses, key = { it.id }) { addr ->
                            val isSelected = addr.fullAddress == currentAddress
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) ZivoraPeachContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) BorderStroke(1.5.dp, ZivoraOrange) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectAddress(addr.fullAddress)
                                        onDismiss()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = ZivoraOrange
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(addr.label, style = MaterialTheme.typography.titleSmall)
                                        Text(
                                            addr.fullAddress,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = ZivoraOrange
                                        )
                                    }
                                }
                            }
                        }
                        item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }
                    }
                    item {
                        Text(
                            "Popular City Hubs",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(ZivoraCatalog.popularLocations) { hub ->
                        val isSelected = hub == currentAddress
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ZivoraPeachContainer else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) ZivoraOrange else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectAddress(hub)
                                    onDismiss()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = if (isSelected) ZivoraOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    hub,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (showAddForm) {
                Button(
                    onClick = {
                        if (newLabel.isNotBlank() && newFullAddress.trim().length >= 3) {
                            onAddAddress(newLabel.trim(), newFullAddress.trim(), newLandmark.trim())
                            onDismiss()
                        }
                    },
                    enabled = newLabel.isNotBlank() && newFullAddress.trim().length >= 3,
                    modifier = Modifier.testTag("save_new_address_button")
                ) {
                    Text("Save & Use")
                }
            } else {
                OutlinedButton(
                    onClick = { showAddForm = true },
                    modifier = Modifier.testTag("add_new_address_trigger")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Address")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                if (showAddForm) showAddForm = false else onDismiss()
            }) {
                Text(if (showAddForm) "Back" else "Close")
            }
        }
    )
}
