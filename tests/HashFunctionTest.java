import org.junit.Test;

import static org.junit.Assert.*;

public class HashFunctionTest {

    @Test
    public void HashFunctionDeterministicTest() {
        HashFunction hf = new HashFunction(0);
        int firstHash = hf.hash(new byte[]{0, 1});
        int secondHash = hf.hash(new byte[]{0, 1});

        assertEquals(firstHash, secondHash);
    }

    @Test
    public void HashFunctionDifferenceTest() {
        HashFunction hf = new HashFunction(0);
        int firstHash = hf.hash(new byte[]{1, 10});
        int secondHash = hf.hash(new byte[]{0, 1});

        assertNotEquals(firstHash, secondHash);
    }
}
