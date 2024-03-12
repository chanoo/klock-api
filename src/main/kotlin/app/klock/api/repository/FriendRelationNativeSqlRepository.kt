package app.klock.api.repository

import app.klock.api.functional.friendRelation.FriendDetailDto
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class FriendRelationNativeSqlRepository(private val databaseClient: DatabaseClient) {

    /**
     * 친구 목록 조회
     * - 친구 목록은 친구의 총 공부시간 순으로 정렬되어야 한다.
     * - 친구의 총 공부시간이 같은 경우 닉네임 순으로 정렬되어야 한다.s
     * @param userId
     */
    fun findFriendDetails(userId: Long): Flux<FriendDetailDto> {
      return databaseClient.sql("""
    (
        SELECT
            fr.follow_id AS id, 
            u.nickname, 
            COALESCE(SUM(CASE 
                WHEN WEEK(st.start_time, 1) = WEEK(CURRENT_DATE, 1) THEN TIMESTAMPDIFF(SECOND, st.start_time, st.end_time)
                ELSE 0 END), 0) AS total_study_time, 
            u.profile_image
        FROM 
            klk_friend_relation fr
        JOIN 
            klk_user u ON fr.follow_id = u.id
        LEFT OUTER JOIN
            klk_study_session st ON fr.follow_id = st.user_id AND st.start_time >= DATE_SUB(CURRENT_DATE, INTERVAL (DAYOFWEEK(CURRENT_DATE)-2) DAY)
        WHERE 
            fr.user_id = :userId
            AND fr.followed = true
        GROUP BY
            fr.follow_id
    )
    UNION ALL
    (
        SELECT
            u.id, 
            u.nickname, 
            COALESCE(SUM(CASE 
                WHEN WEEK(st.start_time, 1) = WEEK(CURRENT_DATE, 1) THEN TIMESTAMPDIFF(SECOND, st.start_time, st.end_time)
                ELSE 0 END), 0) AS total_study_time, 
            u.profile_image
        FROM 
            klk_user u
        LEFT OUTER JOIN
            klk_study_session st ON u.id = st.user_id AND st.start_time >= DATE_SUB(CURRENT_DATE, INTERVAL (DAYOFWEEK(CURRENT_DATE)-2) DAY)
        WHERE 
            u.id = :userId
        GROUP BY
            u.id
    )
    ORDER BY total_study_time DESC, nickname
  """)
        .bind(0, userId)
        .map { row, _ ->
          FriendDetailDto(
            followId = row.get("id", Long::class.java) ?: 0L,
            nickname = row.get("nickname", String::class.java) ?: "",
            totalStudyTime = row.get("total_study_time", Int::class.java) ?: 0,
            profileImage = row.get("profile_image", String::class.java) ?: ""
          )
        }
        .all()
        .collectList()
        .flatMapMany {
          if (it.isEmpty()) {
            Flux.empty()
          } else {
            Flux.fromIterable(it)
          }
        }
    }
}