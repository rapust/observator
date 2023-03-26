package net.rapust.observator.commons.crypt;

import net.rapust.observator.commons.logger.MasterLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAKeyPair {

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public static RSAKeyPair create(File folder) throws Exception {
        File publicKeyFile = new File(folder, "public");
        File privateKeyFile = new File(folder, "private");

        if (publicKeyFile.exists() && privateKeyFile.exists()) {
            return new RSAKeyPair(publicKeyFile, privateKeyFile);
        }

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();

        PublicKey publicKey = pair.getPublic();
        PrivateKey privateKey = pair.getPrivate();

        if (publicKeyFile.exists()) {
            publicKeyFile.delete();
        }
        publicKeyFile.createNewFile();

        try (FileOutputStream fos = new FileOutputStream(publicKeyFile)) {
            fos.write(publicKey.getEncoded());
        }


        if (privateKeyFile.exists()) {
            privateKeyFile.delete();
        }
        privateKeyFile.createNewFile();

        try (FileOutputStream fos = new FileOutputStream(privateKeyFile)) {
            fos.write(privateKey.getEncoded());
        }

        return new RSAKeyPair(publicKey, privateKey);
    }

    private RSAKeyPair(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public RSAKeyPair() {
        publicKey = null;
        privateKey = null;
    }

    private RSAKeyPair(File publicKeyFile, File privateKeyFile) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        publicKey = keyFactory.generatePublic(publicKeySpec);

        byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        privateKey = keyFactory.generatePrivate(privateKeySpec);
    }

    public byte[] getPublic() {
        return publicKey.getEncoded();
    }

    public byte[] decrypt(byte[] s) {
        if (privateKey == null) {
            return s;
        }

        try {
            return Crypt.decrypt(s, privateKey, "RSA");
        } catch (Exception e) {
            MasterLogger.error(e);
            return "ERROR".getBytes(StandardCharsets.UTF_8);
        }
    }

}
