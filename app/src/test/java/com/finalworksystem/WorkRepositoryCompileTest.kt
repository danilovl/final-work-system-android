package com.finalworksystem

import com.finalworksystem.data.user.model.User
import com.finalworksystem.data.work.model.Work
import com.finalworksystem.data.work.model.WorkStatus
import com.finalworksystem.data.work.model.WorkType
import com.finalworksystem.data.work.model.toDomainModel
import org.junit.Test

class WorkRepositoryCompileTest {

    @Test
    fun testToDomainModelCompilation() {
        val mockWorkType = WorkType(1, "Test Type", "Test Description")
        val mockWorkStatus = WorkStatus(1, "Test Status", "Test Description")
        val mockUser = User(
            id = 1,
            username = "testuser",
            firstname = "Test",
            lastname = "User",
            fullName = "Test User",
            degreeBefore = null,
            degreeAfter = null,
            email = "test@example.com",
            token = "token",
            roles = emptyList()
        )

        val mockWork = Work(
            id = 1,
            title = "Test Work",
            shortcut = "TW",
            type = mockWorkType,
            status = mockWorkStatus,
            deadline = "2024-01-01",
            deadlineProgram = null,
            author = mockUser,
            supervisor = mockUser,
            opponent = null,
            consultant = null
        )

        val domainWork = mockWork.toDomainModel()

        assert(domainWork.id == mockWork.id)
        assert(domainWork.title == mockWork.title)
    }
}
