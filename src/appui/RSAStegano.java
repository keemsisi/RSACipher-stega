package appui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import javax.imageio.ImageIO;
import javax.imageio.metadata.IIOMetadata;

/**
 *
 * @author techx
 */
public class RSAStegano {

    private static File imageFile;
    private BufferedImage originalBufferedImage;
    private static WritableRaster stegaWriteableRaster;
    private BufferedImage stegaImage;
    private static final int PIXEL_SIZE = 1; // the size of each pixel
    private static int originalImageHeight = 0;
    private static int getOriginalImageWidth = 0;

    public RSAStegano(File imageFileName) {
        this.imageFile = imageFileName;
    }
    private static final ExecutorService executorService = createThreadPoolsForVideoEmbedding(10);
    HashMap<Integer, ArrayList<StringBuffer[]>> imagePixelResult = new HashMap<>(); //the result of the pixels extracted

    private BufferedImage createBufferedImage() throws IOException {
        return ImageIO.read(imageFile);
    }

    private void processBufferedImage() throws IOException {
        this.originalBufferedImage = createBufferedImage();
//        this.originalBufferedImage.
    }

    private byte[] getARGB() {
        return ((DataBufferByte) originalBufferedImage.getRaster().getDataBuffer()).getData();
    }

    private String convertByteToInteger(byte byteValue) {
        return Integer.toBinaryString(byteValue);
    }

    private int getImageWidth() {
        return originalBufferedImage.getWidth();
    }

    private int getImageHeight() {
        return originalBufferedImage.getHeight();
    }

    private Raster getImageRaster() {
        return this.originalBufferedImage.getRaster();
    }

    private WritableRaster createStegaImage() {
        return originalBufferedImage.getData().createCompatibleWritableRaster();
    }


    private void getAllPixelsFromOriginalImage() throws Exception {
        processBufferedImage();
        int count = 0;
        double[][] pixels = new double[getImageWidth()][3];
        int[] pixel = new int[4];
        stegaWriteableRaster = createStegaImage();
        Raster originalImageRaster = getImageRaster();
        ArrayList<StringBuffer[]> pixelArrayStringBuffer = new ArrayList<>();

        StringBuffer[] tempArS = new StringBuffer[4];
        this.originalImageHeight = getImageHeight();
        this.getOriginalImageWidth = getImageWidth();

        System.out.println("Video height ::: " + imagePixelResult.size());

        for (int y = 0; y < getImageHeight(); y += PIXEL_SIZE) {

            for (int x = 0; x < getImageWidth(); x += PIXEL_SIZE) {

                originalImageRaster.getPixel(x, y, pixel);
                tempArS[0] = getBinaryString(pixel[0]);
                tempArS[1] = getBinaryString(pixel[1]);
                tempArS[2] = getBinaryString(pixel[2]);
                tempArS[3] = getBinaryString(pixel[3]);
                pixelArrayStringBuffer.add(tempArS);

            }
            imagePixelResult.put(y , pixelArrayStringBuffer);
            pixelArrayStringBuffer = new ArrayList<>();
        }
        System.out.println("Please wait while the program extracts the image pixels....");
        changeLSBs(imagePixelResult, getVideoBinaryArray(null));

    }

    /**
     *
     * @param p The pixel integer value to be converted to binary string
     * @return StringBuffer object type is being returned
     */
    public static StringBuffer getBinaryString(int p) {
        StringBuffer stringBuffer = new StringBuffer(Integer.toBinaryString(p));
        StringBuffer newStringBuffer = new StringBuffer();
        int checker = 8 - stringBuffer.length();
        if (checker > 0) {
            char[] chars = new char[checker];
            for (int i = 0; i < chars.length; i++) {
                chars[i] = '0';
                /*character '0' will be initialised to all the char[]*/
            }
            for (char aChar : chars) {
                newStringBuffer.append(aChar);
                /**
                 * append all the chars from the char[]
                 */
            }
            Arrays.stream(stringBuffer.toString().split("")).forEach(s -> {
                newStringBuffer.append(s);
            });
            return newStringBuffer;
        }
        return stringBuffer;
    }
//
//    public static void main(String[] args) {
//
//        try {
//            RSAStegano imageProcessor = new RSAStegano(new File(getImageFile()));
////
//////        arrayList.forEach(System.out::println);
////        System.out.println("Starting to perform LSB manipulation");
//        imageProcessor.getAllPixelsFromOriginalImage();
////            System.out.println("Running");
////            exstractVideos(null);
////        getVideoBinaryArray(null);
////            copySomeBytes(null);
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
////
//    }

