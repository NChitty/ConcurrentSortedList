import java.text.NumberFormat;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import collection.LockFreeList;
import thermometer.ThermometerData;

public class Main {

    static final int NUM_OF_THREADS = 4;
    static final int NUM_OF_GIFTS = 500000;
    static final AtomicInteger numOfThankYous = new AtomicInteger(0);
    
    public static void main(String[] args) {
        ThermometerData td = new ThermometerData();
        td.run(32);
        System.out.println(td);
    }

    public static void runMinotaurGifts() {
        LockFreeList<Integer> list = new LockFreeList<>();
        ExecutorService es = Executors.newFixedThreadPool(NUM_OF_THREADS);
        long start = System.currentTimeMillis();
        System.out.println("Telling servants to write thank yous, check if the gift is in the list, and remove gifts from the stack...");
        for(int i = 0; i < NUM_OF_THREADS; i++) {
            es.submit(new ServantRunnable(list, i));
        }
        boolean term = true;
        try {
            es.shutdown();
            term = es.awaitTermination(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        es.shutdownNow();
        long end = System.currentTimeMillis();
        System.out.printf("List empty? %b\t|\t# of Thank Yous written: %s\n", list.isEmpty(), NumberFormat.getInstance().format(Main.numOfThankYous.get() - NUM_OF_THREADS));
        System.out.printf("Finished in %dms %s\n", end - start, term ? "via completion." : "via timeout.");
    }
    public static class ServantRunnable implements Runnable {
        LockFreeList<Integer> list;
        Random random;
        public ServantRunnable(LockFreeList<Integer> list, long seed) {
            this.list = list;
            random = new Random(seed);
        }

        @Override
        public void run() {
            int cur = Main.numOfThankYous.get();
            while(cur < NUM_OF_GIFTS) {
                int ranNum = random.nextInt(100);
                if(ranNum >= 5) {
                    cur = Main.numOfThankYous.getAndIncrement();
                    list.add(cur);
                    list.remove(cur);
                } else {
                    ranNum = random.nextInt(cur);
                    list.contains(cur);
                }
            }
        }
    }

    public static class ThermometerCallable<ThermometerData> implements Callable<ThermometerData> {

        @Override
        public ThermometerData call() throws Exception {
            return null;
        }

    }

}
