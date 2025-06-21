package com.example.ninhdt_btvn.ui.screen.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ninhdt_btvn.R

@Composable
fun PhotoUploadArea(
    onChangeImage: () -> Unit,
    selectedImage: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .border(
                width = 2.dp,
                color = colorResource(id = R.color.primary_color),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selectedImage != null) {
            AsyncImage(
                model = selectedImage,
                contentDescription = "Selected image",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )

            Image(
                painter = painterResource(id = R.drawable.ic_main_change_image),
                contentDescription = "Change image",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(50.dp)
                    .clickable { onChangeImage() }
                    .padding(top = 15.dp, start = 19.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onChangeImage() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_main_image),
                    contentDescription = "Add photo",
                    modifier = Modifier.size(80.dp),
                )

                Text(
                    text = stringResource(R.string.main_image_title),
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }
    }
}