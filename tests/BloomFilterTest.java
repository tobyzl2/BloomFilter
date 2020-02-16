import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BloomFilterTest {
    @Test
    void constructorGeneralTest() {
        // initialize BloomFilter
        int n = 1000;
        double fpr = 0.01;
        BloomFilter bf = new BloomFilter(n, fpr);

        // check m and k values
        assertEquals(9586, bf.getM());
        assertEquals(7, bf.getK());
    }

    @Test
    void constructorNonZeroKTest() {
        // initialize BloomFilter
        int n = 100;
        double fpr = 0.8;
        BloomFilter bf = new BloomFilter(n, fpr);

        // check m and k values
        assertEquals(47, bf.getM());
        assertEquals(1, bf.getK());
    }
}