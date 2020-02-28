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
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * @author techx
 */
public class RSAStegaCrypt extends RecursiveTask<byte[]> {

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
    public static String publicKeyDir = "" ;
    public static String privateKeyDir = "" ;
    public static String videoToEncrypt = "";
    public static String videoToDecrypt = "";


    /**
     * @param originalVideo
     * @param encryptedFile
     * @throws Exception
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
     * @param encryptedBytes
     * @param byteLenghtRead
     */
    public RSAStegaCrypt(byte[] encryptedBytes, int byteLenghtRead) {
        this.byteToDecrypt = encryptedBytes;
        this.byteLenghtRead = byteLenghtRead;
    }

    public RSAStegaCrypt() {
    }

    /**
     * @param encryptedBytes
     * @param byteLenghtRead
     * @param bis
     */
    public RSAStegaCrypt(byte[] encryptedBytes, int byteLenghtRead, BufferedInputStream bis) {
        this.encryptedVideoByteInStream = bis;
        this.byteToDecrypt = encryptedBytes;
        this.byteLenghtRead = byteLenghtRead;
    }

    /**
     * @param bis
     * @throws Exception
     */
    public RSAStegaCrypt(byte[] encryptedBytes, BufferedInputStream bis, File privateKeyFile) throws Exception {
        cipherDecrypter = RSAKeyManager.initCipherDecrypter(privateKeyFile.getPath()); //put the private key file in here
        this.encryptedVideoByteInStream = bis;
        this.byteToDecrypt = encryptedBytes;
    }


