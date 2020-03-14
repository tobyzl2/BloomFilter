import DataLoader.TextLoader;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DataLoaderTests {
    @Test
    void readTextTest() {
        Object[] words = TextLoader.readText("./data/data.txt");

        // check first 3 words
        assertEquals("the", words[0]);
        assertEquals("of", words[1]);
        assertEquals("and", words[2]);

        // check length of words array
        assertEquals(10000, words.length);
    }
}
