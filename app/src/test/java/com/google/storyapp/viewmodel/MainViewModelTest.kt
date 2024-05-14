package com.google.storyapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.google.storyapp.adapter.StoryAdapter
import com.google.storyapp.remote.response.Story
import com.google.storyapp.utils.DataDummy
import com.google.storyapp.utils.MainDispatcherRule
import com.google.storyapp.utils.StoryPaging
import com.google.storyapp.utils.getOrAwaitValue
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: Repository
    private lateinit var viewModel: MainViewModel
    private val dummyStory = DataDummy.dummyStories()
    private val testCoroutineScheduler = TestCoroutineScheduler()
    private val standardTestDispatcher = StandardTestDispatcher(testCoroutineScheduler)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        viewModel = MainViewModel(repository)
        Dispatchers.setMain(standardTestDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val data: PagingData<Story> = StoryPaging.getSnapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<Story>>()
        expectedStory.value = data
        `when`(repository.getStory()).thenReturn(expectedStory)

        val actualStory: PagingData<Story> = viewModel.getStories().getOrAwaitValue()

        val dataDiffer = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        dataDiffer.submitData(actualStory)

        assertNotNull(dataDiffer.snapshot())
        assertEquals(dummyStory.size, dataDiffer.snapshot().size)
        assertEquals(dummyStory[0], dataDiffer.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val emptyList = emptyList<Story>()
        val expectedEmptyStory = MutableLiveData<PagingData<Story>>()
        expectedEmptyStory.value = PagingData.from(emptyList)
        `when`(repository.getStory()).thenReturn(expectedEmptyStory)

        val actualEmptyStory: PagingData<Story> = viewModel.getStories().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualEmptyStory)
        assertNotNull(differ.snapshot())
        Assert.assertEquals(0, differ.snapshot().size)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}