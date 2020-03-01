import BloomFilter.BloomFilter;
import BloomFilterUtilities.HashFunction;
import BloomFilterUtilities.Serializer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BloomFilterTest {
    private BloomFilter initializeSimpleBf() {
        // initialize BloomFilter
        int n = 100;
        double fpr = 0.01;
        return new BloomFilter(n, fpr);
    }

    @Test
    void simpleConstructorTest() {
        // initialize BloomFilter parameters
        int n = 1000;
        double fpr = 0.01;

        // initialize BloomFilter
        BloomFilter bf = new BloomFilter(n, fpr);

        // check m and k values
        assertEquals(9586, bf.getM());
        assertEquals(7, bf.getK());
    }

    @Test
    void invalidConstructorTest() {
        // initialize BloomFilter parameters
        int n = -1;
        double fpr = 0.01;

        // check invalid argument exception
        assertThrows(IllegalArgumentException.class,
                () -> new BloomFilter(n, fpr)
        );
    }

    @Test
    void invalidFprConstructorTest() {
        // initialize BloomFilter parameters
        int n = 1000;
        double fpr = 1.01;

        // check invalid argument exception
        assertThrows(IllegalArgumentException.class,
                () -> new BloomFilter(n, fpr)
        );
    }

    @Test
    void kConstructorTest() {
        // initialize BloomFilter
        int n = 100;
        double fpr = 0.8;
        BloomFilter bf = new BloomFilter(n, fpr);

        // check m and k values
        assertEquals(47, bf.getM());
        assertEquals(1, bf.getK());
    }

    @Test
    void addElementTest() {
        // initialize bloomfilter and add 12
        BloomFilter bf = initializeSimpleBf();
        bf.add(12);

        // check that indexes of hash are set
        for (HashFunction hf: bf.getHashFunctions()) {
            int hash = hf.hash(Serializer.serialize(12));
            assertTrue(bf.getBitset().get(hash));
        }
    }

    @Test
    void positiveContainsTest() {
        // initialize bloomfilter and add 12
        BloomFilter bf = initializeSimpleBf();
        bf.add(12);

        // check contains 12
        assertTrue(bf.contains(12));
    }

    @Test
    void negativeContainsTest() {
        // initialize bloomfilter
        BloomFilter bf = initializeSimpleBf();

        // check does not contain 12
        assertFalse(bf.contains(12));
    }
}