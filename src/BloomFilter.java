import java.util.BitSet;

/**
 * A BloomFilter implementation.
 */
public class BloomFilter {
    private BitSet data;
    private int m;
    private int k;

    /**
     * Constructor that initializes BloomFilter given expected number of elements and target false-positive rate.
     * @param n expected number of elements to be stored by BloomFilter
     * @param fpr target false positive rate
     */
    public BloomFilter(int n, double fpr) {
        m = (int) Math.ceil(((-n * Math.log(fpr)) / Math.pow(Math.log(2), 2)));
        k = (int) Math.round((((double) m / n) * Math.log(2)));
        k = (k < 1) ? 1 : k;
        data = new BitSet(m);
    }

    /**
     * Getter that returns size of bit array.
     * @return size of bit array
     */
    public int getM() {
        return m;
    }

    /**
     * Getter that returns number of hash functions.
     * @return number of hash functions
     */
    public int getK() {
        return k;
    }
}
