package com.fisight.comment

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : CrudRepository<Comment, Int> {
    @Query("SELECT * FROM comment WHERE entity_id = :entityId AND entity_type = :entityType", nativeQuery = true)
    fun findByCommentedEntityIdAndEntityType(entityId: Int, entityType: String): List<Comment>
}
