package Poker;

/**
 * Created by Denk on 21/02/17.
 */
class Util {
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

    // http://stackoverflow.com/questions/36925730/java-calculating-binomial-coefficient
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