    private static ArrayList<Integer[]> getpixelIntergerArr(ArrayList<StringBuffer[]> stringbuffferArray) {
        ArrayList<Integer[]> result = new ArrayList<Integer[]>();

        for (StringBuffer[] stringBuffer : stringbuffferArray) {
            result.add(
                    new Integer[]{
                            Integer.parseInt(stringBuffer[0].toString(), 2),
                            Integer.parseInt(stringBuffer[1].toString(), 2),
                            Integer.parseInt(stringBuffer[2].toString(), 2),
                            Integer.parseInt(stringBuffer[3].toString(), 2)
                    });
        }

        return result;

    }

    private static ArrayList<StringBuffer[]> getpixelBinaryStringArr(ArrayList<StringBuffer[]> stringbuffferArray) {
        ArrayList<StringBuffer[]> result = new ArrayList<>();

        for (StringBuffer[] stringBuffer : stringbuffferArray) {
            result.add(new StringBuffer[]{
                    stringBuffer[0],
                    stringBuffer[1],
                    stringBuffer[2],
                    stringBuffer[3]
            });
        }
        return result;
    }

    private static void appendRest(HashMap<Integer, ArrayList<StringBuffer[]>> buf, LinkedHashMap<Integer, ArrayList<StringBuffer[]>> linked, int y, int x) {

        try {
            System.out.println("HEIGHT SIZE :::: " + buf.size());
            System.out.println("WIDTH SIZE :::: " + buf.get(0).size());
            System.out.println("LINKED-WIDTH SIZE :::: " + linked.get(0).size());


            boolean stopper = false ;



            for (int i = y; i < buf.size(); i++) { // ArrayList<ArrayList<StringBuffer[]>>

                if (!stopper) {
                    for (int i1 = x; i1 < buf.get(0).size(); i1++) { // ArrayBuffer<StringBuffer[]>
                        linked.put(i, buf.get(i));
                    }
                    stopper = true ;
                }else {
                    for (int i1 = 0; i1 < buf.get(0).size(); i1++) { // ArrayBuffer<StringBuffer[]>
                        linked.put(i, buf.get(i));
                    }
                }
            }

            System.out.println("HEIGHT SIZE :::: " + linked.size());
            System.out.println("WIDTH SIZE :::: " + linked.get(0).size());


            createStanoImage(linked);
//            stopAllThreadExecutorservices(executorService);
            System.out.println(buf.size() + "-------------->>>>>>>   " + linked.size());

        }catch (Exception e ){
            e.printStackTrace();
        }

    }

