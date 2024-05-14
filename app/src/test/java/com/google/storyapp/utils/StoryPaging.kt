package com.google.storyapp.utils

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.storyapp.remote.response.Story

class StoryPaging : PagingSource<Int, LiveData<List<Story>>>() {

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

    companion object {
        fun getSnapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }
}