package social;

import java.util.function.BiFunction;

public final class Matchmaker {
    private final BiFunction<User, User, Boolean> compatibleChecker;

    public Matchmaker(BiFunction<User, User, Boolean> compatibleChecker) {
        this.compatibleChecker = compatibleChecker;
    }

    public void tryMatching(User userOne, User userTwo) {
        //avoiding deadlock
        User firstLocked;
        User secondLocked;

        if (userOne.getUserName().hashCode() > userTwo.getUserName().hashCode()) {
            firstLocked = userOne;
            secondLocked = userTwo;
        } else {
            firstLocked = userTwo;
            secondLocked = userOne;
        }

        try {
            firstLocked.getLock().lock();
            secondLocked.getLock().lock();
            Boolean compatibility = compatibleChecker.apply(userOne, userTwo);
            if (compatibility) {
                userOne.considerFriendRequest(userTwo);
                userTwo.considerFriendRequest(userOne);
            }
        } finally {
            firstLocked.getLock().unlock();
            secondLocked.getLock().unlock();
        }
    }
}
