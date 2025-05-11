package appui;
/* To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * RSAStegaCrypt handles the encryption and decryption of video files using RSA and AES algorithms. It allows encryption and decryption of video files using public and private keys.
 * The encryption process includes AES encryption with RSA encryption of the AES key, and the decryption process includes the reverse.
 * This class also utilizes parallel processing with ForkJoinPool and ExecutorCompletionService to efficiently handle large video files.
 *
 * @author techx
 */
public class RSAStegaCrypt extends RecursiveTask<byte[]> {

    public static String publicKeyDir = "";
    public static String privateKeyDir = "";
    public static String videoToEncrypt = "";
    public static String videoToDecrypt = "";
    private static Cipher cipherEncrypter;
    private static Cipher cipherDecrypter;
    private static boolean ENCRYPTION_COMPLETED = false;
    private ExecutorCompletionService<Byte[]> completionService;
    private ExecutorService exeService;
    private BufferedInputStream videoToEncrypInStream;
    private BufferedOutputStream videoToEncrypOutStream;
    private byte[] byteToDecrypt;
    private int byteLenghtRead;
    private BufferedInputStream encryptedVideoByteInStream;

    /**
     * Constructor for initializing encryption with video file and output encrypted file.
     *
     * @param originalVideo The original video file to be encrypted
     * @param encryptedFile The encrypted output file
     * @throws Exception If any exception occurs during initialization
     */
    public RSAStegaCrypt(File originalVideo, File encryptedFile) throws Exception {
        cipherEncrypter = RSAKeyManager.initCipherEncrypter(publicKeyDir);
        this.exeService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 6);
        this.completionService = new ExecutorCompletionService<>(this.exeService);
        this.videoToEncrypInStream = new BufferedInputStream(new FileInputStream(originalVideo));
        //write to the file without overwriting the file
        this.videoToEncrypOutStream = new BufferedOutputStream(new FileOutputStream(encryptedFile, true));
    }

    /**
     * Constructor for initializing decryption with a byte array and its length.
     *
     * @param encryptedBytes The encrypted byte array
     * @param byteLenghtRead The length of the encrypted byte array
     */
    public RSAStegaCrypt(byte[] encryptedBytes, int byteLenghtRead) {
        this.byteToDecrypt = encryptedBytes;
        this.byteLenghtRead = byteLenghtRead;
    }

    /**
     * Default constructor.
     */
    public RSAStegaCrypt() {
    }

    /**
     * Constructor for initializing decryption with encrypted bytes, length, and input stream.
     *
     * @param encryptedBytes The encrypted byte array
     * @param byteLenghtRead The length of the encrypted byte array
     * @param bis            The input stream of the encrypted video file
     */
    public RSAStegaCrypt(byte[] encryptedBytes, int byteLenghtRead, BufferedInputStream bis) {
        this.encryptedVideoByteInStream = bis;
        this.byteToDecrypt = encryptedBytes;
        this.byteLenghtRead = byteLenghtRead;
    }

    /**
     * Constructor for initializing decryption with encrypted bytes, input stream, and private key file.
     *
     * @param encryptedBytes The encrypted byte array
     * @param bis            The input stream of the encrypted video file
     * @param privateKeyFile The private key file for decryption
     * @throws Exception If any exception occurs during initialization
     */
    public RSAStegaCrypt(byte[] encryptedBytes, BufferedInputStream bis, File privateKeyFile) throws Exception {
        cipherDecrypter = RSAKeyManager.initCipherDecrypter(privateKeyFile.getPath()); //put the private key file in here
        this.encryptedVideoByteInStream = bis;
        this.byteToDecrypt = encryptedBytes;
    }

    /**
     * Encrypts a video file with RSA and AES algorithms.
     *
     * @param inputFile The video file to be encrypted
     * @throws Exception If any exception occurs during encryption
     */
    static public void doEncryptRSAWithAES(File inputFile)
            throws java.security.NoSuchAlgorithmException,
            java.security.InvalidAlgorithmParameterException,
            java.security.InvalidKeyException,
            java.security.spec.InvalidKeySpecException,
            NoSuchPaddingException,
            BadPaddingException,
            IllegalBlockSizeException,
            IOException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        SecretKey skey = kgen.generateKey();

        byte[] iv = new byte[128 / 8];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        try (FileOutputStream out = new FileOutputStream(inputFile.getName() + ".videnc")) {
            {
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, RSAKeyManager.loadPublicKeyFromFile(publicKeyDir));
                byte[] b = cipher.doFinal(skey.getEncoded());
                out.write(b);
            }

            out.write(iv);

            Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
            ci.init(Cipher.ENCRYPT_MODE, skey, ivspec);
            try {
                FileInputStream in = new FileInputStream(inputFile);
                processFile(ci, in, out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Decrypts a video file encrypted with RSA and AES algorithms.
     *
     * @param inputFile The encrypted video file
     * @param extension The file extension for the decrypted file
     * @throws Exception If any exception occurs during decryption
     */
    static public void doDecryptRSAWithAES(File inputFile, String extension)
            throws java.security.NoSuchAlgorithmException,
            java.security.InvalidAlgorithmParameterException,
            java.security.InvalidKeyException,
            java.security.spec.InvalidKeySpecException,
            NoSuchPaddingException,
            BadPaddingException,
            IllegalBlockSizeException,
            IOException {
        FileInputStream in = new FileInputStream(inputFile);

        SecretKeySpec skey = null;
        {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, RSAKeyManager.loadPrivateKeyFromFile(privateKeyDir));
            byte[] b = new byte[128];
            in.read(b);
            byte[] keyb = cipher.doFinal(b);
            skey = new SecretKeySpec(keyb, "AES");
        }

        byte[] iv = new byte[128 / 8];
        in.read(iv);
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ci.init(Cipher.DECRYPT_MODE, skey, ivspec);

        try (FileOutputStream out = new FileOutputStream(new File("decoded-stegano-video." + extension))) {
            processFile(ci, in, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes a file using the specified cipher to perform encryption or decryption.
     *
     * @param ci  The cipher to be used for processing
     * @param in  The input stream of the file to be processed
     * @param out The output stream to write the processed data
     * @throws IllegalBlockSizeException If there is an illegal block size during processing
     * @throws BadPaddingException       If there is a bad padding during processing
     * @throws IOException               If there is an I/O error during processing
     */
    private static void processFile(Cipher ci, FileInputStream in, FileOutputStream out)
            throws IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] ibuf = new byte[64];
        int bytesRead;
        while ((bytesRead = in.read(ibuf)) != -1) {
            byte[] obuf = ci.update(ibuf, 0, bytesRead);
            if (obuf != null) {
                out.write(obuf);
            }
        }
        byte[] obuf = ci.doFinal();
        if (obuf != null) {
            out.write(obuf);
        }
    }

    /**
     * Decrypts the provided encrypted video file using the private key.
     *
     * @param encryptedVideo The encrypted video file
     * @param privateKeyFile The private key file used for decryption
     * @throws Exception If any exception occurs during decryption
     */
    public void concurrentDecrypt(File encryptedVideo, File privateKeyFile) throws Exception {
        ForkJoinPool forkJoinTask = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        byte[] decrypted = forkJoinTask.invoke(
                new RSAStegaCrypt(new byte[]{0}, new BufferedInputStream(new FileInputStream(encryptedVideo)), privateKeyFile
                ));
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File("Video.mp4")));
        outputStream.write(decrypted);
        outputStream.flush();
    }

    /**
     * Encrypts the video byte array using the RSA cipher.
     *
     * @param videoBytes The video byte array to be encrypted
     * @return The encrypted byte array
     */
    public byte[] encryptVideoBytes(byte[] videoBytes) {
        try {
            return cipherEncrypter.doFinal(videoBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Decrypts the video byte array using the RSA cipher.
     *
     * @param videoBytes The video byte array to be decrypted
     * @return The decrypted byte array
     * @throws IllegalBlockSizeException If there is an illegal block size during decryption
     * @throws BadPaddingException       If there is a bad padding during decryption
     */
    public byte[] decryptVideoBytes(byte[] videoBytes) throws IllegalBlockSizeException, BadPaddingException {
        return cipherDecrypter.doFinal(videoBytes);
    }

    /*
     * The compute method performs parallel decryption of encrypted video bytes.
     * It reads and decrypts the video file in chunks using ForkJoinPool and returns the total decrypted byte array.
     *
     * @return The decrypted byte array
     */
    @Override
    protected byte[] compute() {
        System.out.println("LENGTH OF THE BYTE TO DECRYPT  " + byteToDecrypt.length);
        try {
            if (byteToDecrypt.length >= 256 && byteLenghtRead > -1) {
                return decryptVideoBytes(byteToDecrypt);
            } else {

                byte[] forkFirstBytes = new byte[128];
                byte[] computeSecondBytes = new byte[128];

                byte[] decryptedByteFirst = null, decryptedByteSecond = null;
                int forkByteLength = 0;
                int computeByteLength = 0;

                RSAStegaCrypt forkFirst = new RSAStegaCrypt(), second = new RSAStegaCrypt();

                if ((forkByteLength = encryptedVideoByteInStream.read(forkFirstBytes)) >= -1) {
                    forkFirst = new RSAStegaCrypt(forkFirstBytes, forkByteLength, encryptedVideoByteInStream);
                    decryptedByteFirst = forkFirst.fork().get();
                }

                if ((computeByteLength = encryptedVideoByteInStream.read(computeSecondBytes)) >= -1) {
                    second = new RSAStegaCrypt(computeSecondBytes, computeByteLength, encryptedVideoByteInStream);
                    decryptedByteSecond = second.compute();
                    forkFirst.join();
                }

                byte[] totalDecryptedBytes = new byte[decryptedByteFirst.length + decryptedByteSecond.length];
                decryptedByteFirst = decryptedByteFirst;
                System.arraycopy(decryptedByteFirst, 0, totalDecryptedBytes, 0, decryptedByteFirst.length);

                int j = decryptedByteFirst.length - 1;
                decryptedByteSecond = decryptedByteSecond;
                System.arraycopy(decryptedByteSecond, 0, totalDecryptedBytes, j + 0, decryptedByteSecond.length);
                System.out.println("COMPLETED TOTAL DECRYPTED BYTES : " + totalDecryptedBytes.length);
                return totalDecryptedBytes;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Encrypts the video file concurrently using multiple threads and writes to the encrypted output file.
     */
    private void concurrentEncrypt() {
        try {
            while (!ENCRYPTION_COMPLETED) {
                Future<Byte[]> videoBytesPart = this.completionService.submit(submitTask());
                if (videoBytesPart.isDone()) {
                    System.out.println("Encryption Done!");
                }
            }
            System.out.println("ENCRYPTION IS COMPLETED!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Submits a task to encrypt a part of the video and return the encrypted bytes.
     *
     * @return A callable task that encrypts a part of the video and returns the encrypted bytes.
     */
    private Callable<Byte[]> submitTask() {
        return new Callable<Byte[]>() {
            @Override
            public Byte[] call() throws Exception {
                ArrayList<Byte> arrayList = new ArrayList<Byte>();
                try {
                    byte[] bytesRead = new byte[2048]; // maximum number of bytes to read
                    byte[] finalBytes = new byte[arrayList.size()];
                    if (videoToEncrypInStream.read(bytesRead) != -1) {
                        videoToEncrypOutStream.write(encryptVideoBytes(finalBytes));
                        videoToEncrypOutStream.flush();//encrypt the bytes and write to output
                    } else {
                        ENCRYPTION_COMPLETED = true;
                        return null;
                    }
                } catch (Exception ex) {
                    videoToEncrypOutStream.close();
                    videoToEncrypInStream.close();
                    ex.printStackTrace();
                }
                return null;
            }
        };
    }
}

//    public static void main(String[] args) throws Exception {
//
//
//
//
////
////        Long start = System.currentTimeMillis();
////        doEncryptRSAWithAES(
////                new File(videoToEncrypt)
////        );
////        Long end = System.currentTimeMillis();
////        System.out.println( "End :: " +  ( end -  start )  ) ;
//
//
//     Long start = System.currentTimeMillis();
//        doDecryptRSAWithAES(
//                new File("C:\\Users\\user\\IdeaProjects\\RSA_Steganography\\62a41b1f-f00d-4987-abc3-33982f9e3d93.mp4.videnc" ), "extracted");
//////
//        Long end = System.currentTimeMillis();
//        System.out.println( "End :: " +  ( end -  start )  ) ;
//
//
////
////
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////        File filer = new File(videoToEncrypt);
////        RSAStegaCrypt crypter = new RSAStegaCrypt(filer , new File("new-encryptedvid.enc"));
////        crypter.concurrentEncrypt();
//
//
////        BufferedInputStream bufferedInputStream =  new BufferedInputStream(
////                new FileInputStream("C:\\Users\\NeuralTechX CEO\\Documents\\RSAWithSteganography\\new-encryptedvid.enc")
////        );
//
////        byte[] tets = new byte[200] ;
////        while (bufferedInputStream.read(tets) != -1) {
////            System.out.println(Arrays.toString(tets));
////        }
//    }
