import javax.swing.*;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class Quicksort {

    static int swaps;
    static final int NUMARRAYS = 9;

    static final int MAXRANDELEMENT = 10;
    static final int MINRANDELEMENT = 1;

    static final int MAXARRAYLEN = 10;
    static final int MINARRAYLEN = 10;

    static final int OUTLIERTOLERANCE = 10;

    public static void main(String[] args) throws IOException {

        System.out.println("Creating " + NUMARRAYS + " arrays");
        System.out.println("With random length: " + MINARRAYLEN + "->" + MAXARRAYLEN);
        System.out.println("Filled with random elements: " + MINRANDELEMENT + "->" + MAXRANDELEMENT + "\n");

        FileWriter writer = new FileWriter("metricTimes.csv");
        writer.append("Sortedness,Time(ms),Length\n");

        for(int i = 0; i < 1; i++) {
            //array list of all created arrays
            ArrayList<int[]> arrays = createArrays();

            System.out.println("reached");
            //2D array where each row is a record with the number of swaps, time taken to sort, and length of the array
            double[][] swapTimes = sortArrays(arrays);

            //remove outliers from the data
            double[][] pruned = pruneOutliers(swapTimes);

            //calculate a sortedness metric from the number of swaps
            double[][] csvData = calculateMetrics(pruned);

            try {
                //write complete CSV data to the file
                writeData(csvData, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        writer.close();
    }


    /**
     * https://stackoverflow.com/questions/2920315/permutation-of-array
     *
     * @param arr
     * @param k
     * @param permutations
     */
    static void permute(List<Integer> arr, int k, ArrayList<String> permutations) {
        for (int i = k; i < arr.size(); i ++) {
            java.util.Collections.swap(arr, i, k);
            permute(arr, k + 1, permutations);
            java.util.Collections.swap(arr, k, i);
        }
        if (k == arr.size() - 1) {
            permutations.add(java.util.Arrays.toString(arr.toArray()));
        }
    }


    static ArrayList<int[]> createArrays() {

        //list to hold all arrays
        ArrayList<int[]> arrays = new ArrayList<>();

        //stoopid
        int[] dummy = new int[0];
        arrays.add(dummy);

        ArrayList<String> permutations = new ArrayList<>();

        Integer[] totalList = new Integer[NUMARRAYS];
        for(int i = 1; i < NUMARRAYS + 1; i++){
            totalList[i-1] = i;
        }

        Integer[] leftList = new Integer[(totalList.length + 1)/2];
        Integer[] rightList = new Integer[totalList.length - leftList.length];

        System.arraycopy(totalList, 0, leftList, 0, leftList.length);
        System.arraycopy(totalList, leftList.length, rightList, 0, rightList.length);


        permute(Arrays.asList(totalList), 0, permutations);
      //  permute(Arrays.asList(totalList), 2, permutations);



        for (String permutation : permutations) {
            String[] strArray = (permutation.substring(1, permutation.length() - 1)).split(", ");
            int[] intArray = new int[strArray.length];

            for (int i = 0; i < strArray.length; i++) {
                intArray[i] = Integer.parseInt(strArray[i]);
            }

            arrays.add(intArray);
        }

//        for(int[] array: arrays)
//        System.out.println(Arrays.toString(array));


        return arrays;
    }

    static double[][] sortArrays(ArrayList<int[]> arrays) {

        //2D array where each row is a record with the number of swaps, time taken to sort, and length of the array
        double[][] swapTimes = new double[arrays.size()][3];


        //fill 2D array with data
        int c = 0;
        for (int[] array : arrays) {

            //reset swap field for every array
            swaps = 0;

            swapTimes[c][0] = scoreArray(array);

            //sort array
            double timeTaken = timeAndSort(array);
            swapTimes[c][1] = timeTaken;
            swapTimes[c][2] = array.length;
            c++;
        }

        return swapTimes;

    }

    //returns number of inversions in array
    static double scoreArray(int[] array) {

        int n = array.length;

        int inversions = 0;
        for (int i = 0; i < n - 1; i++)
            for (int j = i + 1; j < n; j++)
                if (array[i] > array[j])
                    inversions++;

        return inversions;
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

        double validMax = avgTime * OUTLIERTOLERANCE;

        System.out.println("Average sort time from dataset: " + avgTime + "ms");
        System.out.println("Maximum non-outlier time: " + validMax + "ms\n");

        int c = 0;
        int prunes = 0;
        //skip the first dummy result
        for (int i = 1; i < swapTimes.length; i++) {
            if (swapTimes[i][1] < validMax) {
                prunedTimes[c][0] = swapTimes[i][0];
                prunedTimes[c][1] = swapTimes[i][1];
                prunedTimes[c][2] = swapTimes[i][2];
                c++;
            } else {
                if (prunes <= 50) {
                    System.out.println("Removed a row of data with time: " + swapTimes[i][1] + "ms");
                } else if (prunes == 51) {
                    System.out.println("Over 50 records pruned... cancelling system outs to reduce clutter");
                }
                prunes++;
            }
        }

        System.out.println("\nPruned dataset now contains: " + (prunedTimes.length - prunes) + " records");

        return prunedTimes;


    }

    static double[][] calculateMetrics(double[][] pruned) {

        //find maximum number of swaps in the data
        int maxInversions = 0;
        for (double[] row : pruned) {
            if ((int) row[0] > maxInversions)
                maxInversions = (int) row[0];
        }

        //calculating "sortedness" metric by dividing number of swaps by the maximum
        //therefore
        //sorted = 0
        //unsorted = 1
        //0 > slightly sorted < 1
        for (int i = 0; i < pruned.length; i++) {
            double metric = pruned[i][0] / maxInversions;
            pruned[i][0] = metric;
        }

        return pruned;

    }

    static void writeData(double[][] csvData, FileWriter writer) throws IOException {


        System.out.println("\nWriting data...");


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


        System.out.print("... data successfully written");


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

        double[] allTimes = new double[1000];

        for(int i = 0; i < allTimes.length; i++) {

            int[] arrayCopy = new int[array.length];

            System.arraycopy(array, 0, arrayCopy, 0, array.length);


            //get starting system time in nanoseconds
            long startTime = System.nanoTime();

            //sort the array
            quickSort(arrayCopy, 0, arrayCopy.length - 1);

            //get ending system time in nanoseconds
            long endTime = System.nanoTime();


            double diffTime = (endTime - startTime);

            allTimes[i] = diffTime;


        }
        //debug sysout
        //System.out.println(diffTime + " ms");
        //System.out.println(swaps + " swaps");
        //System.out.println("\n");

        //return the difference in milliseconds

        double totalTime = 0.00;
        for(double time: allTimes){
            totalTime += time;
        }

        //returns average time taken to sort
        return (totalTime / allTimes.length);
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

                //if (array[j] != array[i]) {
                int temp = array[j];
                array[j] = array[i];
                array[i] = temp;
                swaps++;
                // }

            }
        }

        //  if (array[i + 1] != array[end]) {
        int temp = array[i + 1];
        array[i + 1] = array[end];
        array[end] = temp;
        swaps++;
        // }
        return (i + 1);
    }

}
