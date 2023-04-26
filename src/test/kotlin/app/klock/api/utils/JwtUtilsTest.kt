package app.klock.api.utils

import app.klock.api.domain.entity.AccountRole
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.GrantedAuthority
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("local")
class JwtUtilsTest {

  private lateinit var jwtUtils: JwtUtils

  @BeforeEach
  fun setUp() {
    jwtUtils = JwtUtils()
  }

  @Test
  fun generateTokenTest() {
    val userId = "1"
    val userRole = listOf(AccountRole.USER.name)
    val token = jwtUtils.generateToken(userId, userRole)
    assertTrue(jwtUtils.validateToken(token))

    val extractedUserId = jwtUtils.getUserIdFromToken(token)
    assertEquals(userId, extractedUserId)
  }

  @Test
  fun refreshTokenTest() {
    val userId = "1"
    val userRoles = listOf(AccountRole.USER.name)
    val refreshToken = jwtUtils.generateRefreshToken(userId, userRoles)
    assertTrue(jwtUtils.validateRefreshToken(refreshToken))

    val extractedUserId = jwtUtils.getUserIdFromToken(refreshToken)
    assertEquals(userId, extractedUserId)
  }

  @Test
  fun validateTokenTest() {
    val userId = "1"
    val userRole = listOf(AccountRole.USER.name)
    val token = jwtUtils.generateToken(userId, userRole)

    assertTrue(jwtUtils.validateToken(token))
  }

  @Test
  fun getUserIdFromTokenTest() {
    val userId = "1"
    val userRole = listOf(AccountRole.USER.name)
    val token = jwtUtils.generateToken(userId, userRole)

    assertEquals(userId, jwtUtils.getUserIdFromToken(token))
  }

  @Test
  fun getAuthoritiesFromJwtTest() {
    val userId = "testUser"
    val roles = listOf(AccountRole.USER.name, AccountRole.ADMIN.name)

    // JWT 토큰에 역할을 포함하여 생성
    val token = jwtUtils.generateToken(userId, roles)

    // 토큰에서 역할 목록 추출
    val authorities: List<GrantedAuthority> = jwtUtils.getAuthoritiesFromJwt(token)

    // 추출된 역할 목록이 올바른지 확인
    assertFalse(authorities.isEmpty())
    assertEquals(roles.size, authorities.size)
    roles.forEachIndexed { index, role ->
      assertEquals(role, authorities[index].authority)
    }
  }


}
