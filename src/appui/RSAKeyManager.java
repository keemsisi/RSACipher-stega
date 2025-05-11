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
 * RSAKeyManager handles RSA key pair generation, saving keys in Base64 PEM format,
 * loading keys from files, and initializing cipher instances for encryption and decryption.
 *
 * Author: techx
 */
public class RSAKeyManager {

    private static Cipher cipherEncrypter;
    private static Cipher cipherDecrypter;
    private static SecretKey cipherSecreteKey;
    private static String cipherAlgorithm;
    private String videoPath;
    private HashMap<Integer, Byte[]> frames;
    private static String timeString;

    /**
     * Constructor initializes the time string using the current system time.
     */
    public RSAKeyManager() {
        this.timeString = Calendar.getInstance().getTime().toString();
    }

    /**
     * Loads an RSA private key from a PEM-formatted file.
     *
     * @param keyFile The file path to the private key.
     * @return The loaded PrivateKey instance.
     */
    public static final PrivateKey loadPrivateKeyFromFile(String keyFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Path path = Paths.get(keyFile);
        byte[] bytes = Files.readAllBytes(path);
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(
                Base64.getDecoder().decode(
                        new String(bytes)
                                .replaceAll("-----BEGIN RSA PRIVATE KEY-----\n", "")
                                .replaceAll("\n-----END RSA PRIVATE KEY-----\n", "")));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(ks);
    }

    /**
     * Loads an RSA public key from a PEM-formatted file.
     *
     * @param keyFile The file path to the public key.
     * @return The loaded PublicKey instance.
     */
    public static final PublicKey loadPublicKeyFromFile(String keyFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Path path = Paths.get(keyFile);
        byte[] bytes = Files.readAllBytes(path);
        X509EncodedKeySpec ks = new X509EncodedKeySpec(
                Base64.getDecoder().decode(
                        new String(bytes)
                                .replaceAll("-----BEGIN RSA PUBLIC KEY-----\n", "")
                                .replaceAll("\n-----END RSA PUBLIC KEY-----\n", "")));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(ks);
    }

    /**
     * Generates a new 1024-bit RSA key pair.
     *
     * @return A KeyPair containing the public and private keys.
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024, new SecureRandom());
        return keyPairGen.generateKeyPair();
    }

    /**
     * Extracts the private key from a given key pair.
     *
     * @param keyPair The RSA key pair.
     * @return The PrivateKey.
     */
    public static PrivateKey getPrivateKey(KeyPair keyPair) {
        if (!keyPair.equals(null)) {
            return keyPair.getPrivate();
        } else {
            throw new IllegalArgumentException("Key Pair is not provided");
        }
    }

    /**
     * Extracts the public key from a given key pair.
     *
     * @param keyPair The RSA key pair.
     * @return The PublicKey.
     */
    public static PublicKey getPublicKey(KeyPair keyPair) {
        if (!keyPair.equals(null)) {
            return keyPair.getPublic();
        } else {
            throw new IllegalArgumentException("Key Pair is not provided");
        }
    }

    /**
     * Saves a private key to a file with a name that includes a timestamp.
     * (Currently writes to stream but doesn't write actual key content.)
     *
     * @param fileName Placeholder for filename.
     */
    public static void savePrivateKey(String fileName) throws FileNotFoundException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(
                new File("RSAStagaPrivate".concat(timeString))));
    }

    /**
     * Saves a public key to a file with a name that includes a timestamp.
     * (Currently writes to stream but doesn't write actual key content.)
     */
    public static void savePublicKey() throws FileNotFoundException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(
                new File("RSAStagaPublic".concat(timeString))));
    }

    /**
     * Helper method to generate RSA key pair and save them in Base64-encoded format.
     * It creates 'public.key' and 'private.key' files.
     */
    public static void keyGenHelper() {
        try {
            KeyPair keyPair = RSAKeyManager.generateKeyPair();
            File publicFile = new File("public");
            File privateFile = new File("private");

            // Uncomment if writing raw encoded bytes instead of Base64
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

    // Uncomment to run the keyGenHelper from the main method
//    public static void main(String[] args) throws NoSuchAlgorithmException {
//        keyGenHelper();
//    }

    /**
     * Saves a private key to a file in PEM format with Base64 encoding.
     *
     * @param pvtOutputFileName Base name of the output file.
     * @param pvt The PrivateKey to be saved.
     * @return true if successful.
     */
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

    /**
     * Saves a public key to a file in PEM format with Base64 encoding.
     *
     * @param pubOutputFileName Base name of the output file.
     * @param pvt The PublicKey to be saved.
     * @return true if successful.
     */
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

    /**
     * Initializes an RSA cipher in ENCRYPT_MODE using a public key from the given file.
     *
     * @param privateKeyFilePath Path to the public key file.
     * @return Initialized Cipher for encryption.
     */
    public static Cipher initCipherEncrypter(String privateKeyFilePath) throws Exception {
        cipherEncrypter = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipherEncrypter.init(Cipher.ENCRYPT_MODE, loadPublicKeyFromFile(privateKeyFilePath));
        return cipherEncrypter;
    }

    /**
     * Initializes an RSA cipher in DECRYPT_MODE using a private key from the given file.
     *
     * @param privateKeyFileName Path to the private key file.
     * @return Initialized Cipher for decryption.
     */
    public static Cipher initCipherDecrypter(String privateKeyFileName) throws Exception {
        cipherDecrypter = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipherDecrypter.init(Cipher.DECRYPT_MODE, loadPrivateKeyFromFile(privateKeyFileName));
        return cipherDecrypter;
    }

}
