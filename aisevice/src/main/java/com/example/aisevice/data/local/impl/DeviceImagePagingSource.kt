package com.example.aisevice.data.local.impl

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.aisevice.data.local.model.DeviceImage
import com.example.aisevice.data.local.repository.ImageRepository

class DeviceImagePagingSource(
    private val imageRepository: ImageRepository
) : PagingSource<Int, DeviceImage>() {

    override fun getRefreshKey(state: PagingState<Int, DeviceImage>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DeviceImage> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            
            val images = imageRepository.getDeviceImages(page * pageSize, pageSize)
            
            LoadResult.Page(
                data = images,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (images.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
} 