package com.timodenk.poker;

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
}
