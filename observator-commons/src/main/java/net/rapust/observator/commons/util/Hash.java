package net.rapust.observator.commons.util;

import lombok.experimental.UtilityClass;
import net.rapust.observator.commons.logger.MasterLogger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@UtilityClass
public class Hash {

    public String hash(String s, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(s.getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder();
            for (byte b : hash) {
                String value = Integer.toHexString(b);

                switch (value.length()) {
                    case 8:
                        builder.append(value.substring(6));
                        break;
                    case 2:
                        builder.append(value);
                        break;
                    case 1:
                        builder.append("0").append(value);
                        break;
                }
            }

            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            if (algorithm.equals("SHA256")) {
                return hash(s, "SHA-256");
            } else {
                MasterLogger.error(e);
                return "ERROR";
            }
        }
    }

}
