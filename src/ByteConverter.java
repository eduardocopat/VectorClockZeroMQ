import java.io.*;

/**
 * Created by AVELL on 17/11/14.
 */
public class ByteConverter {
    public static byte[] toByte(Object object) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(object);
            byte[] bytes = bos.toByteArray();
            out.close();
            bos.close();
            return bytes;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public static Object fromByte(byte[] bytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInput in = new ObjectInputStream(bis);
            Object object = (Object) in.readObject();
            in.close();
            bis.close();
            return object;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
