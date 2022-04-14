import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import collection.LockFreeList;
import thermometer.ThermometerData;

public class Main {

    static final int NUM_OF_THREADS = 4;
    static final int NUM_OF_GIFTS = 500000;
    static final AtomicInteger numOfThankYous = new AtomicInteger(0);
    static int SEED = 0;

    public static void main(String[] args) {
        if(args.length < 2) {
            System.out.println("Proper use of this program requires two arguments:\n" +
             "(1) Which part of the program are you trying to run \"1\" - Minotaur Gifts or \"2\" - Thermometer Probes.\n" +
             "(2) The seed you want to use for random number generation. Each thread will receive it's own seed but this seed is the base.");
            return;
        }
        SEED = Integer.parseInt(args[1]);
        if(Integer.parseInt(args[0]) == 1) {
            runMinotaurGifts();
        } else if(Integer.parseInt(args[0]) == 2) {
            runThermometerProbe();
        } else {
            System.out.println("Proper use of this program requires that the first argument be either 1 or 2:\n" +
             "\"1\" - Minotaur Gifts\n\"2\" - Thermometer Probes.\n");
        }
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

    public static void runThermometerProbe() {
        ExecutorService es = Executors.newFixedThreadPool(8);
        ArrayList<Future<ThermometerData>> listOfFutures = new ArrayList<>();
        for(int i = 0; i < 8; i++) {
            Future<ThermometerData> td = es.submit(new ThermometerCallable(i));
            listOfFutures.add(td);
        }
        ThermometerData total = new ThermometerData();
        int diff = Integer.MIN_VALUE;
        for(Future<ThermometerData> future : listOfFutures) {
            try {
                ThermometerData td = future.get();
                for(int high : td.getHighTemps()) {
                    total.addHighTemp(high);
                }
                for(int low : td.getLowTemps()) {
                    total.addLowTemp(low);
                }
                if(td.getTempDifference() > diff) {
                    total.setTenMinDiff(td.getTimeSpan());
                    diff = td.getTempDifference();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Extreme values across all probes: ");
        System.out.println(total);
        es.shutdownNow();
    }

    public static class ServantRunnable implements Runnable {
        LockFreeList<Integer> list;
        Random random;
        public ServantRunnable(LockFreeList<Integer> list, long seed) {
            this.list = list;
            random = new Random(seed + Main.SEED);
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

    public static class ThermometerCallable implements Callable<ThermometerData> {
        int probe;
        public ThermometerCallable(int probe) {
            this.probe = probe;
        }
        @Override
        public ThermometerData call() throws Exception {
            ThermometerData td = new ThermometerData();
            td.run(new Random(probe + Main.SEED).nextInt(500));
            synchronized(System.out) {
                System.out.printf("\t[Probe #%d]\n", this.probe);
                System.out.println(td);
            }
            return td;
        }

    }

}
