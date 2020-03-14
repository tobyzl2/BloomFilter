import BloomFilterUtilities.HashFunction;
import BloomFilterUtilities.Serializer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BloomFilterUtilitiesTests {
    private int testM = 5;
    private int testSeed = 42;
    @Test
    void hashFunctionDeterministicTest() {
        HashFunction hf = new HashFunction(testM, testSeed);
        int firstHash = hf.hash(new byte[]{0, 1});
        int secondHash = hf.hash(new byte[]{0, 1});

        // check equivalent hashes
        assertEquals(firstHash, secondHash);
    }

    @Test
    void hashFunctionRangeTest() {
        HashFunction hf = new HashFunction(testM, testSeed);
        int hash;
        for (int i = 0; i < 1000; i++) {
            hash = hf.hash(new byte[]{1, 0});

            // check hash is positive and is less than M
            assertTrue(hash >= 0);
            assertTrue(hash < testM);
        }
    }

    @Test
    void hashFunctionIllegalArgumentTest() {
        // check invalid 0 argument for M
        assertThrows(IllegalArgumentException.class, () ->
                new HashFunction(0, testSeed)
        );

        // check invalid negative argument for M
        assertThrows(IllegalArgumentException.class, () ->
                new HashFunction(-42, testSeed)
        );
    }

    @Test
    void serializerCharacterTest() {
        byte[] byteArr = Serializer.serialize('a');
        Object o = Serializer.deserialize(byteArr);

        // check character deserialized to original
        assertEquals('a', o);
    }

    @Test
    void serializerStringTest() {
        byte[] byteArr = Serializer.serialize("test");
        Object o = Serializer.deserialize(byteArr);

        // check String deserialized to original
        assertEquals("test", o);
    }

    @Test
    void serializerIntegerTest() {
        byte[] byteArr = Serializer.serialize(42);
        Object o = Serializer.deserialize(byteArr);

        // check int deserialized to original
        assertEquals(42, o);
    }
}
