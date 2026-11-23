package com.finalworksystem.presentation.paging

import androidx.paging.PagingData
import com.finalworksystem.application.use_case.user.GetUserListUseCase
import com.finalworksystem.domain.user.model.UserType
import com.finalworksystem.domain.user.model.UserWithWorks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Paged, searchable user lists for the user selection dialogs (one list per user type).
 */
class UserPicker(
    scope: CoroutineScope,
    getUserListUseCase: GetUserListUseCase,
    pageSize: Int = 20
) {
    private val lists: Map<UserType, PagedList<String, UserWithWorks>> =
        UserType.entries.associateWith { type ->
            PagedList(scope, pageSize) { search, page, size ->
                getUserListUseCase(type = type, page = page, pageSize = size, search = search.ifBlank { null })
                    .first()
                    .map { Page(it.result, it.totalCount) }
            }
        }

    fun users(type: UserType): Flow<PagingData<UserWithWorks>> = list(type).items

    fun searchQuery(type: UserType): String = list(type).query.orEmpty()

    fun search(type: UserType, query: String) {
        list(type).load(query.trim())
    }

    private fun list(type: UserType) = lists.getValue(type)
}
