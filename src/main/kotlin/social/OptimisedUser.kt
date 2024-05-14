package social

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class OptimisedUser(
    override val userName: String,
    override val yearOfBirth: Int,
    override val bio: String,
    private val befriendingStrategy: strategy = ::standardStrategy,
) : User {
    private val optimisedUserFriends: OrderedMap<String, User> = HashMapLinked()

    override val currentFriends: List<User>
        get() = optimisedUserFriends.values.map { it }

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
        optimisedUserFriends.remove(user.userName)
        return true
    }

    override fun considerFriendRequest(candidateFriend: User): Boolean {
        if (!befriendingStrategy.invoke(this, candidateFriend)) {
            return false
        }
        optimisedUserFriends[candidateFriend.userName] = candidateFriend
        return true
    }

    override fun removeLongestStandingFriend(): User? {
        if (currentFriends.isEmpty()) {
            return null
        }
        return optimisedUserFriends.removeLongestStandingEntry()?.second
    }
}