    /**
     *
     * @param binaryStringBuffer The arrays of bufferstrings from the image
     * pixels
     * @param videoIntegers An array of binary string from the video bytes
     * @return
     */
    private static void changeLSBs(HashMap<Integer, ArrayList<StringBuffer[]>> binaryStringBuffer, ArrayList<Integer> videoIntegers) {


        LinkedHashMap<Integer, ArrayList<StringBuffer[]>> stegaImageBufferStringArray = new LinkedHashMap<>();
//        System.out.println("IMAGE BINARIES BEFORE START");
//            int height = originalImageHeight;
//            int width = getOriginalImageWidth;
//            int[] test = new int[4];
//            for (int k = 0; k < binaryStringBuffer.size(); k++) { // ArrayList<ArrayList<StringBuffer[]>>
//                for (int j = 0; j < binaryStringBuffer.get(k).size(); j++) { // ArrayBuffer<StringBuffer[]>
//                    StringBuffer[] sb = binaryStringBuffer.get(k).get(j);
//                    System.out.println(Arrays.toString(sb));
//                    if (k == 2) {
//                        System.out.println("IMAGE BINARIES BEFORE END");
////                        System.exit(0);
//                    }
//                }
//            }

        /**
         * This method will hide all the video bytes which has been converted to
         * binaries into a given image. All the pixels in the image are also in
         * the form of binaries. The Least Significant Bit (LSB) of each
         * binaries will be removed and the the bit in the video binaries will
         * be replaced with the bit removed from the image binary.
         */
        ArrayList<String[]> result = new ArrayList<>();

        ArrayList<String> videoArrayBitStrings = new ArrayList<>();

        String temp = "";

        int nextPos = 0;

        int n = 0;

        int nextP = 0;

        StringBuffer[] strBuf = new StringBuffer[4];

        ArrayList<StringBuffer[]> sba = new ArrayList<>();

        StringBuffer[] sBuf = null;

        ArrayList<StringBuffer[]> imageStringBuffer = getNextImageStringBufferArray(++n, binaryStringBuffer);

        int ORIGINAL_IMAGE_PIXEL_LENGTH = binaryStringBuffer.size();

        ArrayList<StringBuffer[]> temBuffer = new ArrayList<>(); // store temporary string buffer
        System.out.println(binaryStringBuffer.size());
        System.out.println(binaryStringBuffer.get(0).size());

        System.out.println(binaryStringBuffer.size() * (binaryStringBuffer.get(0).size() * 4));
        System.out.println(2 * videoIntegers.size());



        ArrayList<ArrayList<StringBuffer[]>> steganoSringBufbinaryArray = new ArrayList<>(); // the stegano bufferedm string

        if (((binaryStringBuffer.size() * (binaryStringBuffer.get(0).size() * 4 ) )) < (2 * videoIntegers.size())) {
            System.out.println("VIDEO SIZE ::: " + videoIntegers.size() + "IMAGE PIXELS :::: " + (binaryStringBuffer.size() * binaryStringBuffer.get(0).size() * 4));
            System.err.println("ERROR_OCCURRED :::: The video is too large to be hidden with the given picture");
            System.exit(0);
        }

        int vidPos = 0;
        ArrayList<String> vbs = new ArrayList<>();
        // ArrayList <ArrayList<StringBuffer[]>>
        //creating the thread pool for video embedding
//        while (!videoIntegers.isEmpty()) {
            System.out.println(videoIntegers.size() + ":::" + videoIntegers.size()*2);
            try {
                int nextVidInt = videoIntegers.remove(0);
//                int normalSize = videoIntegers.size();
                System.out.println("NEXT []----------> " + getBinaryString(nextVidInt));
                vbs.addAll(Arrays.asList(getBinaryString(nextVidInt).toString().split("")));

                for (int i = 0; i < originalImageHeight; i++) { // ArrayList<ArrayList<StringBuffer[]>>

                    for (int i1 = 0; i1 < getOriginalImageWidth; i1++) { // ArrayBuffer<StringBuffer[]>
//
                        if (videoIntegers.size() == 0 && vbs.isEmpty()) {

                            System.out.println("Video Size :::: " + "::::: " + videoIntegers.size());
                            System.out.println("Congratulations... all the video bytes are now inside the picture");
                            System.out.println("Done creating stegano image");

                            createVideoEnpoint(i, i1);

                            appendRest(binaryStringBuffer, stegaImageBufferStringArray, i, i1);

                            System.exit(0);

                        } else if (vbs.isEmpty()) {

//                            System.out.println(videoIntegers.size());

                            int nextVidInt1 = videoIntegers.remove(0);

                            vbs.addAll(Arrays.asList(getBinaryString(nextVidInt1).toString().split("")));

                            hide(i , i1 , binaryStringBuffer , strBuf ,sba ,vbs , stegaImageBufferStringArray);


                        } else {

//                            System.out.println(videoIntegers.size());

                            System.out.println(i + ":::: " + "::::: " + i1);

                            hide(i , i1 , binaryStringBuffer , strBuf ,sba ,vbs , stegaImageBufferStringArray);

                        }
                    }
                    sba = new ArrayList<>(); // empty the row of data from the sba
                }
            } catch (Exception e) {

                e.printStackTrace();

            }

//        createVideoEnpoint(i, i1);

        appendRest(binaryStringBuffer, stegaImageBufferStringArray, originalImageHeight, getOriginalImageWidth);
        System.out.println(videoIntegers.size());




    }

//    }

    /**
     *
     * @param index The index to get from the ArrayList<StringBuffer[]> object
     * @return
     */
    private static ArrayList<StringBuffer[]> getNextImageStringBufferArray(int index, HashMap<Integer, ArrayList<StringBuffer[]>> stringBuffer) {
        return stringBuffer.get(index);
    }

