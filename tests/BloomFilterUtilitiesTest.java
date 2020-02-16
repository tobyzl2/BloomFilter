import BloomFilterUtilities.HashFunction;
import BloomFilterUtilities.Serializer;
import org.junit.Test;

import static org.junit.Assert.*;

public class BloomFilterUtilitiesTest {
    @Test
    public void HashFunctionDeterministicTest() {
        HashFunction hf = new HashFunction(1000, 0);
        int firstHash = hf.hash(new byte[]{0, 1});
        int secondHash = hf.hash(new byte[]{0, 1});

        assertEquals(firstHash, secondHash);
    }

    @Test
    public void HashFunctionDifferenceTest() {
        HashFunction hf = new HashFunction(1000, 0);
        int firstHash = hf.hash(new byte[]{1, 0});
        int secondHash = hf.hash(new byte[]{0, 1});

        assertNotEquals(firstHash, secondHash);
    }

    @Test
    public void SerializerCharacterTest() {
        byte[] byteArr = Serializer.serialize('a');
        Object o = Serializer.deserialize(byteArr);

        assertEquals('a', o);
    }

    @Test
    public void SerializerStringTest() {
        byte[] byteArr = Serializer.serialize("test");
        Object o = Serializer.deserialize(byteArr);

        assertEquals("test", o);
    }

    @Test
    public void SerializerIntegerTest() {
        byte[] byteArr = Serializer.serialize(42);
        Object o = Serializer.deserialize(byteArr);

        assertEquals(42, o);
    }
}
