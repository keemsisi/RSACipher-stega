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
import javax.swing.JTextArea;

/**
 *
 * @author techx
 */
public class RSA_STEGA_IMPL {

    public static File imageFile;
    private BufferedImage originalBufferedImage;
    private static WritableRaster stegaWriteableRaster;
    private BufferedImage stegaImage;
    private static final int PIXEL_SIZE = 1; // the size of each pixel
    private static int originalImageHeight = 0;
    private static int getOriginalImageWidth = 0;
    public static String extractedVideo = "extracted-video-from-stega-image";
    public static String stegaImageFileOut = "stega-image";
    public static String stegaImageFileInput = "stega-image_mp4_704560_.PNG";
//    public static String imageFile = "";
    public JTextArea jTextArea;

    public static String encryptedVideoInputFile = "C:\\Users\\user\\IdeaProjects\\RSA_Steganography\\3bb615ff-a4fa-470b-b7c0-faa502217a35.mp4.videnc";

    public RSA_STEGA_IMPL(File imageFileName, JTextArea jTextArea) {
        this.imageFile = imageFileName;
        this.jTextArea = jTextArea;
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

    private static boolean done = false;

    //    private HashMap<Integer , ArrayList<StringBuffer[]>> getAllPixelsFromOriginalImage() throws Exception {
    public void getAllPixelsFromOriginalImage() throws Exception {
        jTextArea.append("\n>>>>Processing the video steganography...\n>>>>Please wait while the program hides the video insde the image\nThis will take a while");
        Thread processor = new Thread(() -> {
            try {
                processBufferedImage();
                ArrayList<Integer> videoBinaryArray = getVideoBinaryArray(null);

//        System.out.println(videoBinaryArray.size() );
                createVideoEnpoint(videoBinaryArray.size());

                if (videoBinaryArray.size() * 2 > (getImageWidth() * getImageHeight())) {
//            System.err.println("VIDEO IS TOO LARGE TO FIT INTO THE IMAGE...");
                    jTextArea.append(">>>>\nVIDEO IS TOO LARGE TO FIT INTO THE IMAGE...");
                    jTextArea.append(">>>>\nVideo can not be hidden.... please try another image and video combination");
//                    processor.interrupt();
                    return;
//            System.exit(0);
                }

                double[][] pixels = new double[getImageWidth()][3];
                int[] pixel = new int[4];
                stegaWriteableRaster = createStegaImage();
                Raster originalImageRaster = getImageRaster();
                ArrayList<StringBuffer[]> pixelArrayStringBuffer = new ArrayList<>();
                int check = 0;
                ArrayList<String> vidc = new ArrayList<>();

                StringBuffer[] tempArS = new StringBuffer[4];
                this.originalImageHeight = getImageHeight();
                this.getOriginalImageWidth = getImageWidth();

                StringBuffer R = new StringBuffer(),
                        G = new StringBuffer(),
                        B = new StringBuffer(),
                        A = new StringBuffer();

//        System.out.println("height ::: " + getImageHeight());
//        System.out.println("width ::: " + getImageWidth());
                jTextArea.append("The image Height ::: " + getImageHeight());
                jTextArea.append("The image width ::: " + getImageWidth());

                for (int y = 0; y < getImageHeight(); y += PIXEL_SIZE) {

                    for (int x = 0; x < getImageWidth(); x += PIXEL_SIZE) {

                        originalImageRaster.getPixel(x, y, pixel);
//                System.out.println(++check);

//                if (videoBinaryArray.size() == 0) {
//                     System.out.println("ALL THE VIDEO BYTES WERE HIDDEN SUCCESSFULLY ");
//                    System.out.println("X :: " + x + " Y  :: " + y);
//                    System.exit(0);
//                }
//                if (vidc.isEmpty() && !videoBinaryArray.isEmpty()) {
//                    System.out.println(++check);
//                }
                        if (!videoBinaryArray.isEmpty()) {
                            //remove the byte in the video and fill in
                            if (vidc.isEmpty()) {
//                        System.out.println(getBinaryString(videoBinaryArray.get(0)));
                                vidc.addAll(Arrays.asList(getBinaryString(videoBinaryArray.remove(0)).toString().split("")));
                            }
                        }

                        if (vidc.size() == 8) {

//                        System.out.println("---------------------------------------------------------------------");
//                        System.out.println(vidc.get(0) + vidc.get(1) + vidc.get(2) + vidc.get(3) );
//                        System.out.println("BEFORE " + Arrays.toString(pixel));
                            //pull out the first four valuez starting from the left hand side
                            R = getBinaryString(pixel[0]); // convert integer to stringbuffer
                            R.setCharAt(7, vidc.remove(0).charAt(0)); // change the LSB
                            pixel[0] = Integer.parseInt(R.toString(), 2); // R // set the new value

                            G = getBinaryString(pixel[1]);
                            G.setCharAt(7, vidc.remove(0).charAt(0));
                            pixel[1] = Integer.parseInt(G.toString(), 2); // G

                            B = getBinaryString(pixel[2]);
                            B.setCharAt(7, vidc.remove(0).charAt(0));
                            pixel[2] = Integer.parseInt(B.toString(), 2); // B

                            A = getBinaryString(pixel[3]);
                            A.setCharAt(7, vidc.remove(0).charAt(0));
                            pixel[3] = Integer.parseInt(A.toString(), 2); // A

//                        System.out.println("AFTER " + Arrays.toString(pixel));
//                        System.out.println("-----------------------------------------------------
                            stegaWriteableRaster.setPixel(x, y, pixel);
//                        System.out.println(++check);
//                    System.out.println(x + "::::"+ y);

//                    System.out.println("Done");
                        } else if (vidc.size() == 4) {

//                        System.out.println("---------------------------------------------------------------------");
//                        System.out.println(vidc.get(0)  + vidc.get(1) + vidc.get(2) + vidc.get(3) );
//                        System.out.println("BEFORE " + Arrays.toString(pixel));
                            R = getBinaryString(pixel[0]); // convert integer to stringbuffer
                            R.setCharAt(7, vidc.remove(0).charAt(0)); // change the LSB
                            pixel[0] = Integer.parseInt(R.toString(), 2); // R // set the new value

                            G = getBinaryString(pixel[1]);
                            G.setCharAt(7, vidc.remove(0).charAt(0));
                            pixel[1] = Integer.parseInt(G.toString(), 2); // G

                            B = getBinaryString(pixel[2]);
                            B.setCharAt(7, vidc.remove(0).charAt(0));
                            pixel[2] = Integer.parseInt(B.toString(), 2); // B

                            A = getBinaryString(pixel[3]);
                            A.setCharAt(7, vidc.remove(0).charAt(0));
                            pixel[3] = Integer.parseInt(A.toString(), 2); // A
//
//                        System.out.println("AFTER " + Arrays.toString(pixel));
//                        System.out.println("---------------------------------------------------------------------");

                            stegaWriteableRaster.setPixel(x, y, pixel);
//                        System.out.println("Done!");
//                        System.out.println(++check);
//                        System.out.println(x + "::::"+ y);
                        } else {
                            stegaWriteableRaster.setPixel(x, y, pixel); // set the default if the video bytes are all exhausted
//                            System.out.println("Ok");
                        }
                    }
                }

//        videoBinaryArray.forEach(integer -> {
//            System.out.println(integer);
//        });
                BufferedImage bufferedImage = new BufferedImage(stegaWriteableRaster.getWidth(), stegaWriteableRaster.getHeight(), BufferedImage.TYPE_INT_ARGB);
                bufferedImage.setData(stegaWriteableRaster);
                String extension = getFileExtentsion(getImageFile());
                stegaImageFileOut = stegaImageFileOut + "." + extension;
                jTextArea.append("\nComplete hidden the video ::::::: " + encryptedVideoInputFile);
                ImageIO.write(bufferedImage, extension, new File(stegaImageFileOut));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        processor.start();

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

            boolean stopper = false;

            for (int i = y; i < buf.size(); i++) { // ArrayList<ArrayList<StringBuffer[]>>

                if (!stopper) {
                    for (int i1 = x; i1 < buf.get(0).size(); i1++) { // ArrayBuffer<StringBuffer[]>
                        linked.put(i, buf.get(i));
//                System.out.println(" VBS END :: " + Arrays.asList(buf.get(i).toArray()));
                    }
                    stopper = true;
                } else {
                    for (int i1 = 0; i1 < buf.get(0).size(); i1++) { // ArrayBuffer<StringBuffer[]>
                        linked.put(i, buf.get(i));
//                System.out.println(" VBS END :: " + Arrays.asList(buf.get(i).toArray()));
                    }
                }
            }

            System.out.println("HEIGHT SIZE :::: " + linked.size());
            System.out.println("WIDTH SIZE :::: " + linked.get(0).size());

//        for (int i = 0; i < y; i++) { //height
//            for (int i1 = 0; i1 < x ; i1++) { //width
//                StringBuffer[] sb = linked.get(i).get(i1);
//                stegaWriteableRaster.setPixel(i1, i, getIntegerValue(sb));
//                System.out.println(Arrays.toString(sb));
//            }
//        }
            createStanoImage(linked);
            stopAllThreadExecutorservices(executorService);
            System.out.println(buf.size() + "-------------->>>>>>>   " + linked.size());

        } catch (Exception e) {
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

        StringBuffer[] strBuf = new StringBuffer[4];

        ArrayList<StringBuffer[]> sba = new ArrayList<>();

        if (((binaryStringBuffer.size() * (binaryStringBuffer.get(0).size() * 4))) < (2 * videoIntegers.size())) {
            System.out.println("VIDEO SIZE ::: " + videoIntegers.size() + "IMAGE PIXELS :::: " + (binaryStringBuffer.size() * binaryStringBuffer.get(0).size() * 4));
            System.err.println("ERROR_OCCURRED :::: The video is too large to be hidden with the given picture");
        }
        int vidPos = 0;
        ArrayList<String> vbs = new ArrayList<>();
        // ArrayList <ArrayList<StringBuffer[]>>
        //creating the thread pool for video embedding
        while (!videoIntegers.isEmpty()) {
            System.out.println(videoIntegers.size() + ":::" + videoIntegers.size() * 2);
            try {
                int nextVidInt = videoIntegers.remove(0);
                System.out.println("NEXT []----------> " + getBinaryString(nextVidInt));
                vbs.addAll(Arrays.asList(getBinaryString(nextVidInt).toString().split("")));
                for (int i = 0; i < binaryStringBuffer.size(); i++) { // ArrayList<ArrayList<StringBuffer[]>>
                    for (int i1 = 0; i1 < binaryStringBuffer.get(i).size(); i1++) { // ArrayBuffer<StringBuffer[]>
//                        System.out.println( videoIntegers.size());

                        if (videoIntegers.size() == 0 && vbs.isEmpty()) {

                            System.out.println("Video Size :::: " + "::::: " + videoIntegers.size());
                            System.out.println("Congratulations... all the video bytes are now inside the picture");
                            System.out.println("Done creating stegano image");

//                            System.out.println(i + ":::: " + "::::: " + i1);
//                            createVideoEnpoint(i, i1);
                            appendRest(binaryStringBuffer, stegaImageBufferStringArray, i, i1);

                            System.exit(0);

                        } else if (vbs.isEmpty()) {
//                            System.out.println(i + "---------- " + i1);
//                        System.out.println("waiting");
                            int nextVidInt1 = videoIntegers.remove(0);
//                        System.out.println("NEXT ----------> " + getBinaryString(nextVidInt1));
                            vbs.addAll(Arrays.asList(getBinaryString(nextVidInt1).toString().split("")));
//                            System.out.println(vbs.get(0) + "" + vbs.get(1) +""+vbs.get(2)+""+vbs.get(3));

//                        System.out.println("NEW VBS:: " + Arrays.toString(vbs.toArray()));
//                            strBuf = binaryStringBuffer.get(i).get(i1);
//                            strBuf[0].setCharAt(7, vbs.remove(0).charAt(0));
//                            strBuf[1].setCharAt(7, vbs.remove(0).charAt(0));
//                            strBuf[2].setCharAt(7, vbs.remove(0).charAt(0));
//                            strBuf[3].setCharAt(7, vbs.remove(0).charAt(0));
//                            sba.add(new StringBuffer[]{strBuf[0], strBuf[1], strBuf[2], strBuf[3]});
//                            stegaImageBufferStringArray.put(i, sba);
                            hide(i, i1, binaryStringBuffer, strBuf, sba, vbs, stegaImageBufferStringArray);

                        } else {
                            System.out.println(i + ":::: " + "::::: " + i1);
//                            System.out.println(vbs.get(0) + "" + vbs.get(1) +""+vbs.get(2)+""+vbs.get(3));

                            hide(i, i1, binaryStringBuffer, strBuf, sba, vbs, stegaImageBufferStringArray);

                            //System.out.println(" VBS END :: " + Arrays.toString(strBuf));
//                        System.out.println("VBS END:: " + Arrays.toString(vbs.toArray()));
//                        System.out.println("---------- AFTER ------------"  +   strBuf[0].toString() );
//                        System.out.println("---------- AFTER ------------"  +   strBuf[1].toString() );
//                        System.out.println("---------- AFTER ------------"  +   strBuf[2].toString() );
//                        System.out.println("---------- AFTER ------------"  +   strBuf[3].toString() );
//                        System.out.println("------------------------------------------------------------------------------\n\n\n\n");
                        }
                    }
                    sba = new ArrayList<>(); // empty the row of data from the sba
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

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
//            if (arrayList.size() == 1000000) {
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
        return arrayList;
    }

    private static ArrayList<Integer> copySomeBytes(byte[] videoBytes) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(
                new FileInputStream("src\\original-video.mp4"));
        BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream("copied-original8.mp4"));
        ArrayList<Integer> arrayList = new ArrayList<>();
        int b;
        byte[] vb = new byte[900000];
        while ((b = bufferedInputStream.read(vb)) != -1) {
            bo.write(vb, 0, b);
            break;

        }
        System.out.println("bytes copied");
        return arrayList;
    }

    private static void hide(int i, int i1,
            HashMap<Integer, ArrayList<StringBuffer[]>> binaryStringBuffer,
            StringBuffer[] strBuf,
            ArrayList<StringBuffer[]> sba,
            ArrayList<String> vbs,
            LinkedHashMap<Integer, ArrayList<StringBuffer[]>> stegaImageBufferStringArray
    ) {
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
    private static WritableRaster createStanoImage(HashMap<Integer, ArrayList<StringBuffer[]>> steganoImageStringBinaries) {
        System.out.println(stegaWriteableRaster.getHeight());
        System.out.println(stegaWriteableRaster.getWidth());
        try {
            int[] test = new int[4];
            System.out.println("---------------------------------------");

            for (int i = 0; i < steganoImageStringBinaries.size(); i++) { //height
                for (int i1 = 0; i1 < steganoImageStringBinaries.get(i).size(); i1++) {
                    StringBuffer[] sb = steganoImageStringBinaries.get(i).get(i1);
                    stegaWriteableRaster.setPixel(i1, i, getIntegerValue(sb));
                }
            }

            BufferedImage bufferedImage = new BufferedImage(stegaWriteableRaster.getWidth(), stegaWriteableRaster.getHeight(), BufferedImage.TYPE_INT_ARGB);
            bufferedImage.setData(stegaWriteableRaster);
            String extension = getFileExtentsion(getImageFile());
            ImageIO.write(bufferedImage, extension, new File("stegano-image" + extension));

            return stegaWriteableRaster;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param threads
     * @return
     */
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
    public static ArrayList<Integer> extractVideos(BufferedImage bufferedImage, JTextArea j) throws IOException {
        j.append("\n>>>>Extraction has started...\n>>>>>Please wait...\n");
        Thread proce2 = new Thread(new Runnable() {
            @Override
            public void run() {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                try {

                    WritableRaster wr = ImageIO.read(new File(stegaImageFileInput)).getRaster();
                    int height = wr.getHeight();
                    int width = wr.getWidth();
                    ArrayList<Integer> videoExtractedIntegers = new ArrayList<>();
                    int count = 0;

                    System.out.println(height + ":::: " + width);

                    int[] stegaImagePixel = new int[4];
//            String[] spec = new BufferedReader(new FileReader("spec.code")).readLine().split(", ");
                    String spec = stegaImageFileInput.split("_")[2];

                    Integer videobyteLength = Integer.parseInt(spec);

                    StringBuffer R, G, B, A;
                    System.out.println(height + ":::" + width);

                    StringBuffer s = new StringBuffer();
                    for (int y = 0; y < height; y += PIXEL_SIZE) {

                        for (int x = 0; x < width; x += PIXEL_SIZE) {

                            wr.getPixel(x, y, stegaImagePixel);

                            R = getBinaryString(stegaImagePixel[0]);
                            G = getBinaryString(stegaImagePixel[1]);
                            B = getBinaryString(stegaImagePixel[2]);
                            A = getBinaryString(stegaImagePixel[3]);

                            s.append(R.charAt(7));
                            s.append(G.charAt(7));
                            s.append(B.charAt(7));
                            s.append(A.charAt(7));
//                    System.out.println(++count);

                            if (s.length() == 8) {
                                videoExtractedIntegers.add(Integer.parseInt(s.toString(), 2));
//                        System.out.println(s);
                                s = new StringBuffer();
                                int remainder = (videobyteLength - count);
//                                j.append("\n>>> Remianing ::: " +  count );
                                System.out.println(++count);
                            }

                            if (videoExtractedIntegers.size() == videobyteLength) {
//                        if ( videoExtractedIntegers.size() == Integer.parseInt(spec[0])) {
                                j.append("\nExtraction completed...\n");
                                writeExtractedVideo(videoExtractedIntegers, j);
                                System.out.println("Done" + videoExtractedIntegers.size());
                                j.append(("Done Extracting the video"));
//                        System.exit(0);
                            }

                        }
                    }
//                return videoExtractedIntegers;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        proce2.start();
        proce2.interrupt(); // stop thread after executing

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

    public static void writeExtractedVideo(ArrayList<Integer> vidA, JTextArea j) {
        System.out.println("WRITING THE VIDEO INSIDE A NEW FILE WITH THE EXTENSION");
        j.append("WRITING THE VIDEO INSIDE A NEW FILE WITH THE EXTENSION");

        try (BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(new File(extractedVideo + "." + stegaImageFileInput.split("_")[1])))) {
            while (!vidA.isEmpty()) {
//                System.out.println(getBinaryString(vidA.get(0)));
                bo.write(vidA.remove(0).byteValue());
            }
            bo.flush();
            /*decode the encrypted video from the extracted video and write to a file*/
//            RSAStegaCrypt.doDecryptRSAWithAES(new File(extractedVideo + stegaImageFileOut.split("_")[1]),stegaImageFileInput.split("_")[1]);
//            System.out.println("Successful!");
//            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @return
     */
    private static String getImageFile() {
        return "C:\\Users\\user\\Desktop\\cap3.PNG";
    }

    /**
     *
     * @return
     */
    private static BufferedInputStream getVideoInputStream() throws IOException {
//        System.out.println(Arrays.toString(encryptedVideoInputFile.split("\\.")));
        stegaImageFileOut = "stega-image_" + encryptedVideoInputFile.split("\\.")[1];
        System.out.println(stegaImageFileOut);
        return new BufferedInputStream(new FileInputStream(encryptedVideoInputFile));
//        return new BufferedInputStream(new FileInputStream("out.dec.mp4"));

    }

    /**
     *
     * // * @param y // * @param x
     *
     */
    private static void createVideoEnpoint(long s) {

        stegaImageFileOut = stegaImageFileOut + "_" + s + "_";
//        System.out.println(stegaImageFileOut);
    }

}
