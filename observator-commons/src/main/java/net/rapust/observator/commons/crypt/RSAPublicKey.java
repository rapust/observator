package net.rapust.observator.commons.crypt;

import net.rapust.observator.commons.crypt.Crypt;
import net.rapust.observator.commons.logger.MasterLogger;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAPublicKey {

    private final PublicKey key;

    public RSAPublicKey(byte[] bytes) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytes);
        this.key = keyFactory.generatePublic(publicKeySpec);
    }

    public RSAPublicKey() {
        key = null;
    }

    public byte[] encrypt(byte[] s) {
        if (key == null) {
            return s;
        }

        try {
            return Crypt.encrypt(s, key, "RSA");
        } catch (Exception e) {
            MasterLogger.error(e);
            return "ERROR".getBytes(StandardCharsets.UTF_8);
        }
    }

}
