package com.watermark.androidwm;


import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * The unit tests for the LSB invisible watermark detection methods.
 *
 * @author huangyz0918 (huangyz0918@gmail.com)
 */
public class LSBDetectionTest {

    private static int[][] chunkArray(int[] array, int chunkSize) {
        int numOfChunks = (int) Math.ceil((double) array.length / chunkSize);
        int[][] output = new int[numOfChunks][];

        for (int i = 0; i < numOfChunks; ++i) {
            int start = i * chunkSize;
            int length = Math.min(array.length - start, chunkSize);

            int[] temp = new int[length];
            System.arraycopy(array, start, temp, 0, length);
            output[i] = temp;
        }

        return output;
    }

    @Test
    public void arrayToStringTest() {
        int[] inputArray = {
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0
        };
        int minSize = 8;

        int multiple = inputArray.length / minSize;
        int mod = inputArray.length % minSize;

        StringBuilder[] builders = new StringBuilder[multiple + 1];
        builders[builders.length - 1] = new StringBuilder();

        int[] arrayWithout = Arrays.copyOfRange(inputArray, multiple * minSize, inputArray.length);
        int[] expectArray = {7, 8, 9, 0};

        assertEquals(Arrays.toString(expectArray), Arrays.toString(arrayWithout));
        assertEquals(mod, arrayWithout.length);
    }

    @Test
    public void chunkArrayTest() {
        int[] inputArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
        int chunkSize = 3;
        int[][] result = chunkArray(inputArray, chunkSize);
        StringBuilder[] builders = new StringBuilder[result.length];
        StringBuilder resultBuilder = new StringBuilder();

        for (int i = 0; i < result.length - 1; i++) {
            builders[i] = new StringBuilder();
            for (int j = 0; j < chunkSize; j++) {
                builders[i].append(result[i][j]);
            }
        }

        builders[builders.length - 1] = new StringBuilder();
        for (int i : result[result.length - 1]) {
            builders[builders.length - 1].append(i);
        }

        for (StringBuilder builder : builders) {
            resultBuilder.append(builder.toString());
        }

        assertEquals(resultBuilder.toString(), "1234567891011121314");
        assertEquals(Arrays.toString(result[0]), "[1, 2, 3]");
        assertEquals(Arrays.toString(result[3]), "[10, 11, 12]");
        assertEquals(Arrays.toString(result[result.length - 1]), "[13, 14]");
        assertEquals(result.length, 5);

    }

}
