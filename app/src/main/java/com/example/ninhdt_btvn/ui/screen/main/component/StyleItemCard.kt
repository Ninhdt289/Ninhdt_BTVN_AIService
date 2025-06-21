package com.example.ninhdt_btvn.ui.screen.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.aisevice.data.remote.model.StyleCategory
import com.example.aisevice.data.remote.model.StyleItem
import com.example.ninhdt_btvn.R


@Composable
fun StyleItemCard(
    styleItem: StyleItem,
    isSelected: Boolean = false,
    onSelect: (StyleItem) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = colorResource(id = R.color.style_selected_bg),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { onSelect(styleItem) }
        ) {
            UrlImageWithCoil(styleItem.key)
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(colorResource(R.color.style_selected_background))
                )
            }
        }

        Text(
            text = styleItem.name,
            fontSize = 12.sp,
            color = if (isSelected) colorResource(id = R.color.primary_color) else Color.Black
        )
    }
}

@Composable
fun StyleList(
    styles: List<StyleItem>,
    selectedStyle: StyleItem?,
    onStyleSelected: (StyleItem) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(styles) { styleItem ->
            StyleItemCard(
                styleItem = styleItem,
                isSelected = styleItem == selectedStyle,
                onSelect = onStyleSelected
            )
        }
    }
}

@Composable
fun StyleTabsWithContent(
    styleList: List<StyleCategory>,
    selectedStyle: StyleItem?,
    onStyleSelected: (StyleItem) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = colorResource(id = R.color.primary_color),
            edgePadding = 2.dp,
            indicator = { tabPositions ->
                val currentTab = tabPositions[selectedTabIndex]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.BottomStart)
                        .offset(x = currentTab.left + (currentTab.width - 16.dp) / 2)
                        .width(16.dp)
                        .height(2.dp)
                        .background(colorResource(id = R.color.primary_color), shape = CircleShape)
                )
            },
            divider = {}
        ) {
            styleList.forEachIndexed { index, category ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier
                        .height(22.dp),
                    text = {
                        Text(
                            text = category.name,
                            fontSize = 14.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) colorResource(id = R.color.primary_color) else Color.Gray,
                            maxLines = 1,
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        StyleList(
            styles = styleList[selectedTabIndex].styles,
            selectedStyle = selectedStyle,
            onStyleSelected = onStyleSelected
        )
    }
}

@Composable
fun StyleSelectionSection(
    styleList: List<StyleCategory>?,
    selectedStyle: StyleItem?,
    onStyleSelected: (StyleItem) -> Unit
) {
    Column() {
        Text(
            text = stringResource(R.string.main_style_title),
            color = colorResource(R.color.primary_color),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        if (!styleList.isNullOrEmpty()) {
            StyleTabsWithContent(
                styleList = styleList,
                selectedStyle = selectedStyle,
                onStyleSelected = onStyleSelected
            )
        }
    }
}

@Composable
fun StyleSelectionPlaceholder() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabNames = listOf("Tab 1", "Tab 2", "Tab 3")
    Column(
    ) {
        Text(
            text = stringResource(R.string.main_style_title),
            color = colorResource(id = R.color.primary_color),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            tabNames.forEachIndexed { index, name ->
                Column(
                    modifier = Modifier
                        .clickable { selectedTabIndex = index }
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = name,
                        fontSize = 14.sp,
                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTabIndex == index) colorResource(id = R.color.primary_color) else Color.Gray,
                        maxLines = 1,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(16.dp)
                            .background(
                                if (selectedTabIndex == index) colorResource(id = R.color.primary_color) else Color.Transparent,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
        // List style giả
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(5) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_main_image),
                            contentDescription = "Add photo",
                            modifier = Modifier.size(40.dp),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.LightGray.copy(alpha = 0.5f))
                    )
                }
            }
        }
    }
}

@Composable
fun UrlImageWithCoil(url: String) {
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StyleSelectionPlaceholder()
}