    /**
     *
     * @param index
     * @param pixelStringBuffers
     * @param stringBuffer
     * @return
     */
    private static ArrayList<StringBuffer[]> setNextStringBufferArray(int index, ArrayList<ArrayList<StringBuffer[]>> pixelStringBuffers, ArrayList<StringBuffer[]> stringBuffer) {
        return pixelStringBuffers.set(index, stringBuffer);
    }

    /**
     *
     * @param index
     * @param stringBuffer
     * @param p
     * @return
     */
    private static boolean setNextP(int index, ArrayList<StringBuffer[]> stringBuffer, StringBuffer[] p) {
        try {
            stringBuffer.set(index, p);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param videoBytes
     * @return
     * @throws IOException The type of exception thrown from the method block
     */
    private static ArrayList<Integer> getVideoBinaryArray(byte[] videoBytes) throws IOException {

        BufferedInputStream bufferedInputStream = getVideoInputStream();

        ArrayList<Integer> arrayList = new ArrayList<>();
        int b;
        byte[] vb = new byte[1];
//        System.out.println("Reading the video bytes...\n please wait...");
        int count = 0;
        while ((b = bufferedInputStream.read()) != -1) {
//            if (arrayList.size() == 800) {
//                break;
//            }
//            for (int i = 0 ; i < b ; i++) {
//            System.out.println(getBinaryString(b));
//            getBinaryString(b);
            arrayList.add(b);
//            }
        }

        bufferedInputStream = null; //free the memory
        System.out.println("Video bytes read successfully");
        System.out.println(arrayList.size());

        return arrayList;
    }


    private static ArrayList<Integer> copySomeBytes(byte[] videoBytes) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(
                new FileInputStream("C:\\Users\\NeuralTechX CEO\\Documents\\RSAWithSteganography\\src\\rsa_steganography\\original.mp4"));
        BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream("copied-original.mp4"));
        ArrayList<Integer> arrayList = new ArrayList<>();
        int b;
        byte[] vb = new byte[1_000_000];
        while ((b = bufferedInputStream.read(vb)) != -1) {
            bo.write(vb , 0 , b);
            break;

        }
        System.out.println("bytes copied");
        return arrayList;
    }



    private static void hide(int i , int i1 ,
                             HashMap<Integer , ArrayList<StringBuffer[]>> binaryStringBuffer ,
                             StringBuffer[] strBuf ,
                             ArrayList<StringBuffer[]> sba ,
                             ArrayList<String> vbs,
                             LinkedHashMap<Integer , ArrayList<StringBuffer[]>> stegaImageBufferStringArray
    ){
        strBuf = binaryStringBuffer.get(i).get(i1);
        strBuf[0].setCharAt(7, vbs.remove(0).charAt(0));
        strBuf[1].setCharAt(7, vbs.remove(0).charAt(0));
        strBuf[2].setCharAt(7, vbs.remove(0).charAt(0));
        strBuf[3].setCharAt(7, vbs.remove(0).charAt(0));
        sba.add(new StringBuffer[]{strBuf[0], strBuf[1], strBuf[2], strBuf[3]});
        stegaImageBufferStringArray.put(i, sba);
    }

