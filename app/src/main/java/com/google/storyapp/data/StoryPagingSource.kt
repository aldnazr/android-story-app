package com.google.storyapp.data

import androidx.paging.LoadState
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.storyapp.preference.LoginPreferences
import com.google.storyapp.remote.response.Story
import com.google.storyapp.remote.retrofit.ApiService

class StoryPagingSource(private val pref: LoginPreferences, private val apiService: ApiService) :
    PagingSource<Int, Story>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = pref.getUser().token.toString()
            if (token.isEmpty()) {
                LoadState.Error(Exception("Token is empty"))
            }
            val responseData = apiService.getStories("Bearer $token", position, params.loadSize, 0)
            if (!responseData.isSuccessful) {
                LoadState.Error(Exception("Failed to load"))
            }
            LoadResult.Page(
                data = responseData.body()?.listStory ?: emptyList(),
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.body()?.listStory.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}