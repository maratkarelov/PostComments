package test.coreteka.data

import kotlinx.serialization.Serializable

@Serializable
data class Post(val userId: Int, val id: Int, val title: String, val body: String){
    override fun toString(): String {
        return title
    }
}

@Serializable
data class Comment(
    val postId: Int,
    val id: Int,
    val name: String,
    val email: String,
    val body: String
)