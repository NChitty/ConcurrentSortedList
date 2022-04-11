import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    static final int NUM_OF_THREADS = 4;

    public static void main(String[] args) {
        OptimisticList<Integer> list = new OptimisticList<>();
        AtomicInteger curr = new AtomicInteger(0);
        ExecutorService es = Executors.newFixedThreadPool(NUM_OF_THREADS);
        long start = System.currentTimeMillis();
        for(int i = 0; i < NUM_OF_THREADS; i++) {
            es.execute(new AddRunnable(curr, list, start));
        }
        try {
            es.shutdown();
            boolean executed = es.awaitTermination(30, TimeUnit.SECONDS);
            System.out.println(executed ? "Finished on time" : "Timeout!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Added 1000 objects in %dms\n", System.currentTimeMillis() - start);
    }

    public static class AddRunnable implements Runnable {
        AtomicInteger value;
        OptimisticList<Integer> list;
        long start;
        public AddRunnable(AtomicInteger value, OptimisticList<Integer> list, long start) {
            this.value = value;
            this.list = list;
            this.start = start;
        }

        @Override
        public void run() {
            int curr = value.getAndIncrement();
            while(curr < 1000) {
                if(!list.add(curr))
                    break;
                curr = value.getAndIncrement();
            }
        }

    }

}
