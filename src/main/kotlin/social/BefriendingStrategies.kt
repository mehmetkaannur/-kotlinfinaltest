package social

const val friendLimit = 5

fun standardStrategy(target: User, candidate: User): Boolean =
    !target.hasFriend(candidate)

fun unfriendlyStrategy(target: User, candidate: User): Boolean = false

fun limitOfFiveStrategy(target: User, candidate: User): Boolean {
    if (!standardStrategy(target, candidate)) {
        return false
    }
    while (target.currentFriends.size >= friendLimit) {
        target.removeLongestStandingFriend()
    }

    return true
}

fun interestedInDogsStrategy(target: User, candidate: User): Boolean {
    if (!standardStrategy(target, candidate)) {
        return false
    }
    return "dog" in candidate.bio.lowercase().split(' ')
}
