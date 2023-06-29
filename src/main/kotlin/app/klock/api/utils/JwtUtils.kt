package app.klock.api.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.security.KeyPair
import java.util.*

// JWT를 생성하고 검증하는 유틸리티 클래스
@Component
class JwtUtils {

  // JWT 만료 기간
  @Value("\${jwt.expiration}")
  private val jwtExpiration: Long = 90L * 24 * 60 * 60 * 1000 // 90 days

  // Refresh Token 만료 기간
  @Value("\${jwt.refreshExpiration}")
  private val jwtRefreshExpiration: Long = 180L * 24 * 60 * 60 * 1000 // 90 days

  // KeyPairUtils에서 키 쌍 로드
  private val keyPair: KeyPair = KeyPairUtils.loadKeyPair()

  // JWT 토큰 생성 메소드
  fun generateToken(userId: String, roles: List<String>?): String {

    val claims = Jwts.claims().setSubject(userId)
    claims["userId"] = userId
    claims["roles"] = roles

    val now = Date()
    val expiryDate = Date(now.time + jwtExpiration)

    // JWT를 구성하고 서명하여 문자열로 반환
    return Jwts.builder()
      .setClaims(claims)
      .setIssuedAt(now)
      .setExpiration(expiryDate)
      .signWith(keyPair.private, SignatureAlgorithm.RS256)
      .compact()
  }

  // JWT 토큰에서 사용자 이름 가져오기
  fun getUserIdFromToken(token: String): String {
    val claims: Claims = getClaimsFromToken(token)
    return claims.subject
  }

  fun getUserIdFromToken(): Long {
    val authentication: Authentication = SecurityContextHolder.getContext().authentication
    val token = authentication.credentials.toString()
    return getUserIdFromToken(token).toLong()
  }

  // JWT 토큰의 유효성 검사
  fun validateToken(token: String): Boolean {
    val claims: Claims = getClaimsFromToken(token)
    return !claims.expiration.before(Date())
  }

  // JWT 토큰의 유효성 검사하고 이메일을 반환
  fun validateTokenAndGetUserId(token: String): String {
    val claims: Claims = getClaimsFromToken(token)
    if (!claims.expiration.before(Date())) {
      return claims.subject
    } else {
      throw IllegalArgumentException("Invalid token")
    }
  }

  // JWT 토큰에서 권한 목록 가져오기
  fun getAuthoritiesFromJwt(token: String): List<GrantedAuthority> {
    val claims: Claims = getClaimsFromToken(token)
    val roles = claims["roles"] as List<String>
    return roles.map { SimpleGrantedAuthority(it) }
  }

  // JWT 토큰에서 Claims 객체 가져오기 (내부 사용)
  private fun getClaimsFromToken(token: String): Claims {
    return Jwts.parserBuilder()
      .setSigningKey(keyPair.public)
      .build()
      .parseClaimsJws(token)
      .body
  }

  // Refresh 토큰 생성 메소드
  fun generateRefreshToken(userId: String, roles: List<String>?): String {
    val now = Date()
    val expiryDate = Date(now.time + jwtRefreshExpiration)

    return Jwts.builder()
      .setSubject(userId)
      .setIssuedAt(now)
      .setExpiration(expiryDate)
      .claim("roles", roles) // 역할 목록을 Refresh 토큰에 저장
      .setId(UUID.randomUUID().toString()) // Refresh token에 고유한 ID 추가
      .signWith(keyPair.private)
      .compact()
  }

  // Refresh 토큰의 유효성 검사
  fun validateRefreshToken(token: String): Boolean {
    val claims: Claims = getClaimsFromToken(token)
    return !claims.expiration.before(Date())
  }

}
