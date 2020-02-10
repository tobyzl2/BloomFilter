import java.util.BitSet;

/**
 * A BloomFilter implementation.
 */
public class BloomFilter {
    private int m;
    private int k;
    private BitSet bitset;
    private HashFunction[] hashFunctions;

    /**
     * Constructor that initializes BloomFilter given expected number of elements and target false-positive rate.
     * @param n expected number of elements to be stored by BloomFilter
     * @param fpr target false positive rate
     */
    public BloomFilter(int n, double fpr) {
        // initialize m and k
        this.m = (int) Math.ceil(((-n * Math.log(fpr)) / Math.pow(Math.log(2), 2)));
        this.k = (int) Math.round((((double) m / n) * Math.log(2)));
        this.k = (k < 1) ? 1 : k;

        // initialize bitset and hashfunctions array
        this.bitset = new BitSet(m);
        this.hashFunctions = new HashFunction[k];
        for (int seed = 0; seed < k; seed++) {
            hashFunctions[seed] = new HashFunction(m, seed);
        }
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
