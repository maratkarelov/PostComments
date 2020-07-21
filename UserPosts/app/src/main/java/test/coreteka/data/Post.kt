package test.coreteka.data

import kotlinx.serialization.Serializable

data class PostComments(val post: Post, var comments: List<Comment>?){
    override fun toString(): String {
        return post.title
    }
}

@Serializable
data class Post(val userId: Int, val id: Int, val title: String, val body: String)
@Serializable
data class Comment(
    val postId: Int,
    val id: Int,
    val name: String,
    val email: String,
    val body: String
)