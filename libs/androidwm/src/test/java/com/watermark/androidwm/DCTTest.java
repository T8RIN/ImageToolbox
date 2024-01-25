package com.watermark.androidwm;

import com.watermark.androidwm.utils.FastDctFft;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * The unit tests for the FD invisible watermark detection methods.
 *
 * @author huangyz0918 (huangyz0918@gmail.com)
 */
public class DCTTest {

    @Test
    public void testFftDct() {
        double[] test = {255.0, 254.0, 243.0, 253.0, 255.0, 255.0, 246.0, 255.0, 255.0, 255.0};
        double[] temp = {255.0, 254.0, 243.0, 253.0, 255.0, 255.0, 246.0, 255.0, 255.0, 255.0};
        FastDctFft.transform(test);
        FastDctFft.inverseTransform(test);

        assertEquals(temp[0] - test[0], 0, 0);
        assertEquals(temp[1] - test[1], 0, 0);
        assertEquals(temp[2] - test[2], 0, 0);
        assertEquals(temp[3] - test[3], 0, 0);
    }

    @Test
    public void testCombine() {
        double[] a = {1, 2, 3, 4, 5};
        double[] b = {6, 7, 8, 9, 0};
        double[] c = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};

        FastDctFft.transform(a);
        FastDctFft.transform(b);
        FastDctFft.transform(c);

        double[] combine = new double[10];
        System.arraycopy(a, 0, combine, 0, 5);
        System.arraycopy(b, 0, combine, 5, 5);

        assertNotEquals(Arrays.toString(combine), Arrays.toString(c));
    }

    @Test
    public void testDivide() {
        boolean result = false;
        int count = 0;
        int[] test = new int[200];
        for (int i = 0; i < test.length; i++) {
            test[i] = 0;
        }

        int numOfChunks = (int) Math.ceil((double) test.length / 20);
        for (int i = 0; i < numOfChunks; i++) {
            int start = i * 20;
            int length = Math.min(test.length - start, 20);
            int[] temp = new int[length];
            System.arraycopy(test, start, temp, 0, length);

            for (int j = 0; j < temp.length; j++) {
                temp[j] = 1;
                count++;
                test[start + j] = 1;
            }
        }

        for (int i : test
                ) {
            if (i == 0) {
                result = true;
            }
        }

        assertEquals(result, false);
        assertEquals(count, 200);

    }

    @Test
    public void testDct() {
        double[] test64 = {
                231.0, 224.0, 224.0, 217.0, 217.0, 203.0, 189.0, 196.0,
                210.0, 217.0, 203.0, 189.0, 203.0, 224.0, 217.0, 224.0,
                196.0, 217.0, 210.0, 224.0, 203.0, 203.0, 196.0, 189.0,
                210.0, 203.0, 196.0, 203.0, 182.0, 203.0, 182.0, 189.0,
                203.0, 224.0, 203.0, 217.0, 196.0, 175.0, 154.0, 140.0,
                182.0, 189.0, 168.0, 161.0, 154.0, 126.0, 119.0, 112.0,
                175.0, 154.0, 126.0, 105.0, 140.0, 105.0, 119.0, 84.0,
                154.0, 98.0, 105.0, 98.0, 105.0, 63.0, 112.0, 84.0};
//
//        double[] temp = {
//                231.0, 224.0, 224.0, 217.0, 217.0, 203.0, 189.0, 196.0,
//                210.0, 217.0, 203.0, 189.0, 203.0, 224.0, 217.0, 224.0,
//                196.0, 217.0, 210.0, 224.0, 203.0, 203.0, 196.0, 189.0,
//                210.0, 203.0, 196.0, 203.0, 182.0, 203.0, 182.0, 189.0,
//                203.0, 224.0, 203.0, 217.0, 196.0, 175.0, 154.0, 140.0,
//                182.0, 189.0, 168.0, 161.0, 154.0, 126.0, 119.0, 112.0,
//                175.0, 154.0, 126.0, 105.0, 140.0, 105.0, 119.0, 84.0,
//                154.0, 98.0, 105.0, 98.0, 105.0, 63.0, 112.0, 84.0};

        FastDctFft.transform(test64);

        // the operations need to be done.

        for (int j = 0; j < test64.length; j++) {
            test64[j] = test64[j] * 2;
        }

        FastDctFft.inverseTransform(test64);
//
//        for (int i = 0; i < test64.length; i++) {
//            temp[i] = test64[i] - temp[i];
//        }

//        for (double i : test64) {
//            System.out.println(i / 2);
//        }

    }


}
