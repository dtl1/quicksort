import kotlin.Pair;

import java.io.*;
import java.util.*;

//https://technologyrediscovery.net/javaoo2/sorting.html

public class Quicksort {

    static int swaps;

    public static void main(String[] args) {
        Random rand = new Random();

        ArrayList<int[]> arrays = new ArrayList<>();

        //stoopid
        int[] dummy = {0};

        for (int i = 1; i < 1000; i++) {
            //metricTimes1
            // int[] array = new int[rand.nextInt(100 - 1 + 1) + 1];

            //metricTimes2
            int[] array = new int[i];

            //metricTimes3
            //int[] array = new int[100];

            for (int j = 0; j < array.length - 1; j++) {

                //random int from 1 - 100
                array[j] = rand.nextInt(100 - 1 + 1) + 1;
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


        double[][] metricTimes = new double[arrays.size()][2];

        int c = 0;

        for (int[] array : arrays) {
            swaps = 0;
            double timeTaken = timeAndSort(array);
            metricTimes[c][0] = swaps;
            metricTimes[c][1] = timeTaken;
            c++;
        }

        //pruneOutliers(metricTimes);

        try {
            writeTimes(metricTimes);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    static void pruneOutliers(double[][] metricTimes){
        //TODO get pruning
    }


    static void writeTimes(double[][] metricTimes) throws IOException {


        FileWriter writer = new FileWriter("metricTimes3.csv");


        //skip the first dummy result
        for (int i = 1; i < metricTimes.length; i++) {
            int swap = (int) metricTimes[i][0];
            double time = metricTimes[i][1];

            String strVar = swap + "," + time + "\n";
            writer.append(strVar);
        }


        writer.flush();
        writer.close();


    }


    static double timeAndSort(int[] array) {

        System.out.println(Arrays.toString(array));

        //get starting system time in nanoseconds
        long startTime = System.nanoTime();

        quickSort(array, 0, array.length - 1);

        //get ending system time in nanoseconds
        long endTime = System.nanoTime();

        //get the difference in milliseconds
        double diffTime = (endTime - startTime) / 1e6;


        System.out.println(diffTime + " ms");
        System.out.println(swaps + " swaps");
        System.out.println("\n");


        return diffTime;
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
