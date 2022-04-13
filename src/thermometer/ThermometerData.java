package thermometer;

import java.text.NumberFormat;
import java.util.Random;

public class ThermometerData {
    private int[] lowestTemps = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
    private int[] highestTemps = {Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
    /**
     * The time for the largest temp difference
     */
    private int[] tenMinuteDifference = {0, 9};
    /**
     * The temperature difference between any ten minute period
     */
    private int[] tempDifferenceTenMinute = {0, 0};

    /**
     * Shift the lowest temps
     * @param index the index where shifting starts (inclusive)
     */
    private void shiftLowestTemps(int index) {
        for(int i = 3; i >= index; i--) {
            lowestTemps[i+1] = lowestTemps[i];
        }
    }
    /**
     * Shift the lowest temps
     * @param index the index where shifting starts (exclusive)
     */
    private void shiftHighestTemps(int index) {
        for(int i = 3; i >= index; i--) {
            highestTemps[i+1] = highestTemps[i];
        }
    }

    private void addHighTemp(int temp) {
        int index = 0;
        while(index < 5 && temp < highestTemps[index])
            index++;
        if(index > 4) return;
        shiftHighestTemps(index);
        highestTemps[index] = temp;
    }

    private void addLowTemp(int temp) {
        int index = 0;
        while(index < 5 && temp > lowestTemps[index])
            index++;
        if(index < 5) {
            shiftLowestTemps(index);
            lowestTemps[index] = temp;
        }
    }

    private int tempDiffTenMinute() {
        return this.tempDifferenceTenMinute[1] - this.tempDifferenceTenMinute[0];
    }

    public void run(long seed) {
        Random random = new Random(seed);
        int[] highTempTenMinute = {0, Integer.MIN_VALUE};
        int[] lowTempTenMinute = {0, Integer.MAX_VALUE};
        for(int i = 0; i < 60; i++) {
            int temp = random.nextInt(171) - 100;
            if(highTempTenMinute[1] < temp) {
                if(i - lowTempTenMinute[0] <= 10) {
                    tenMinuteDifference[0] = tenMinuteDifference[1];
                    tenMinuteDifference[1] = i;
                    tempDifferenceTenMinute[1] = temp; 
                }
                highTempTenMinute[0] = i;
                highTempTenMinute[1] = temp;
            }
            if(lowTempTenMinute[1] > temp) {
                if(i - highTempTenMinute[0] <= 10) {
                    tenMinuteDifference[0] = tenMinuteDifference[1];
                    tenMinuteDifference[1] = i;
                    tempDifferenceTenMinute[0] = temp;
                }
                lowTempTenMinute[0] = i;
                lowTempTenMinute[1] = temp;
            }
            System.out.println(temp);
            addHighTemp(temp);
            addLowTemp(temp);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lowest temps: {\t");
        for(int i = 0; i < 5; i++) {
            sb.append(String.format("%d\t", lowestTemps[i]));
        }
        sb.append("}\n");
        sb.append("Highest temps: {\t");
        for(int i = 0; i < 5; i++) {
            sb.append(String.format("%d\t", highestTemps[i]));
        }
        sb.append("}\n");
        sb.append(String.format("Largest Difference in 10-minute span: [%d, %d]\n", tenMinuteDifference[0], tenMinuteDifference[1]));
        return sb.toString();
    }
}
