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
        SELECT
            fr.follow_id, u.nickname, sum(TIMESTAMPDIFF(MINUTE, st.start_time, st.end_time)) AS total_study_time, u.profile_image
        FROM 
            klk_friend_relation fr
        JOIN 
            klk_user u ON fr.follow_id = u.id
        JOIN
			klk_study_session st ON fr.follow_id = st.user_id    
        WHERE 
            fr.user_id = :userId
            AND fr.followed = true
    """)
            .bind("userId", userId)
            .map { row, _ ->
                FriendDetailDto(
                    followId = row.get("follow_id", Long::class.java)!!,
                    nickname = row.get("nickname", String::class.java)!!,
                    totalStudyTime = row.get("total_study_time", Int::class.java)!!,
                    profileImage = row.get("profile_image", String::class.java)!!,
                    // Add more fields as needed
                )
            }
            .all()
            .collectList()
            .map { friendList ->
                friendList.sortedWith(compareByDescending<FriendDetailDto> {it.totalStudyTime }.thenBy { it.nickname })
            }
            .flatMapMany { Flux.fromIterable(it) }
    }

}