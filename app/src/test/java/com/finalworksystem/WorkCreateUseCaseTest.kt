package com.finalworksystem

import com.finalworksystem.application.use_case.work.GetWorkCategoriesUseCase
import com.finalworksystem.application.use_case.work.GetWorkStatusesUseCase
import com.finalworksystem.application.use_case.work.GetWorkTypesUseCase
import com.finalworksystem.application.use_case.work.PostWorkCreateUseCase
import com.finalworksystem.data.work.model.request.WorkCreateRequest
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.domain.work.model.WorkCategory
import com.finalworksystem.domain.work.model.WorkStatus
import com.finalworksystem.domain.work.model.WorkType
import com.finalworksystem.domain.work.repository.GetWorkRepository
import com.finalworksystem.domain.work.repository.PostWorkRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import io.mockk.every
import io.mockk.mockk

class WorkCreateUseCaseTest {

    @Test
    fun testGetWorkTypesStatusesCategoriesUseCases() = runTest {

        val repository = mockk<GetWorkRepository>()
        val expectedTypes = listOf(WorkType(1, "Type 1", "Desc 1"))
        val expectedStatuses = listOf(WorkStatus(1, "Status 1", "Desc 1", "#FFFFFF"))
        val expectedCategories = listOf(WorkCategory(1, "Category 1", "Desc 1", 0))

        every { repository.getWorkTypes() } returns flowOf(Result.success(expectedTypes))
        every { repository.getWorkStatuses() } returns flowOf(Result.success(expectedStatuses))
        every { repository.getWorkCategories() } returns flowOf(Result.success(expectedCategories))

        val typesUseCase = GetWorkTypesUseCase(repository)
        val statusesUseCase = GetWorkStatusesUseCase(repository)
        val categoriesUseCase = GetWorkCategoriesUseCase(repository)

        val typesResult = typesUseCase().first()
        val statusesResult = statusesUseCase().first()
        val categoriesResult = categoriesUseCase().first()

        assertTrue(typesResult.isSuccess)
        assertEquals(expectedTypes, typesResult.getOrNull())

        assertTrue(statusesResult.isSuccess)
        assertEquals(expectedStatuses, statusesResult.getOrNull())

        assertTrue(categoriesResult.isSuccess)
        assertEquals(expectedCategories, categoriesResult.getOrNull())
    }

    @Test
    fun testPostWorkCreateUseCase() = runTest {

        val repository = mockk<PostWorkRepository>()
        val request = WorkCreateRequest(
            title = "Test Work",
            typeId = 1,
            statusId = 1,
            deadline = "2026-10-10"
        )
        val expectedWork = mockk<Work> { every { id } returns 100 }

        every { repository.postWorkCreate(request) } returns flowOf(Result.success(expectedWork))

        val useCase = PostWorkCreateUseCase(repository)
        val result = useCase(request).first()

        assertTrue(result.isSuccess)
        assertEquals(100, result.getOrNull()?.id)
    }
}
