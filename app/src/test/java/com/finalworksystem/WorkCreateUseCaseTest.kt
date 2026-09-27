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
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class WorkCreateUseCaseTest {

    @Test
    fun testGetWorkTypesStatusesCategoriesUseCases() = runBlocking {
        println("[DEBUG_LOG] Testing GetWorkTypesUseCase, GetWorkStatusesUseCase, GetWorkCategoriesUseCase")

        val repository = mock(GetWorkRepository::class.java)
        val expectedTypes = listOf(WorkType(1, "Type 1", "Desc 1"))
        val expectedStatuses = listOf(WorkStatus(1, "Status 1", "Desc 1", "#FFFFFF"))
        val expectedCategories = listOf(WorkCategory(1, "Category 1", "Desc 1", 0))

        `when`(repository.getWorkTypes()).thenReturn(flowOf(Result.success(expectedTypes)))
        `when`(repository.getWorkStatuses()).thenReturn(flowOf(Result.success(expectedStatuses)))
        `when`(repository.getWorkCategories()).thenReturn(flowOf(Result.success(expectedCategories)))

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

        println("[DEBUG_LOG] All 3 form options UseCases tests passed successfully")
    }

    @Test
    fun testPostWorkCreateUseCase() = runBlocking {
        println("[DEBUG_LOG] Testing PostWorkCreateUseCase")

        val repository = mock(PostWorkRepository::class.java)
        val request = WorkCreateRequest(
            title = "Test Work",
            typeId = 1,
            statusId = 1,
            deadline = "2026-10-10"
        )
        val expectedWork = mock(Work::class.java)
        `when`(expectedWork.id).thenReturn(100)

        `when`(repository.postWorkCreate(request)).thenReturn(flowOf(Result.success(expectedWork)))

        val useCase = PostWorkCreateUseCase(repository)
        val result = useCase(request).first()

        assertTrue(result.isSuccess)
        assertEquals(100, result.getOrNull()?.id)

        println("[DEBUG_LOG] PostWorkCreateUseCase test passed successfully")
    }
}
