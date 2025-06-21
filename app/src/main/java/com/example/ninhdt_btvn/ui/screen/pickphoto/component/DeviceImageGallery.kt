package com.example.ninhdt_btvn.ui.screen.pickphoto.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.aisevice.data.local.model.DeviceImage
import kotlinx.coroutines.flow.Flow
import androidx.paging.PagingData

@Composable
fun DeviceImageGallery(
    pagingDataFlow: Flow<PagingData<DeviceImage>>,
    selectedImageId: Long?,
    onToggleImage: (DeviceImage) -> Unit,
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState
) {
    val lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems()
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = lazyGridState,
        modifier = modifier
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = { index ->
                lazyPagingItems[index]?.id ?: index
            }
        ) { index ->
            val image = lazyPagingItems[index]
            if (image != null) {
                DeviceImageItem(
                    image = image,
                    isSelected = selectedImageId == image.id,
                    onToggle = onToggleImage,
                    modifier = Modifier.aspectRatio(1f)
                )
            }
        }

        when {
            lazyPagingItems.loadState.refresh is LoadState.Loading -> {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(3) }) {
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
            lazyPagingItems.loadState.append is LoadState.Loading -> {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(3) }) {
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
            lazyPagingItems.loadState.refresh is LoadState.Error -> {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(3) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error loading images")
                    }
                }
            }
        }
    }
}
