import org.apache.commons.codec.digest.MurmurHash3;

public class HashFunction {
    private int seed;

    /**
     * Constructor that initializes hash function with a seed.
     * @param seed
     */
    public HashFunction(int seed) {
        this.seed = seed;
    }

    /**
     * Function used to hash data based on MurmurHash3.
     * @param data data to be hashed
     * @return hash value
     */
    public int hash(byte[] data) {
         return MurmurHash3.hash32x86(data, 0, data.length, seed);
    }
}
