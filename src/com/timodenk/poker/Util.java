package com.timodenk.poker;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class (static.
 * Contains several static methods which are used in poker functions.
 */
class Util {
    /**
     * Returns the binary digit sum of an integer.
     * Example: int x = 5 is equal to 0b000101. The binary digit sum would be 2.
     * @param x The integer to take the binary digit sum from.
     * @return Binary digit sum of the parameter.
     */
    static int binaryDigitSum(int x) {
        int counter = 0,
                max = 64;//(int)(Math.log(x + 1) / Math.log(2));
        for (int i = 0; i < max; i++) {
            if ((x & (1L << i)) != 0) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * Binomial coefficient method.
     * Taken from SO answer http://stackoverflow.com/questions/36925730/java-calculating-binomial-coefficient
     * @return Binomial coefficient n over k.
     */
    static long binomial(int n, int k) {
        if (k > n - k) {
            k = n - k;
        }

        long b = 1;
        for (int i = 1, m = n; i <= k; i++, m--) {
            b = b * m / i;
        }
        return b;
    }

    static Suit[][] permute(Suit[] arr) {
        List<Suit[]> list = permute(arr, 0);
        Suit[][] array = new Suit[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    /**
     * Taken from SO answer http://stackoverflow.com/a/14444037/3607984
     */
    private static List<Suit[]> permute(Suit[] arr, int k) {
        List<Suit[]> result = new ArrayList<>();
        for(int i = k; i < arr.length; i++) {
            swapArrayElements(arr, i, k);
            result.addAll(permute(arr, k + 1));
            swapArrayElements(arr, k, i);
        }
        if (k == arr.length -1) {
            result.add(arr.clone());
        }
        return result;
    }

    static void swapArrayElements(Suit[] suit, int i, int j) {
        Suit tmp = suit[i];
        suit[i] = suit[j];
        suit[j] = tmp;
    }


}
