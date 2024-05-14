package social

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

typealias strategy = (User, User) -> Boolean

const val MIN_DOB = 1900
const val MAX_DOB = 2100

class SimpleUser(
    override val userName: String,
    override val yearOfBirth: Int,
    override val bio: String,
    private val befriendingStrategy: strategy = ::standardStrategy,
) : User {
    private val simpleUserFriends: MutableList<User> = mutableListOf()

    override val currentFriends: List<User>
        get() = simpleUserFriends.map { it }

    override val lock: Lock = ReentrantLock()

    init {
        if (yearOfBirth !in (MIN_DOB..MAX_DOB)) {
            throw IllegalArgumentException()
        }
    }

    override fun hasFriend(user: User): Boolean {
        return user.userName in currentFriends.map { it.userName }
    }

    override fun removeFriend(user: User): Boolean {
        if (!hasFriend(user)) {
            return false
        }
        simpleUserFriends.remove(user)
        return true
    }

    override fun considerFriendRequest(candidateFriend: User): Boolean {
        if (!befriendingStrategy.invoke(this, candidateFriend)) {
            return false
        }
        simpleUserFriends.add(candidateFriend)
        return true
    }

    override fun removeLongestStandingFriend(): User? {
        if (currentFriends.isEmpty()) {
            return null
        }
        return simpleUserFriends.removeAt(0)
    }
}
