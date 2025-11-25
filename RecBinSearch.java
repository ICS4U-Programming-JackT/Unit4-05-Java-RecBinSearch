import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

final class RecBinSearch {

    /** Private constructor to prevent instantiation. */
    private RecBinSearch() {
        throw new IllegalStateException("Utility Class");
    }

    /**
     * Recursively searches for the key in the array slice.
     * @param key The value to search for.
     * @param array The array to search within.
     * @param low The starting index of the slice.
     * @param high The ending index of the slice.
     * @return The index of the key in the original array, or -1 if not found.
     */
    public static int search(final int key, final int[] array,
                             final int low, final int high) {
        if (low > high) {
            return -1;
        }

        int mid = low + (high - low) / 2;
        if (array[mid] == key) {
            return mid;
        } else if (array[mid] < key) {
            return search(key, array, mid + 1, high);
        } else {
            return search(key, array, low, mid - 1);
        }
    }

    /**
     * Performs binary search for each key in its respective array.
     * @param keys Array of keys to search.
     * @param arrays Array of arrays to search in.
     * @return An array of results (index or -1) for each search.
     */
    public static int[] searchAll(final int[] keys, final int[][] arrays) {
        int[] results = new int[keys.length];
        // Perform binary search for each key in its corresponding array
        for (int i = 0; i < keys.length; i++) {
            int[] currentArray = arrays[i];
            results[i] = search(keys[i], currentArray, 0,
                        currentArray.length - 1);
        }
        return results;
    }

    /**
     * Writes search results to a file.
     * @param results Array of search results (indices or -1).
     * @param outputFile File name to write to.
     */
    public static void writeToFile(final int[] results,
                                   final String outputFile) {
        // Write to file, or raise error
        try {
            // Create writer, write each line
            FileWriter writer = new FileWriter(outputFile);
            for (int result : results) {
                writer.write(Integer.toString(result) + System.lineSeparator());
            }
            // Close writer and inform user
            writer.close();
            System.out.println("Search results written to " + outputFile);
        } catch (IOException e) {
            // File write error
            System.out.println("Error writing to file: " + outputFile);
        }
    }

    /**
     * Reads a file and converts its contents into keys and arrays.
     * @param inputFile Input file.
     * @return A 2D array: [0] keys, [1]...[N] flattened arrays.
     */
    public static int[][] getLines(final String inputFile) {
        try {
            // Create file, scanner and empty array list
            File file = new File(inputFile);
            Scanner fileScanner = new Scanner(file);

            ArrayList<Integer> keys = new ArrayList<>();
            ArrayList<int[]> arrays = new ArrayList<>();

            // Iterate through every line
            while (fileScanner.hasNextLine()) {
                // Read each line
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    // Separate key from array elements by the comma
                    String[] parts = line.split(",", 2);
                    if (parts.length != 2) {
                        System.out.println("Error: Invalid line format: "
                                           + line);
                        continue;
                    }

                    // Get key
                    int key = Integer.parseInt(parts[0].trim());
                    keys.add(key);

                    // Get array elements
                    String[] numStrings = parts[1].trim().split("\\s+");
                    ArrayList<Integer> currentArray = new ArrayList<>();
                    for (String s : numStrings) {
                        if (!s.isEmpty()) {
                            currentArray.add(Integer.parseInt(s));
                        }
                    }
                    int[] array = currentArray.stream()
                    .mapToInt(i -> i).toArray();
                    arrays.add(array);

                } catch (NumberFormatException e) {
                    // Raise file empty line/ contains string error
                    System.out.println(
                        "Error: File contained a non-integer value"
                        + "or was malformed.");
                    fileScanner.close();
                    return new int[0][0];
                }
            }
            fileScanner.close();

            // Structure the return:
            // First array contains all keys,
            // followed by all arrays to search
            int keysCount = keys.size();
            int[][] compoundArray = new int[keysCount + 1][];

            // keys go into the first array element
            // of the compound array
            compoundArray[0] = keys.stream()
            .mapToInt(i -> i).toArray();

            // The arrays to search go into the subsequent elements
            for (int i = 0; i < keysCount; i++) {
                compoundArray[i + 1] = arrays.get(i);
            }

            return compoundArray;
        } catch (FileNotFoundException error) {
            // Error msg for file not found
            System.out.println("\nError: The file " + inputFile
                               + " was not found."
                               + " Please ensure it exists"
                               + "in the same directory.");
            return new int[0][0];
        }
    }

    public static void main(final String[] args) {
        // File names
        String inputFile = "input.txt";
        String outputFile = "output.txt";

        // Get array, get search results and write to file
        int[][] compoundArray = getLines(inputFile);
        if (compoundArray.length == 0) {
            return;
        }

        // The first element holds all the keys
        int[] keys = compoundArray[0];

        // The subsequent elements are the arrays to search
        int arraysCount = compoundArray.length - 1;
        int[][] arrays = new int[arraysCount][];
        for (int i = 0; i < arraysCount; i++) {
            arrays[i] = compoundArray[i + 1];
        }

        int[] results = searchAll(keys, arrays);
        writeToFile(results, outputFile);
    }
}