    /**
     * @param encryptedVideo The Video file that is encrypted and to be
     *                       decrypted
     * @throws Exception The Exception thrown if it occurs
     */
    public void concurrentDecrypt(File encryptedVideo, File privateKeyFile) throws Exception {
        ForkJoinPool forkJoinTask = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        byte[] decrypted = forkJoinTask.invoke(
                new RSAStegaCrypt(new byte[]{0}, new BufferedInputStream(new FileInputStream(encryptedVideo)), privateKeyFile
                ));
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File("Video.mp4")));
//        System.out.println("TOTAL DECRYPTED BYTES : " + decrypted.length);
        outputStream.write(decrypted);
        outputStream.flush();
    }

    /**
     * @param videoBytes
     * @return
     * @throws IllegalBlockSizeException The Exception thrown it it occurs
     */
    public byte[] encryptVideoBytes(byte[] videoBytes) {
        try {
            return cipherEncrypter.doFinal(videoBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decryptVideoBytes(byte[] videoBytes) throws IllegalBlockSizeException, BadPaddingException {
        return cipherDecrypter.doFinal(videoBytes);
    }

    /*
     * @return
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
//                    System.out.println(Arrays.toString(forkFirstBytes));
                    forkFirst = new RSAStegaCrypt(forkFirstBytes, forkByteLength, encryptedVideoByteInStream);
                    decryptedByteFirst = forkFirst.fork().get();
                }

                if ((computeByteLength = encryptedVideoByteInStream.read(computeSecondBytes)) >= -1) {
//                    System.out.println(Arrays.toString(computeSecondBytes));
                    second = new RSAStegaCrypt(computeSecondBytes, computeByteLength, encryptedVideoByteInStream);
                    decryptedByteSecond = second.compute();
                    System.out.println("SECOND IS COMPUTING");
                    forkFirst.join();
                    System.out.println("FIRST IS COMPUTING");

                }

                byte[] totalDecryptedBytes = new byte[decryptedByteFirst.length + decryptedByteSecond.length];
                decryptedByteFirst = decryptedByteFirst;
                for (int i = 0; i < decryptedByteFirst.length; i++) {
                    totalDecryptedBytes[i] = decryptedByteFirst[i];
                }

                int j = decryptedByteFirst.length - 1;
                decryptedByteSecond = decryptedByteSecond;
                for (int i = 0; i < decryptedByteSecond.length; i++) {
                    totalDecryptedBytes[j + i] = decryptedByteSecond[i];
                }
                System.out.println("COMPLETED TOTAL DECRYPTED BYTES : " + totalDecryptedBytes.length);
                return totalDecryptedBytes;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void concurrentEncrypt() {
        try {
            while (!ENCRYPTION_COMPLETED) {
                Future<Byte[]> videoBytesPart = this.completionService.submit(submitTask());
                if (videoBytesPart.isDone()) {
                    System.out.println("Encryption Done!");
                }
//                System.out.println("AGAIN");
            }
            System.out.println("ENCRYPTION IS COMPLETED!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Callable<Byte[]> submitTask() {
        return new Callable<Byte[]>() {
            @Override
            public Byte[] call() throws Exception {
                ArrayList<Byte> arrayList = new ArrayList<Byte>();
                try {
                    int processors = Runtime.getRuntime().availableProcessors();
//                    for (int i = 0; i < processors; i++) {
                    byte[] bytesRead = new byte[2048]; // maximum number of bytes to read
                    byte[] finalBytes = new byte[arrayList.size()];
                    if (videoToEncrypInStream.read(bytesRead) != -1) {
                        System.out.println(Arrays.toString(bytesRead).length());
                        videoToEncrypOutStream.write(encryptVideoBytes(finalBytes));
                        videoToEncrypOutStream.flush();//encrypt the bytes and write to output
                    } else {
                        ENCRYPTION_COMPLETED = true;
//                        cipherEncrypter.doFinal(new byte[]{});
//                        System.out.println(ENCRYPTION_COMPLETED);
//                            System.exit(0);
                        return null;
//                        }
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

    static public void doEncryptRSAWithAES(File inputFile)
            throws java.security.NoSuchAlgorithmException,
            java.security.InvalidAlgorithmParameterException,
            java.security.InvalidKeyException,
            java.security.spec.InvalidKeySpecException,
            NoSuchPaddingException,
            BadPaddingException,
            IllegalBlockSizeException,
            IOException {
//        if ( args.length != 2 ) {
//            System.err.println("enc pvtKeyFile inputFile");
//            System.exit(1);
//        }

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
//                System.err.println("AES Key Length: " + b.length);
            }

            out.write(iv);
//            System.err.println("IV Length: " + iv.length);

            Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
            ci.init(Cipher.ENCRYPT_MODE, skey, ivspec);
            try  {
                FileInputStream in = new FileInputStream(inputFile);
                System.out.println("Processing the encryption of the provided video...");
                processFile(ci, in, out);
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void doDecryptRSAWithAES(File inputFile, String extension)
            throws java.security.NoSuchAlgorithmException,
            java.security.InvalidAlgorithmParameterException,
            java.security.InvalidKeyException,
            java.security.spec.InvalidKeySpecException,
            NoSuchPaddingException,
            BadPaddingException,
            IllegalBlockSizeException,
            IOException {


        try  {
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

            byte[] iv = new byte[ 128 / 8 ];
            in.read(iv);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
            ci.init(Cipher.DECRYPT_MODE, skey, ivspec);

            try (FileOutputStream out = new FileOutputStream(new File("decoded-stegano-video."+extension))) {
                processFile(ci, in, out);
            }catch (Exception e){
                e.printStackTrace();
            }
        }finally {
            
        }
    }


    static private void processFile(Cipher ci, InputStream in, OutputStream out)
            throws IllegalBlockSizeException,
            BadPaddingException,
            IOException {
        byte[] ibuf = new byte[1024];
        int len;

        while ((len = in.read(ibuf)) != -1) {
            byte[] obuf = ci.update(ibuf, 0, len);
//            System.out.println("Encrypting " + Arrays.toString(obuf));
            if (obuf != null) out.write(obuf);
        }
        {
//            System.out.println("Done ciphering...");
        }
        byte[] obuf = ci.doFinal();
        if (obuf != null) out.write(obuf);
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
}
