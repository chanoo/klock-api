package app.klock.api.functional.auth.dto

import app.klock.api.domain.entity.Account

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

data class AccountResponse(
    val id: Long? = null,
    val username: String,
    val email: String
) {
    companion object {
        fun from(account: Account): AccountResponse {
            return AccountResponse(
                id = account.id,
                username = account.username,
                email = account.email
            )
        }
    }
}
