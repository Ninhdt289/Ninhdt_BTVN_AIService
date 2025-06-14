package com.example.ninhdt_btvn.ui.screen.pickphoto.component

import com.example.aisevice.data.local.model.DeviceImage
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun DeviceImageGallery(
    images: List<DeviceImage>,
    selectedImageId: Long?,
    onToggleImage: (Long) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState()
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            images.isEmpty() && !isLoading -> {
                Text(
                    text = "Không tìm thấy ảnh nào trong thiết bị",
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
            else -> {
                LazyVerticalGrid(
                    state = lazyGridState,
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(images) { image ->
                        DeviceImageItem(
                            image = image,
                            isSelected = image.id == selectedImageId,
                            onToggle = { onToggleImage(image.id) }
                        )
                    }
                    
                    if (isLoading) {
                        item(span = { GridItemSpan(3) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}
