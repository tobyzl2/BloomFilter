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
        ObjectOutputStream oos = null;
        try {
            // write object to baos
            oos = new ObjectOutputStream(baos);
            oos.writeObject(s);
            oos.flush();

            // write byte array to res
            res = baos.toByteArray();
        } catch (IOException ioe) {
            // handle ObjectOutputStream exceptions
            logger.log(Level.SEVERE, "Object could not be serialized with exception: " + ioe);
        } finally {
            // close ObjectOutputStream
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException ioe) {
                logger.log(Level.SEVERE, "Failed to close ObjectOutputStream with exception: " + ioe);
            }
        }

        return res;
    }

    /**
     * Converts a byte array to an object.
     * @param byteArray byte array to deserialize
     * @return object obtained from the deserialized byte array
     */
    public static Object deserialize(byte[] byteArray) {
        Object res = new Object();
        ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
        ObjectInput oi = null;
        try {
            // read object to res
            oi = new ObjectInputStream(bais);
            res = oi.readObject();
        } catch (IOException ioe) {
            // handle ObjectInputStream instantiation exception
            logger.log(Level.SEVERE, "Failed to create ObjectInputStream with exception: " + ioe);
        } catch (ClassNotFoundException cnfe) {
            // handle unreadable object class
            logger.log(Level.SEVERE, "Failed to find class with exception: " + cnfe);
        } finally {
            // close ObjectInput
            try {
                if (oi != null) {
                    oi.close();
                }
            } catch (IOException ioe) {
                logger.log(Level.SEVERE, "Failed to close ObjectInput with exception: " + ioe);
            }
        }

        return res;
    }
}
