package Core.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author snow create 2021/04/17 13:58
 */
public class MD5 {
    private final static String[] strHex = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
    public static String getFileMd5(MultipartFile file){
        StringBuilder str = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] b = md.digest(file.getBytes());
            for (int j : b) {
                int d = j;
                if (d < 0) {
                    d += 256;
                }
                int d1 = d / 16;
                int d2 = d % 16;
                str.append(strHex[d1]).append(strHex[d2]);
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }
}
