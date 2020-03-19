package DataUtilities;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class to load text files.
 */
public class TextLoader {
    private static Logger logger = Logger.getLogger(TextLoader.class.getName());

    /**
     * Reads lines from a text file.
     * @param filepath path of text file
     * @return array containing lines in text file
     */
    public static Object[] readText(String filepath) throws IllegalArgumentException {
        if (filepath == null) {
            throw new IllegalArgumentException("Illegal null argument for filepath.");
        }

        ArrayList<Object> res = new ArrayList<>();
        File file = new File(filepath);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String word;
            // read word line by line
            while ((word = br.readLine()) != null) {
                res.add(word);
            }
        } catch (FileNotFoundException fnfe) {
            // handle file not found in FilerReader instantiation
            logger.log(Level.SEVERE, "Failed to find file with exception: " + fnfe);
        } catch (IOException ioe) {
            // handle IOException in readLine
            logger.log(Level.SEVERE, "Failed to read line with exception: " + ioe);
        }

        return res.toArray();
    }
}
