package com.finalworksystem.infrastructure.api

object ApiEndpoint {
    object User {
        const val GET_DETAIL = "/api/key/users/detail"
        const val GET_LIST = "/api/key/users/{type}"
        const val GET_PROFILE_IMAGE = "/api/key/users/{id}/profile/image"
    }

    object Work {
        const val GET_LIST = "/api/key/works/{type}"
        const val GET_DETAIL = "/api/key/works/{id}/detail"
    }

    object Event {
        object System {
            const val GET_LIST = "/api/key/system-events/{type}"
            const val CHANGE_VIEWED = "/api/key/system-events/{id}/viewed"
            const val CHANGE_ALL_VIEWED = "/api/key/system-events/viewed-all"
        }

        object Work {
            const val GET_LIST = "/api/key/events/works/{id}"
        }

        object Calendar {
            const val GET_LIST = "/api/key/events/calendar/{type}"
            const val GET_USER_WORKS = "/api/key/events/calendar/reservation/user/works"
            const val POST_EVENT_RESERVATION = "/api/key/events/calendar/reservation/events/{id_event}/works/{id_work}"
            const val GET_MANAGE_CREATE_DATA = "/api/key/events/calendar/manage/create/data"
            const val POST_EVENT_CREATE = "/api/key/events/calendar/manage/create"
            const val GET_EVENT = "/api/key/events/{id}"
            const val DELETE_EVENT = "/api/key/events/{id}"
        }
    }

    object Version {
        object Work {
            const val GET_LIST = "/api/key/versions/works/{id}"
        }
    }

    object Conversation {
        const val GET_LIST = "/api/key/conversations/"
        const val GET_DETAIL = "/api/key/conversations/{id}"
        const val GET_MESSAGES = "/api/key/conversations/{id}/messages"
        const val CREATE_MESSAGE = "/api/key/conversations/{id}/message"
        const val CHANGE_MESSAGE_READ_STATUS = "/api/key/conversations/{id_conversation}/messages/{id_message}/change/read/status"
        const val CHANGE_ALL_MESSAGES_TO_READ = "/api/key/conversations/messages/change-all-to-read"

        object Work {
            const val GET_DETAIL = "/api/key/conversations/works/{id}"

            object Message {
                const val GET_LIST = "/api/key/conversations/works/{id}/messages"
            }
        }
    }

    object Task {
        object Work {
            const val GET_LIST = "/api-platform/task/work/{id}/list"
            const val GET_DETAIL = "api/key/tasks/{id_task}/works/{id_work}"
            const val CHANGE_STATUS = "/api/key/tasks/{id_task}/works/{id_work}/change/{type}"
            const val NOTIFY_COMPLETE = "/api/key/tasks/{id_task}/works/{id_work}/notify/complete"
            const val DELETE = "/api/key/tasks/{id_task}/works/{id_work}"
        }

        const val GET_LIST_OWNER = "/api-platform/task/list-owner"
        const val GET_LIST_SOLVER = "/api-platform/task/list-solver"
    }

    object Security {
        const val GENERATE_TOKEN = "/api/public/security/generate-token"
    }
}
