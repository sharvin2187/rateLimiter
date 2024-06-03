public class RateLimiterTest {
    public static void main(String args[]) throws InterruptedException {
        int tokensPerSecond = 20;
        int iterations = 400;
        int sleepTimeMillis = 31;
        int allowCount = 0;

        IRateLimiter limiter = new TokenBucket(tokensPerSecond);
        for (int i = 0; i < iterations; i++) {
            boolean allowed = limiter.allow();
            System.out.println("result at time: " + System.currentTimeMillis() % 100000 + " : " + allowed);
            if(allowed) {
                allowCount++;
            }
            Thread.sleep(sleepTimeMillis);
        }

        System.out.println("time elapsed (millis): " + sleepTimeMillis * iterations);
        System.out.println("allowed count: " + allowCount);
        System.out.println("expected allowed count: " + (sleepTimeMillis * iterations)/1000 * tokensPerSecond);
    }
}