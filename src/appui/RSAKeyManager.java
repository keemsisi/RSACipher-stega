package appui;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;

/**
 * @author techx
 */
public class RSAKeyManager {

    private static Cipher cipherEncrypter;
    private static Cipher cipherDecrypter;
    private static SecretKey cipherSecreteKey;
    private static String cipherAlgorithm;
    private String videoPath;
    private HashMap<Integer, Byte[]> frames;
    private static String timeString;

    public RSAKeyManager() {
        this.timeString = Calendar.getInstance().getTime().toString();

    }

    public static final PrivateKey loadPrivateKeyFromFile(String keyFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Path path = Paths.get(keyFile);
        byte[] bytes = Files.readAllBytes(path);
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(
                Base64.getDecoder().decode(
                        new String(bytes)
                                .replaceAll("-----BEGIN RSA PRIVATE KEY-----\n", "")
                                .replaceAll("\n-----END RSA PRIVATE KEY-----\n", "")));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey pvt = kf.generatePrivate(ks);
        return pvt;
    }

    public static final PublicKey loadPublicKeyFromFile(String keyFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Path path = Paths.get(keyFile);
        byte[] bytes = Files.readAllBytes(path);
        X509EncodedKeySpec ks = new X509EncodedKeySpec(
                Base64.getDecoder().decode(
                        new String(bytes)
                                .replaceAll("-----BEGIN RSA PUBLIC KEY-----\n", "")
                                .replaceAll("\n-----END RSA PUBLIC KEY-----\n", "")));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pvt = kf.generatePublic(ks);
        return pvt;
    }

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024, new SecureRandom());
        return keyPairGen.generateKeyPair();
    }

    public static PrivateKey getPrivateKey(KeyPair keyPair) {
        if (!keyPair.equals(null)) {
            return keyPair.getPrivate();
        } else {
            throw new IllegalArgumentException("Key Pair is not provided");
        }
    }

    public static PublicKey getPublicKey(KeyPair keyPair) {
        if (!keyPair.equals(null)) {
            return keyPair.getPublic();
        } else {
            throw new IllegalArgumentException("Key Pair is not provided");
        }
    }

    public static void savePrivateKey(String fileName) throws FileNotFoundException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(
                new File("RSAStagaPrivate".concat(timeString))));
    }

    public static void savePublicKey() throws FileNotFoundException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(
                new File("RSAStagaPublic".concat(timeString))));
    }

    public static void keyGenHelper() {

        try {

            KeyPair keyPair = RSAKeyManager.generateKeyPair();
            File publicFile = new File("public");
            File privateFile = new File("private");
//            BufferedOutputStream f = new BufferedOutputStream(new FileOutputStream(publicFile));
//            f.write(keyPair.getPublic().getEncoded());
//            f.flush();
//
//            BufferedOutputStream p = new BufferedOutputStream(new FileOutputStream(privateFile));
//            p.write(keyPair.getPrivate().getEncoded());
//            p.flush();
            savePublivckeyAsBase64Format(publicFile.getName(), keyPair.getPublic());
            savePrivateKeyAsBase64Format(privateFile.getName(), keyPair.getPrivate());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    public static void main(String[] args) throws NoSuchAlgorithmException {
//        keyGenHelper();
//    }

    public static boolean savePrivateKeyAsBase64Format(String pvtOutputFileName, PrivateKey pvt) throws IOException {
        Base64.Encoder encoder = Base64.getEncoder();
        String outFile = pvtOutputFileName;
        Writer out = new FileWriter(outFile + ".key");
        out.write("-----BEGIN RSA PRIVATE KEY-----\n");
        out.write(encoder.encodeToString(pvt.getEncoded()));
        out.write("\n-----END RSA PRIVATE KEY-----\n");
        out.close();
        return true;
    }

    public static boolean savePublivckeyAsBase64Format(String pubOutputFileName, PublicKey pvt) throws IOException {
        Base64.Encoder encoder = Base64.getEncoder();
        String outFile = pubOutputFileName;
        Writer out = new FileWriter(outFile + ".key");
        out.write("-----BEGIN RSA PUBLIC KEY-----\n");
        out.write(encoder.encodeToString(pvt.getEncoded()));
        out.write("\n-----END RSA PUBLIC KEY-----\n");
        out.close();
        return true;
    }

    public static Cipher initCipherEncrypter(String privateKeyFilePath) throws Exception {
        cipherEncrypter = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipherEncrypter.init(Cipher.ENCRYPT_MODE, loadPublicKeyFromFile(privateKeyFilePath));
        return cipherEncrypter;
    }

    public static Cipher initCipherDecrypter(String privateKeyFileName) throws Exception {
        cipherDecrypter = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipherDecrypter.init(Cipher.DECRYPT_MODE, loadPrivateKeyFromFile(privateKeyFileName));
        return cipherDecrypter;
    }

}
