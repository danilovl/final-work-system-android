package com.finalworksystem.infrastructure.api

import com.finalworksystem.BuildConfig

object ApiConstant {
    const val BASE_URL = BuildConfig.BASE_URL
    const val API_KEY_VALUE = BuildConfig.API_KEY

    const val AUTH_KEY = "X-AUTH-API-KEY"
    const val AUTH_USER_TOKEN_KEY = "X-AUTH-USER-TOKEN"
    const val AUTH_USER_USERNAME = "X-AUTH-USER-USERNAME"

    const val API_PUBLIC_SECURITY_GENERATE_TOKEN = ApiEndpoint.Security.GENERATE_TOKEN

    const val API_KEY_CONVERSATION_DETAIL = ApiEndpoint.Conversation.GET_DETAIL
    const val API_KEY_CONVERSATION_LIST = ApiEndpoint.Conversation.GET_LIST
    const val API_KEY_CONVERSATION_MESSAGES = ApiEndpoint.Conversation.GET_MESSAGES
    const val API_KEY_CONVERSATION_MESSAGES_CHANGE_ALL_TO_READ = ApiEndpoint.Conversation.CHANGE_ALL_MESSAGES_TO_READ
    const val API_KEY_CONVERSATION_MESSAGE_CHANGE_READ_STATUS = ApiEndpoint.Conversation.CHANGE_MESSAGE_READ_STATUS
    const val API_KEY_CONVERSATION_MESSAGE_CREATE = ApiEndpoint.Conversation.CREATE_MESSAGE
    const val API_KEY_CONVERSATION_MESSAGE_WORK_LIST = ApiEndpoint.Conversation.Work.Message.GET_LIST
    const val API_KEY_CONVERSATION_WORK_DETAIL = ApiEndpoint.Conversation.Work.GET_DETAIL

    const val API_KEY_EVENT_WORK_LIST = ApiEndpoint.Event.Work.GET_LIST
    const val API_KEY_EVENT_CALENDAR_LIST = ApiEndpoint.Event.Calendar.GET_LIST
    const val API_KEY_EVENT_CALENDAR_USER_WORKS = ApiEndpoint.Event.Calendar.GET_USER_WORKS
    const val API_KEY_EVENT_CALENDAR_RESERVATION = ApiEndpoint.Event.Calendar.POST_EVENT_RESERVATION
    const val API_KEY_EVENT_CALENDAR_MANAGE_CREATE_DATA = ApiEndpoint.Event.Calendar.GET_MANAGE_CREATE_DATA
    const val API_KEY_EVENT_CALENDAR_CREATE = ApiEndpoint.Event.Calendar.POST_EVENT_CREATE
    const val API_KEY_EVENT_GET = ApiEndpoint.Event.Calendar.GET_EVENT
    const val API_KEY_EVENT_DELETE = ApiEndpoint.Event.Calendar.DELETE_EVENT
    const val API_KEY_SYSTEM_EVENT_ALL_CHANGE_VIEWED = ApiEndpoint.Event.System.CHANGE_ALL_VIEWED
    const val API_KEY_SYSTEM_EVENT_CHANGE_VIEWED = ApiEndpoint.Event.System.CHANGE_VIEWED
    const val API_KEY_SYSTEM_EVENT_LIST = ApiEndpoint.Event.System.GET_LIST

    const val API_KEY_TASK_LIST_OWNER = ApiEndpoint.Task.GET_LIST_OWNER
    const val API_KEY_TASK_LIST_SOLVER = ApiEndpoint.Task.GET_LIST_SOLVER
    const val API_KEY_TASK_WORK_DETAIL = ApiEndpoint.Task.Work.GET_DETAIL
    const val API_KEY_TASK_WORK_LIST = ApiEndpoint.Task.Work.GET_LIST
    const val API_KEY_TASK_WORK_CHANGE_STATUS = ApiEndpoint.Task.Work.CHANGE_STATUS
    const val API_KEY_TASK_WORK_NOTIFY_COMPLETE = ApiEndpoint.Task.Work.NOTIFY_COMPLETE
    const val API_KEY_TASK_WORK_DELETE = ApiEndpoint.Task.Work.DELETE

    const val API_KEY_USER_DETAIL = ApiEndpoint.User.GET_DETAIL
    const val API_KEY_USER_LIST = ApiEndpoint.User.GET_LIST
    const val API_KEY_USER_PROFILE_IMAGE = ApiEndpoint.User.GET_PROFILE_IMAGE

    const val API_KEY_VERSION_WORK_LIST = ApiEndpoint.Version.Work.GET_LIST

    const val API_KEY_WORK_DETAIL = ApiEndpoint.Work.GET_DETAIL
    const val API_KEY_WORK_LIST = ApiEndpoint.Work.GET_LIST

    object SystemEventType {
        const val READ = "read"
        const val UNREAD = "unread"
        const val ALL = "all"
    }
}
