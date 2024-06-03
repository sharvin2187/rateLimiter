import java.util.concurrent.locks.ReentrantLock;

/* Implements token bucket strategy to rate limit a single client
 *
 * We store two variables lastRefreshed (timestamp) and tokensAvailable (integer)
 * When an access request comes in, we first fill the tokensAvailable based on time elapsed and
 * how many tokens should've been filled. Then we check if we have any tokens to satisfy the request
 * and if yes, give them out. Otherwise, we return false.
 *
 * lastRefreshed and tokensAvailable should be updated together.
 * Lock should be taken for check and decrement.
 */
public class TokenBucket implements IRateLimiter {
    final int tokensPerSecond;

    ReentrantLock re = new ReentrantLock();
    long lastRefreshTime;
    double availableTokens;

    TokenBucket(int tokensPerSecond) {
        this.tokensPerSecond = tokensPerSecond;
        lastRefreshTime = System.currentTimeMillis();
        availableTokens = 0;
    }

    @Override
    public boolean allow() {
        fill();

        re.lock();
        boolean res = false;
        if (availableTokens > 1) {
            res = true;
            availableTokens--;
        }
        re.unlock();

        return res;
    }

    private void fill() {
        re.lock();

        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime - lastRefreshTime;
        double tokensToAdd = 1.0 * timeElapsed / 1000 * tokensPerSecond;
        availableTokens = Math.min(tokensPerSecond, availableTokens + tokensToAdd);
        lastRefreshTime = currentTime;

        re.unlock();
    }
}