    /**
     *
     * @param steganoImageStringBinaries
     * @return
     */
    private static WritableRaster createStanoImage(LinkedHashMap<Integer, ArrayList<StringBuffer[]>> steganoImageStringBinaries) {
//        System.out.println(stegaWriteableRaster.getHeight());
//        System.out.println(Arrays.toString(stegaWriteableRaster.getPixel(0, 0, new int[4])));

        try {
            int height = originalImageHeight;
            int width = getOriginalImageWidth;
            int[] test = new int[4];
            System.out.println("---------------------------------------");
            for (int i = 0; i < originalImageHeight ; i++) { //height
                for (int i1 = 0; i1 < getOriginalImageWidth ; i1++) { //width
                    //convert StringBuffer[] to the integer values
//                    System.out.println(Arrays.toString(steganoImageStringBinaries.get(i).get(i1)));
                    StringBuffer[] sb = steganoImageStringBinaries.get(i).get(i1);
                    stegaWriteableRaster.setPixel(i1, i, getIntegerValue(sb));
                    stegaWriteableRaster.getPixel(i1, i,test);
//                  StringBuffer[] tempArS = new StringBuffer[]{getBinaryString(test[0]), getBinaryString(test[1]), getBinaryString(test[2]) , getBinaryString(test[3]) };

//                    System.out.println(Arrays.toString(test));
//                    System.out.println("---------------------------------------");
//                    if (i == 0 && i1 == 1600){
//                        System.exit(0);
//                    }
                }
            }

            System.out.println(Arrays.toString(stegaWriteableRaster.getPixel(0, 0, new int[4])));
            BufferedImage bufferedImage = new BufferedImage(stegaWriteableRaster.getWidth(), stegaWriteableRaster.getHeight(), BufferedImage.TYPE_INT_ARGB);
            bufferedImage.setData(stegaWriteableRaster);
            ImageIO.write(bufferedImage, getFileExtentsion(getImageFile()), new File("stegano-image."+getFileExtentsion(getImageFile())));

            return stegaWriteableRaster;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ExecutorService createThreadPoolsForVideoEmbedding(int threads) {
        return Executors.newFixedThreadPool(threads);
    }

    /**
     *
     * @param executorService The executor service object parsed and then
     * shutdown
     * @return
     */
    private static boolean stopAllThreadExecutorservices(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
                return true;
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        return false;
    }

    /**
     *
     * @param executorService
     * @return
     */
    private static List<Runnable> shutdownAndReturTheListOfWaitingThreads(ExecutorService executorService) {
        return executorService.shutdownNow();

    }

    /**
     *
     * @param y
     * @param originalImageRaster
     * @param pixelMappingss
     * @return
     */
    private Future<HashMap<Integer, ArrayList<StringBuffer[]>>> readImagePixelsConcurrently(int y, Raster originalImageRaster, HashMap<Integer, ArrayList<StringBuffer[]>> pixelMappingss , StringBuffer[] tempArS) {
        ArrayList<StringBuffer[]> stringBuffers = new ArrayList<>();
        /**
         *
         */
        int[] pixel = new int[4];
//        executorService.execute(() -> {
//            try {
//                for (int x = 0; x < getImageWidth(); x += PIXEL_SIZE) {
//                    originalImageRaster.getPixel(x, y, pixel);
//
//                    tempArS = new StringBuffer[]{getBinaryString(pixel[0]), getBinaryString(pixel[1]), getBinaryString(pixel[2]), getBinaryString(pixel[3])};
////                System.out.println(Arrays.toString(tempArS));
////                System.out.println(pixelMappingss.size() + ":::::"+ getImageHeight());
//                    stringBuffers.add(tempArS);
//                }
//                pixelMappingss.put(y, stringBuffers);
//                if (pixelMappingss.size() == getImageHeight()) {
//                    System.out.println("100% Complete..,Embedding Video in a moment");
//                    changeLSBs(pixelMappingss, getVideoBinaryArray(null));
//                } else if (pixelMappingss.size() == getImageHeight() * 0.1) {
//                    System.out.println("10% Done...");
//                } else if (pixelMappingss.size() == getImageHeight() * 0.2) {
//                    System.out.println("20% Done...");
//                } else if (pixelMappingss.size() == getImageHeight() * 0.3) {
//                    System.out.println("30% Done...");
//                } else if (pixelMappingss.size() == getImageHeight() * 0.4) {
//                    System.out.println("40% Done...");
//                } else if (pixelMappingss.size() == getImageHeight() * 0.5) {
//                    System.out.println("50% Done...");
//                } else if (pixelMappingss.size() == getImageHeight() * 0.6) {
//                    System.out.println("60% Done...");
//                } else if (pixelMappingss.size() == getImageHeight() * 0.7) {
//                    System.out.println("70% Done...");
//                } else if (pixelMappingss.size() == getImageHeight() * 0.8) {
//                    System.out.println("80% Done...");
//                } else if (pixelMappingss.size() == getImageHeight() * 0.9) {
//                    System.out.println("90% Done...");
//                }
////            System.out.println("Current Thread State ::: " + Thread.currentThread().getName());
////            return pixelMappingss ;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
        return null;
    }

    /**
     *
     * @param sb The StringBuffer[] to be converted to double[]
     * @return Returns double[] which contains pixel values (RGBA)
     */
    private static double[] getIntegerValue(StringBuffer[] sb) {
        return new double[]{
                Integer.parseInt(sb[0].toString(), 2),
                Integer.parseInt(sb[1].toString(), 2),
                Integer.parseInt(sb[2].toString(), 2),
                Integer.parseInt(sb[3].toString(), 2)
        };
    }

    /**
     *
     * @param bufferedImage
     * @return
     */
    private static ArrayList<Integer> exstractVideos(BufferedImage bufferedImage) {

        try {

//            WritableRaster wr =  bufferedImage.getRaster();
            WritableRaster wr = ImageIO.read(new File("stegano-image.jpg")).getRaster();
            int height = wr.getHeight();
            int width = wr.getWidth();
            String tempString = "";
            ArrayList<Integer> videoExtractedIntegers = new ArrayList<>();

            int[] stegaImagePixel = new int[4];
            String[] spec = new BufferedReader(new FileReader("spec.code")).readLine().split(",");
            int ch= Integer.parseInt(spec[0]) ;
            int c = Integer.parseInt(spec[1]);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width ; x += PIXEL_SIZE) {
//                    System.out.println(y  + "::::::::::::" + x);
//
                    if ( Integer.parseInt(spec[0]) == y && Integer.parseInt(spec[1] ) == x) {
                        writeExtractedVideo(videoExtractedIntegers);
//                        System.out.println(y  + "::::::::::::" + x);
                        System.out.println("Done" + videoExtractedIntegers.size());
                        System.exit(0);
                    }
                    wr.getPixel(x, y, stegaImagePixel);

                    tempString = tempString.concat(
                            getBinaryString(stegaImagePixel[0]).toString().split("")[7] + ""
                                    + getBinaryString(stegaImagePixel[1]).toString().split("")[7] + ""
                                    + getBinaryString(stegaImagePixel[2]).toString().split("")[7] + ""
                                    + getBinaryString(stegaImagePixel[3]).toString().split("")[7]
                    );

//                    System.out.println("-----------------------------------------");
//                    System.out.println(getBinaryString(stegaImagePixel[0]).toString());
//                    System.out.println(getBinaryString(stegaImagePixel[1]).toString());
//                    System.out.println(getBinaryString(stegaImagePixel[2]).toString());
//                    System.out.println(getBinaryString(stegaImagePixel[3]).toString());
//                    System.out.println(Arrays.toString(stegaImagePixel));
//                    System.out.println("-----------------------------------------");

//                    if (y == Integer.parseInt(spec[0]) && x == Integer.parseInt(spec[1])) {
//                        System.out.println("Video binaries are been extracted completely from the stega image...");
//                        System.exit(0);
//                    }
                    if (tempString.split("").length == 8) {
                        videoExtractedIntegers.add(Integer.parseInt(tempString,2));
//                        System.out.println(tempString);
                        tempString = "";// clear the string
                    }
                }
            }


            return videoExtractedIntegers;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param fileName
     * @return
     */
    private static String getFileExtentsion(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static void writeExtractedVideo(ArrayList<Integer> vidA) {
        System.err.println("WRITING THE VIDEO INSIDE A NEW FILE WITH THE EXTENSION");
        try(BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(new File("out.dec.mp4")))) {
            while (!vidA.isEmpty()) {
//                System.out.println(getBinaryString(vidA.get(0)));
                bo.write((byte)vidA.remove(0).intValue());
            }
            bo.flush();
            /*decode the encrypted video from the extracted video and write to a file*/
            RSAStegaCrypt.doDecryptRSAWithAES(new File("out.dec.mp4" ),"exracted");
            System.out.println("Successful!");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @return
     */
    private static String getImageFile() {
        return "C:\\Users\\user\\Desktop\\cap3.png";
    }

    /**
     *
     * @return
     */
    private static BufferedInputStream getVideoInputStream() throws IOException {

        return new BufferedInputStream(new FileInputStream("src\\original-video.mp4.videnc"));
//        return new BufferedInputStream(new FileInputStream("out.dec.mp4"));

    }

    /**
     *
     * @param i
     * @param i1
     *
     */
    private static void createVideoEnpoint(int i, int i1) {
        try (FileWriter bf = new FileWriter(new File("spec.code"))) {
            bf.write(i + "," + i1);
            bf.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
