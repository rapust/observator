package net.rapust.observator.commons.crypt;

import net.rapust.observator.commons.logger.MasterLogger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class AESKey {

    private final SecretKey key;

    public AESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        key = keyGenerator.generateKey();
    }

    public AESKey(byte[] bytes) {
        key = new SecretKeySpec(bytes, "AES");
    }

    public byte[] encrypt(byte[] bytes) {
        try {
            return Crypt.encrypt(bytes, key, "AES");
        } catch (Exception e) {
            MasterLogger.error(e);
            return "ERROR".getBytes(StandardCharsets.UTF_8);
        }
    }

    public byte[] decrypt(byte[] bytes) {
        try {
            return Crypt.decrypt(bytes, key, "AES");
        } catch (Exception e) {
            MasterLogger.error(e);
            return "ERROR".getBytes(StandardCharsets.UTF_8);
        }
    }

    public byte[] getKey() {
        return key.getEncoded();
    }

}
