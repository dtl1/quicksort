public class Quicksort {

    public static void main(String[] args) {
        int[] array = new int[4];
        array[0] = 4;
        array[1] = 1;
        array[2] = 3;
        array[3] = 2;

        quickSort(array,0,array.length - 1);
    }

    /**
     * quicksort algorithm
     * @param array array to be sorted
     * @param start starting index
     * @param end ending index
     */
    static void quickSort(int[] array, int start, int end) {
        if (start < end) {
        /* pi is partitioning index, arr[pi] is now
           at right place */
            int pi = partition(array, start, end);

            quickSort(array, start, pi - 1);  // Before pi
            quickSort(array, pi + 1, end); // After pi
        }
    }


    /* This function takes last element as pivot, places
       the pivot element at its correct position in sorted
        array, and places all smaller (smaller than pivot)
       to left of pivot and all greater elements to right
       of pivot */
    static int partition (int[] array, int start, int end) {
        // pivot (Element to be placed at right position)
        pivot = arr[high];

        i = (low - 1)  // Index of smaller element

        for (j = low; j <= high- 1; j++)
        {
            // If current element is smaller than the pivot
            if (arr[j] < pivot)
            {
                i++;    // increment index of smaller element
                swap arr[i] and arr[j]
            }
        }
        swap arr[i + 1] and arr[high])
        return (i + 1)
    }

}
