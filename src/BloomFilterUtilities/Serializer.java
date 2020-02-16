package BloomFilterUtilities;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A serializer class.
 */
public class Serializer {
    private static Logger logger = Logger.getLogger(Serializer.class.getName());
    /**
     * Converts a serializable object into a byte array.
     * @param s serializable object to be converted to byte array
     * @return byte array representing serializable object
     */
    public static byte[] serialize(Serializable s) {
        byte[] res = new byte[]{};
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // write object to output stream
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(s);
            oos.flush();

            // write byte array to result
            res = baos.toByteArray();
        } catch (Exception e) {
            // log exception and return empty byte array
            logger.log(Level.SEVERE, "Object could not be serialized with exception: " + e);
        }


        return res;
    }

    /**
     * Converts a byte array to an object.
     * @param byteArray byte array to be deserialized
     * @return object obtained from the deserialized byte array
     */
    public static Object deserialize(byte[] byteArray) {
        Object res = new Object();
        ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bais);
            res = in.readObject();
        } catch (Exception e) {
            try {
                logger.log(Level.SEVERE, "Failed to deserialize with exception: " + e);
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                logger.log(Level.SEVERE, "Failed to close ObjectInputStream with exception: " + e2);
            }
        }

        return res;
    }
}
