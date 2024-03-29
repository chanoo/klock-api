package app.klock.api.repository

import app.klock.api.domain.entity.UserTraceType
import app.klock.api.functional.userTrace.UserTraceDto
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.time.LocalDateTime

@Repository
class UserTraceNativeSqlRepository(private val databaseClient: DatabaseClient) {

  /**
   * 사용자 담벼락 조회
   * -- 사용자 담벼락은 최신순으로 정렬되어야 한다.
   * -- 페이징 기능을 추가한다.
   * @param userId
   * @param page
   * @param size
   */
  fun getTraces(userId: Long, page: Int, size: Int): Flux<UserTraceDto> {
    val offSet = page * size
    return databaseClient.sql(
      """
        SELECT
            ut.id, ut.write_user_id, u.nickname, u.profile_image, ut.type, ut.contents, ut.contents_image, ut.heart_count, ut.created_at
        FROM 
            klk_user_trace ut
        JOIN 
            klk_user u ON ut.write_user_id = u.id
        WHERE 
            ut.user_id = :userId
        ORDER BY
            ut.created_at DESC
        LIMIT :offSet, :size
        """
    )
      .bind("userId", userId)
      .bind("offSet", offSet)
      .bind("size", size)
      .map { row, _ ->
        UserTraceDto(
          id = row.get("id", Long::class.java) ?: 0L,
          writeUserId = row.get("write_user_id", Long::class.java) ?: 0L,
          writeNickname = row.get("nickname", String::class.java) ?: "",
          writeUserImage = row.get("profile_image", String::class.java),
          type = UserTraceType.valueOf(
            row.get("type", String::class.java) ?: UserTraceType.ACTIVITY.name
          ), // VARCHAR 값을 Enum으로 변환
          contents = row.get("contents", String::class.java),
          contentsImage = row.get("contents_image", String::class.java),
          heartCount = row.get("heart_count", Int::class.java) ?: 0,
          createdAt = row.get("created_at", LocalDateTime::class.java) ?: LocalDateTime.now()
        )
      }
      .all()
  }
}