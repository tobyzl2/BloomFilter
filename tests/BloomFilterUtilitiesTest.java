import BloomFilterUtilities.HashFunction;
import BloomFilterUtilities.Serializer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BloomFilterUtilitiesTest {
    private int testM = 5;
    private int testSeed = 42;
    @Test
    void HashFunctionDeterministicTest() {
        HashFunction hf = new HashFunction(testM, testSeed);
        int firstHash = hf.hash(new byte[]{0, 1});
        int secondHash = hf.hash(new byte[]{0, 1});

        assertEquals(firstHash, secondHash);
    }

    @Test
    void HashFunctionRangeTest() {
        HashFunction hf = new HashFunction(testM, testSeed);
        int hash;
        for (int i = 0; i < 1000; i++) {
            hash = hf.hash(new byte[]{1, 0});

            assertTrue(hash >= 0);
            assertTrue(hash < testM);
        }
    }

    @Test
    void HashFunctionIllegalArgumentTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new HashFunction(0, testSeed)
        );
    }

    @Test
    void SerializerCharacterTest() {
        byte[] byteArr = Serializer.serialize('a');
        Object o = Serializer.deserialize(byteArr);

        assertEquals('a', o);
    }

    @Test
    void SerializerStringTest() {
        byte[] byteArr = Serializer.serialize("test");
        Object o = Serializer.deserialize(byteArr);

        assertEquals("test", o);
    }

    @Test
    void SerializerIntegerTest() {
        byte[] byteArr = Serializer.serialize(42);
        Object o = Serializer.deserialize(byteArr);

        assertEquals(42, o);
    }
}
