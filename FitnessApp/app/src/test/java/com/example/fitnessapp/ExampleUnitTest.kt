package com.example.fitnessapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fitnessapp.data.model.LoginRequest
import kotlinx.coroutines.Dispatchers
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class WaterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeApi: FakeWaterApi
    private lateinit var viewModel: WaterViewModelForTest

    @Before
    fun setup() {
        fakeApi = FakeWaterApi(shouldSucceed = true, todayValue = 500)
        viewModel = WaterViewModelForTest(fakeApi, mainDispatcherRule.dispatcher)
    }

    @Test
    fun add_water_success_updates_dailyWater() = runTest {
        viewModel.addWater(1, 250)
        advanceUntilIdle()
        Assert.assertEquals(500, viewModel.dailyWater.value)
    }

    @Test
    fun add_water_failure_sets_error() = runTest {
        fakeApi = FakeWaterApi(shouldSucceed = false)
        viewModel = WaterViewModelForTest(fakeApi, mainDispatcherRule.dispatcher)
        viewModel.addWater(1, 250)
        advanceUntilIdle()
        Assert.assertEquals("Ошибка при добавлении воды", viewModel.error.value)
    }
}