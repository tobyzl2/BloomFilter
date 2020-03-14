package BloomFilter;

import BloomFilterUtilities.HashFunction;
import BloomFilterUtilities.Serializer;

import java.io.Serializable;
import java.util.BitSet;

/**
 * A BloomFilter. implementation.
 */
public class BloomFilter {
    private int m;  // size of BloomFilter
    private int k;  // number of hash functions
    private BitSet bitset;
    private HashFunction[] hashFunctions;

    /**
     * Constructor that initializes BloomFilter given expected number of elements and target false-positive rate.
     * @param n expected number of elements to be stored by BloomFilter.
     * @param fpr target false positive rate
     */
    public BloomFilter(int n, double fpr) throws IllegalArgumentException {
        if (n <= 0) {
            throw new IllegalArgumentException("N must be positive.");
        }

        if (fpr < 0 || fpr > 1) {
            throw new IllegalArgumentException("Fpr must be between 0 and 1.");
        }

        // initialize m and k
        this.m = Math.max(1, (int) Math.ceil(((-n * Math.log(fpr)) / Math.pow(Math.log(2), 2))));
        this.k = Math.max(1, (int) Math.round((((double) this.m / n) * Math.log(2))));

        // initialize bitset and hashfunctions array
        this.bitset = new BitSet(this.m);
        this.hashFunctions = new HashFunction[this.k];

        // generate k hash functions
        for (int seed = 0; seed < k; seed++) {
            this.hashFunctions[seed] = new HashFunction(this.m, seed);
        }
    }

    /**
     * Adds a serializable object to the BloomFilter.
     * @param s serializable object to add
     */
    public void add(Serializable s) {
        // serialize s
        byte[] byteArr = Serializer.serialize(s);

        // set bitset indexes
        for (HashFunction h : this.hashFunctions) {
            int index = h.hash(byteArr);
            this.bitset.set(index);
        }
    }

    /**
     * Checks if serializable object is in BloomFilter.
     * @param s serializable object to check
     * @return true if object is in BloomFilter, false otherwise
     */
    public boolean contains(Serializable s) {
        // serialize s
        byte[] byteArr = Serializer.serialize(s);

        // check bitset indexes
        for (HashFunction h : this.hashFunctions) {
            int index = h.hash(byteArr);
            if (!this.bitset.get(index)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Getter for size of bit array.
     * @return size of bit array
     */
    public int getM() {
        return this.m;
    }

    /**
     * Getter for number of hash functions.
     * @return number of hash functions
     */
    public int getK() {
        return this.k;
    }

    /**
     * Getter for hash functions.
     * @return hash functions
     */
    public HashFunction[] getHashFunctions() {
        return this.hashFunctions;
    }

    /**
     * Getter for bitset.
     * @return bitset
     */
    public BitSet getBitset() {
        return this.bitset;
    }

    /**
     * Returns string representation of BloomFilter.
     * @return string representation of BloomFilter
     */
    @Override
    public String toString() {
        return this.bitset.toString();
    }
}
