import java.io.*;
import java.util.*;

public class Quicksort {
    
    //length of arrays to be sorted
    static final int ARRAYLEN = 500;

    //number of times to sort each array
    static final int REPETITIONS = 500;

    public static void main(String[] args) {

        System.out.println("Creating " + ARRAYLEN + " arrays");
        System.out.println("With length: " + ARRAYLEN);

        //hashmap of all created arrays and their number of swaps
        HashMap<int[], Integer> arraySwaps = createArrays();

        System.out.println("\nArrays created");
        System.out.println("\nSorting arrays: " + REPETITIONS + " times");

        //2D array where each row is a record with the metric based onn number of swaps and the time taken to sort
        double[][] swapTimes = sortArrays(arraySwaps);

        System.out.println("\nArrays sorted");

        try {
            //write complete CSV data to the file
            writeData(swapTimes);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * creates all arrays being sorted and fills them into a hashmap
     *
     * @return hashmap of all arrays and their number of swaps
     */
    static HashMap<int[], Integer> createArrays() {

        //map to hold all arrays and their number of swaps
        HashMap<int[], Integer> arraySwaps = new HashMap<>();

        //stoopid
        int[] dummy = new int[0];
        arraySwaps.put(dummy, -1);

        //create and fill sorted list with numbers 1 -> ARRAYLEN
        //and reversed sorted list with numbers ARRAYLEN -> 1
        int[] sortedList = new int[ARRAYLEN];
        int[] reverseSortedList = new int[ARRAYLEN];
        for (int i = 1; i < ARRAYLEN + 1; i++) {
            sortedList[i - 1] = i;
            reverseSortedList[i - 1] = (ARRAYLEN + 1) - i;
        }


        for (int i = 0; i < (ARRAYLEN / 2); i++) {

            //copy original lists
            int[] sortedArrayCopy = new int[sortedList.length];
            int[] reverseSortedArrayCopy = new int[reverseSortedList.length];
            System.arraycopy(sortedList, 0, sortedArrayCopy, 0, sortedList.length);
            System.arraycopy(reverseSortedList, 0, reverseSortedArrayCopy, 0, reverseSortedList.length);

            //perform swaps on arrays i times and then place the swapped array and the number of swaps
            //into the hashmap
            doSwaps(sortedArrayCopy, i);
            arraySwaps.put(sortedArrayCopy, i);

            doSwaps(reverseSortedArrayCopy, i);
            arraySwaps.put(reverseSortedArrayCopy, ARRAYLEN - i);
        }


        return arraySwaps;
    }

    /**
     * randomly swaps 2 elements in an array, "swaps" number of times
     *
     * @param array array to swap elements in
     * @param swaps number of swaps to occur
     */
    static void doSwaps(int[] array, int swaps) {
        Random rand = new Random();

        for (int i = 0; i < swaps; i++) {
            boolean validSwap = false;
            while (!validSwap) {
                int index1 = rand.nextInt(array.length);
                int index2 = rand.nextInt(array.length);
                if (array[index1] != array[index2]) {
                    swap(array, index1, index2);
                    validSwap = true;
                }
            }
        }

    }

    /**
     * calls timeAndSort on every array in the map and then fills an entry in the 2D array with the result
     *
     * @param arraySwaps hashmap of arrays and their number of swaps
     * @return 2D array of metrics and times
     */
    static double[][] sortArrays(HashMap<int[], Integer> arraySwaps) {

        //2D array where each row is a record with the number of swaps, time taken to sort, and length of the array
        double[][] swapTimes = new double[arraySwaps.size()][2];


        //fill 2D array with data
        int c = 0;
        for (Map.Entry<int[], Integer> entry : arraySwaps.entrySet()) {

            //calculating "sortedness" metric by dividing number of swaps by the maximum
            //therefore
            //sorted = 0
            //reverse sorted = 1
            //0 > unsorted < 1
            swapTimes[c][0] = (entry.getValue() / (ARRAYLEN * 1.00));

            //sort array and get the average time taken
            double timeTaken = timeAndSort(entry.getKey());

            swapTimes[c][1] = timeTaken;
            c++;
        }


        return swapTimes;

    }

    /**
     * calls the quicksort function on an unsorted array REPETITIONS times and calculates the time taken
     * to sort the array
     *
     * @param array array to be sorted
     * @return the average time taken to sort the array
     */
    static double timeAndSort(int[] array) {

        //array to store every time result from sorting array
        double[] allTimes = new double[REPETITIONS];

        for (int i = 0; i < allTimes.length; i++) {

            //copy the array
            int[] arrayCopy = new int[array.length];
            System.arraycopy(array, 0, arrayCopy, 0, array.length);


            //get starting system time in nanoseconds
            long startTime = System.nanoTime();

            //sort the array
            quickSort(arrayCopy, 0, arrayCopy.length - 1);

            //get ending system time in nanoseconds
            long endTime = System.nanoTime();

            //calculate difference
            double diffTime = (endTime - startTime);
            allTimes[i] = diffTime;
        }

        //get total of all differences
        double totalTime = 0.00;
        for (double time : allTimes) {
            totalTime += time;
        }

        //returns average time taken to sort in milliseconds
        return (totalTime / allTimes.length) / 1e6;
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

        int pivot = array[end];

        int i = (start - 1);

        for (int j = start; j <= end - 1; j++) {
            //if current element is smaller than the pivot
            if (array[j] < pivot) {
                // increment index of smaller element
                i++;

                //swap elements at index's j and i
                swap(array, j, i);
            }
        }

        //swap elements at index's i+1 and end
        swap(array, i +1, end);

        return (i + 1);
    }

    /**
     * writes the metric and time taken to sort for every array to a csv file
     *
     * @param csvData data to be written
     * @throws IOException when creating writer
     */
    static void writeData(double[][] csvData) throws IOException {

        FileWriter writer = new FileWriter("metricTimes.csv");

        System.out.println("\nWriting data...");

        //append column headers to file
        writer.append("Sortedness,Time(ms)\n");

        //append all rows from data to file
        for (double[] row : csvData) {
            //skips dummy element
            if (row[0] >= 0) {
                String strVar = row[0] + "," + row[1] + "\n";
                writer.append(strVar);
            }
        }

        //close writer
        writer.flush();
        writer.close();

        System.out.print("... data successfully written");

    }

    /**
     * swaps 2 elements in an array
     *
     * @param array array to swap elements in
     * @param i1 index of first element being swapped
     * @param i2 index of second element being swapped
     */
    static void swap(int[] array, int i1, int i2) {
        int temp = array[i1];
        array[i1] = array[i2];
        array[i2] = temp;
    }

}
