import java.io.*;
import java.util.*;

public class Quicksort {

    static int swaps;
    static final int NUMARRAYS = 50000;

    static final int MAXRANDELEMENT = 1000;
    static final int MINRANDELEMENT = 1;

    static final int MAXARRAYLEN= 1000;
    static final int MINARRAYLEN = 1;

    public static void main(String[] args) {

        System.out.println("Creating "+NUMARRAYS+" arrays");
        System.out.println("With random length: "+MINARRAYLEN+"->"+MAXARRAYLEN);
        System.out.println("Filled with random elements: "+MINRANDELEMENT+"->"+MAXRANDELEMENT+"\n");

        Random rand = new Random();
        ArrayList<int[]> arrays = new ArrayList<>();

        //stoopid
        int[] dummy = {0};

        arrays.add(dummy);

        for (int i = 1; i < NUMARRAYS; i++) {
            //swapTimes1
            int[] array = new int[rand.nextInt(MAXARRAYLEN - MINARRAYLEN + MINARRAYLEN) + MINARRAYLEN];

            //swapTimes2
            // int[] array = new int[i];

            //swapTimes3
            //int[] array = new int[100];

            for (int j = 0; j < array.length - 1; j++) {

                //random int from 1 - 100
                array[j] = rand.nextInt(MAXRANDELEMENT - MINRANDELEMENT + MINRANDELEMENT) + MINRANDELEMENT;
            }

            arrays.add(array);
        }

//        int[] a1 = {4,1,3,2};
//        int[] a2 = {1,2,3,4};
//        int[] a3 = {1,1,1,1};
//        int[] a4 = {4,3,2,1};
//        int[] a5 = {1,2,4,3};
//        int[] a6 = {4,1,3,2};
//
//        arrays.add(dummy);
//        arrays.add(a1);
//        arrays.add(a2);
//        arrays.add(a3);
//        arrays.add(a4);
//        arrays.add(a5);
//        arrays.add(a6);


        double[][] swapTimes = new double[arrays.size()][3];

        int c = 0;

        for (int[] array : arrays) {
            swaps = 0;
            double timeTaken = timeAndSort(array);
            swapTimes[c][0] = swaps;
            swapTimes[c][1] = timeTaken;
            swapTimes[c][2] = array.length;
            c++;
        }

        double[][] pruned = pruneOutliers(swapTimes);
        double[][] csvData = calculateMetrics(pruned);

        try {
            writeData(csvData);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    static double[][] pruneOutliers(double[][] swapTimes) {


        double[][] prunedTimes = new double[swapTimes.length][3];

        double sumTimes = 0;
        double avgTime;


        //skip the first dummy result
        for (int i = 1; i < swapTimes.length; i++) {
            sumTimes += swapTimes[i][1];
        }

        avgTime = sumTimes / swapTimes.length;

        double validMax = avgTime * 2   ;

        System.out.println("Average sort time from dataset: "  + avgTime+"ms");
        System.out.println("Maximum non-outlier time: " + validMax +"ms\n");

        int c = 0;
        int prunes = 0;
        //skip the first dummy result
        for (int i = 1; i < swapTimes.length; i++) {
            if (swapTimes[i][1] < validMax) {
                prunedTimes[c][0] = swapTimes[i][0];
                prunedTimes[c][1] = swapTimes[i][1];
                prunedTimes[c][2] = swapTimes[i][2];
                c++;
            } else{
                if(prunes <= 50) {
                    System.out.println("Pruned a row of data with time: " + swapTimes[i][1] + "ms");
                } else if(prunes == 51){
                    System.out.println("Over 50 records pruned... cancelling system outs to reduce clutter");
                }
                prunes++;
            }
        }

        System.out.println("\nPruned dataset now contains: " + (prunedTimes.length - prunes) + " records");

        return prunedTimes;


    }

    static double[][] calculateMetrics(double[][] pruned){

        //find maximum number of swaps in the data
        int maxSwaps = 0;
        for (double[] row : pruned) {
            if( (int) row[0] > maxSwaps)
                maxSwaps = (int) row[0];
        }

        //calculating "sortedness" metric by dividing number of swaps by the maximum
        //therefore
        //sorted = 0
        //unsorted = 1
        //0 > slightly sorted < 1
        for (int i = 0; i < pruned.length; i++) {
            double metric = pruned[i][0] / maxSwaps;
            pruned[i][0] = metric;
        }

            return pruned;

        }

    static void writeData(double[][] csvData) throws IOException {


        FileWriter writer = new FileWriter("metricTimes.csv");

        System.out.println("\nWriting data...");

        writer.append("Sortedness,Time(ms),Length\n");

        for (double[] row : csvData) {

            //skips null elements where outliers have been removed
            if (row[1] != 0) {


                double swap = row[0];
                double time = row[1];
                int length = (int) row[2];

                String strVar = swap + "," + time + "," + length + "\n";
                writer.append(strVar);
            }
        }


        writer.flush();
        writer.close();

        System.out.print( "... data successfully written");


    }

    /**
     * calls the quicksort function on an unsorted array and calculates the time taken
     * to sort the array
     *
     * @param array array to be sorted
     * @return the time taken to sort the array
     */
    static double timeAndSort(int[] array) {

     //debug sysout
        //System.out.println(Arrays.toString(array));

        //get starting system time in nanoseconds
        long startTime = System.nanoTime();

        //sort the array
        quickSort(array, 0, array.length - 1);

        //get ending system time in nanoseconds
        long endTime = System.nanoTime();


     //debug sysout
        //System.out.println(diffTime + " ms");
        //System.out.println(swaps + " swaps");
        //System.out.println("\n");

        //return the difference in milliseconds
        return (endTime - startTime) / 1e6;
    }

    /**
     * quicksort algorithm
     *
     * @param array array to be sorted
     * @param start starting index
     * @param end   ending index
     */
    static void quickSort(int[] array, int start, int end) {

        if (start < end) {

            //partition index
            int pi = partition(array, start, end);

            //left of partition index
            quickSort(array, start, pi - 1);

            //right of partition index
            quickSort(array, pi + 1, end);
        }

    }

    /**
     * places pivot element at correct position and all smaller elements left of pivot
     * and all greater elements right of pivot
     *
     * @param array array or sub array being sorted
     * @param start first index of array or sub array
     * @param end   last index of array or sub array
     * @return partition index
     */
    static int partition(int[] array, int start, int end) {

        // pivot
        int pivot = array[end];


        int i = (start - 1);

        for (int j = start; j <= end - 1; j++) {
            // If current element is smaller than the pivot
            if (array[j] < pivot) {
                i++;    // increment index of smaller element

                if (array[j] != array[i]) {
                    int temp = array[j];
                    array[j] = array[i];
                    array[i] = temp;
                    swaps++;
                }

            }
        }

        if (array[i + 1] != array[end]) {
            int temp = array[i + 1];
            array[i + 1] = array[end];
            array[end] = temp;
            swaps++;
        }
        return (i + 1);
    }

}
