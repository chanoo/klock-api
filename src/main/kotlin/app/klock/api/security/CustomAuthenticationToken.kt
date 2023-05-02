package app.klock.api.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class CustomAuthenticationToken(
  private val userId: Long,
  private val token: String,
  authorities: Collection<GrantedAuthority>
) : AbstractAuthenticationToken(authorities) {

  override fun getCredentials(): Any {
    return token
  }

  override fun getPrincipal(): Any {
    return userId
  }

  override fun isAuthenticated(): Boolean {
    return true
  }
}
