package net.rapust.observator.commons.crypt;

import lombok.experimental.UtilityClass;

import javax.crypto.Cipher;
import java.security.Key;

@UtilityClass
public class Crypt {

    public static byte[] encrypt(byte[] plain, Key key, String type) throws Exception {
        Cipher encryptCipher = Cipher.getInstance(type);
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);

        return encryptCipher.doFinal(plain);
    }

    public static byte[] decrypt(byte[] cipher, Key key, String type) throws Exception {
        Cipher decryptCipher = Cipher.getInstance(type);
        decryptCipher.init(Cipher.DECRYPT_MODE, key);

        return decryptCipher.doFinal(cipher);
    }

}
