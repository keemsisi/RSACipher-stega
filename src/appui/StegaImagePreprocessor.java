package appui;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import javax.imageio.ImageIO;
import javax.imageio.metadata.IIOMetadata;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author techx
 */
public class StegaImagePreprocessor {

    private static File imageFile;
    private BufferedImage originalBufferedImage;
    private static WritableRaster stegaWriteableRaster;
    private BufferedImage stegaImage;
    private static final int PIXEL_SIZE = 1; // the size of each pixel
    private static int originalImageHeight = 0 ;
    private static int getOriginalImageWidth = 0 ;

    public StegaImagePreprocessor(File imageFileName) {
        this.imageFile = imageFileName;
    }
    private static final ExecutorService executorService = createThreadPoolsForVideoEmbedding(10);
    HashMap<Integer,ArrayList<StringBuffer[]>> imagePixelResult = new HashMap<>(); //the result of the pixels extracted



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

    private static boolean done = false ;

//    private HashMap<Integer , ArrayList<StringBuffer[]>> getAllPixelsFromOriginalImage() throws Exception {
        private void getAllPixelsFromOriginalImage() throws Exception {
        processBufferedImage();
        int count = 0;
        double[][] pixels = new double[getImageWidth()][3];
        int[] pixel = new int[4];
        stegaWriteableRaster = createStegaImage();
        //the the raster image for the original image will be assigined to the Raster
        Raster originalImageRaster = getImageRaster();
        ArrayList<StringBuffer[]> pixelArrayStringBuffer = new ArrayList<>();



        StringBuffer[] tempArS = null;
        this.originalImageHeight = getImageHeight();
        this.getOriginalImageWidth = getImageWidth();

        System.out.println("Video height ::: " + imagePixelResult.size());

        for (int y = 0 ; y < getImageHeight() ; y += PIXEL_SIZE ) {

            readImagePixelsConcurrently(y, originalImageRaster, imagePixelResult);

////            if (count > 3 ) break ;
//            for (int x = 0; x < getImageWidth(); x += PIXEL_SIZE) {
//
//                // [ A , R , G , B ] returned
//                // correct this logic later
//                int R = originalImageRaster.getPixel(x, y, pixel)[0]; // R
//                int G = originalImageRaster.getPixel(x, y, pixel)[1]; // G
//                int B = originalImageRaster.getPixel(x, y, pixel)[2]; // B
//                int A = originalImageRaster.getPixel(x, y, pixel)[3]; // A
//
//
////                int R = (originalBufferedImage.getRGB(x, y) && 0x00 ) >> 23; // R
////                int G = originalBufferedImage.getRGB(x, y); // G
////                int B = originalBufferedImage.getRGB(x, y); // B
////                int A = originalBufferedImage.getRGB(x, y); // A
//
//
//                tempArS = new StringBuffer[]{getBinaryString(R), getBinaryString(G), getBinaryString(B) , getBinaryString(A) };
//                pixelArrayStringBuffer.add(tempArS);
//
////                int a = originalImageRaster.getPixel(x, y);
////                System.out.println( "A :::: " + A + "   BINARY     " + Integer.toBinaryString(A) );
////                System.out.println( "R :::: " + R + "   BINARY     " + Integer.toBinaryString(R) );
////                System.out.println( "G :::: " + G + "   BINARY     " + Integer.toBinaryString(G) );
////                System.out.println( "B :::: " + B + "   BINARY     " + Integer.toBinaryString(B) );
////                System.out.println("--------------------------------------");
////
////                ++count ;
////                if (count > 30000 ) break ;
//                //create the stega image in here
////                for (int yd = y; (y < PIXEL_SIZE) && (y < originalImageRaster.getHeight()); y++) {
////                    for (int xd = x; (x < PIXEL_SIZE) && (x < originalImageRaster.getWidth()); x++) {
////                        //process original image pixel and set to stega image pixel
////                        this.stegaWriteableRaster.setPixel(xd, yd, pixel);
////                    }
////                }
//            }
//            imagePixelResult.add(pixelArrayStringBuffer);
        }

//        this.stegaImage.setData(stegaWriteableRaster);// write the stega pixels insid stega image
//
//        ImageIO.write(stegaImage, "png", new File("stega-image.png"));
//        System.out.println(imagePixelResult.size());
//        System.out.println(imagePixelResult.size() );
//        return imagePixelResult;

    }

    private class PixelProcessing< R, G, B> {

        private R redColor;
        private G greenColor;
        private B blueColor;

        public PixelProcessing(R red, G green, B blue) {
            this.redColor = red;
            this.greenColor = green;
            this.blueColor = blue;
        }

        public R getColorRed() {
            return redColor;
        }

        public R getColorGreen() {
            return redColor;
        }

        public R getColorBlue() {
            return redColor;
        }
    }

    /**
     *
     * @param p The pixel integer value to be converted to binary string
     * @return StringBuffer object type is being returned
     */
    public static StringBuffer getBinaryString(int p) {
        StringBuffer stringBuffer = new StringBuffer(Integer.toBinaryString(p));
        StringBuffer newStringBuffer = new StringBuffer();
        int checker = 8 -  stringBuffer.length();
        if (  checker > 0 ) {
            char[] chars = new char[checker];
            for (int i = 0; i < chars.length ; i++) {
                chars[i] = '0'; /*character '0' will be initialised to all the char[]*/
            }for (char aChar : chars) {
             newStringBuffer.append(aChar); /**append all the chars from the char[]*/
            }
            Arrays.stream(stringBuffer.toString().split("")).forEach(s -> {
                newStringBuffer.append(s);
//                System.out.println(newStringBuffer.length());
            });
//            System.out.println("------------------------------------------------------");
//            System.out.printf("BEFORE :::::%5s\n",stringBuffer);
//            System.out.printf("AFTER :::::%5s\n",newStringBuffer);
//            System.out.println("------------------------------------------------------");
            return newStringBuffer;
        }return stringBuffer;
    }

//    public static void main(String[] args) throws Exception {
//
//        StegaImagePreprocessor imageProcessor = new StegaImagePreprocessor(new File(getImageFile()));
////        imageProcessor.processBufferedImage();
////        imageProcessor.getAllPixelsFromOriginalImage()
////                .stream()
////                .parallel()
////                .forEach(stringBuffer -> {
//////                    System.out.println(Arrays.toString(stringBuffer));
////                    System.out.println(
////                            Arrays.toString(
////                                    new int[]{
////                                        Integer.parseInt(stringBuffer[0].toString(), 2),
////                                        Integer.parseInt(stringBuffer[1].toString(), 2),
////                                        Integer.parseInt(stringBuffer[2].toString(), 2),
////                                        Integer.parseInt(stringBuffer[3].toString(), 2)
////                                    })
////                    );
////                });
//
////        imageProcessor.getAllPixelsFromOriginalImage()
////                .stream()
////                .parallel()
////                .forEach(stringBuffer -> {
//////                    System.out.println(Arrays.toString(stringBuffer));
//////                    System.out.println( );
////                });
//
////        System.out.println(Arrays.toString(getpixelIntergerArr(imageProcessor.getAllPixelsFromOriginalImage())));
//
//
////        get all the integer value of the image pixels
////        getpixelIntergerArr(imageProcessor.getAllPixelsFromOriginalImage()).forEach(integers -> {
////            System.out.println(Arrays.toString(integers));
////        });
////        getpixelIntergerArr(imageProcessor.getAllPixelsFromOriginalImage());
//
////        getpixelBinaryStringArr(imageProcessor.getAllPixelsFromOriginalImage()).forEach(integers -> {
////            System.out.println(Arrays.toString(integers));
////        });
//
//
////        BufferedInputStream bufferedInputStream =  new BufferedInputStream(
////                new FileInputStream("C:\\Users\\NeuralTechX CEO\\Documents\\RSAWithSteganography\\new-encryptedvid.enc")
////        );
////
////
////
//////        arrayList.forEach(System.out::println);
////        System.out.println("Starting to perform LSB manipulation");
////        imageProcessor.getAllPixelsFromOriginalImage();
//        extractVideos(null);
////        getVideoBinaryArray(null);
////        getVideoBinaryArray(null);
////
//    }
//
//    private static ArrayList<Integer[]> getpixelIntergerArr(ArrayList<StringBuffer[]> stringbuffferArray) {
//        ArrayList<Integer[]> result = new ArrayList<Integer[]>();
//
//        for (StringBuffer[] stringBuffer : stringbuffferArray) {
//            result.add(
//                    new Integer[]{
//                            Integer.parseInt(stringBuffer[0].toString(), 2),
//                            Integer.parseInt(stringBuffer[1].toString(), 2),
//                            Integer.parseInt(stringBuffer[2].toString(), 2),
//                            Integer.parseInt(stringBuffer[3].toString(), 2)
//                    });
//        }
//
//        return result ;
//
//    }

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
        return result ;
    }

    private static LinkedHashMap<Integer,ArrayList<StringBuffer[]>> appendRest(HashMap<Integer , ArrayList<StringBuffer[]>> buf, LinkedHashMap<Integer , ArrayList<StringBuffer[]>> linked , int y , int x) {

        for ( int i = y; i < buf.size(); i++) { // ArrayList<ArrayList<StringBuffer[]>>
            for (int i1 = x; i1 < buf.get(i).size(); i1++) { // ArrayBuffer<StringBuffer[]>
//                System.out.println("ABOUT TO APPEND");
//                System.out.println(Arrays.toString(buf.get(i).get(i1)));
//                for (int i3 = x; i3 < buf.get(i).size(); i3++) { // ArrayBuffer<StringBuffer[]>
//////                    System.out.println(Arrays.toString(buf.get(i).get(i3)));
//////                }
                    linked.put(i , buf.get(i));
            }

        }

//        System.out.println("START CHECKINGS");
//
//        for ( int k = 0; k < linked.size(); k++) { // ArrayList<ArrayList<StringBuffer[]>>
//            for ( int j = 0; j < linked.get(k).size() ; j++) { // ArrayBuffer<StringBuffer[]>
//                StringBuffer[] sb = linked.get(k).get(j);
//                System.out.println(Arrays.toString(sb));
//                if (k == 2){
//                    System.exit(0);
//                }
//            }
//        }

//      System.out.println("END CHECKINGS");
        System.out.println(buf.size() + "-------------->>>>>>>   " + linked.size());

        return linked ;
    }

        /**
         *
         * @param binaryStringBuffer The arrays of bufferstrings from the image
         * pixels
         * @param videoIntegers An array of binary string from the video bytes
         * @return
         */
    private static HashMap<Integer,ArrayList<StringBuffer[]>> changeLSBs(HashMap<Integer , ArrayList<StringBuffer[]>> binaryStringBuffer, ArrayList<Integer> videoIntegers) {

        LinkedHashMap<Integer ,ArrayList<StringBuffer[]> > stegaImageBufferStringArray = new LinkedHashMap<>();
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
         * This method will hide all the video bytes which has been converted to binaries
         * into a given image. All the pixels in the image are also in the form of binaries.
         * The Least Significant Bit (LSB) of each binaries will be removed and the the bit in the
         * video binaries will be replaced with the bit removed from the image binary.
         */
        ArrayList<String[]> result = new ArrayList<>();

        ArrayList<String> videoArrayBitStrings = new ArrayList<>();

        String temp = "";

        int nextPos = 0;

        int n = 0 ;

        int nextP = 0 ;


        StringBuffer[] strBuf = new StringBuffer[4];

        ArrayList<StringBuffer[]> sba = new ArrayList<>() ;

        StringBuffer[] sBuf = null ;

        ArrayList<StringBuffer[]> imageStringBuffer = getNextImageStringBufferArray(++n, binaryStringBuffer) ;

        int ORIGINAL_IMAGE_PIXEL_LENGTH = binaryStringBuffer.size() ;


        ArrayList<StringBuffer[]> temBuffer = new ArrayList<>(); // store temporary string buffer

        ArrayList<ArrayList<StringBuffer[]>> steganoSringBufbinaryArray = new ArrayList<>(); // the stegano bufferedm string

        if ( ( ( binaryStringBuffer.size() * binaryStringBuffer.get(0).size() * 4 ) ) < ( 8 * videoIntegers.size() ) ) {
            System.out.println("VIDEO SIZE ::: " + videoIntegers.size() + "IMAGE PIXELS :::: " + ( binaryStringBuffer.size() * binaryStringBuffer.get(0).size() * 4 ) );
            System.err.println("ERROR_OCCURRED :::: The video is too large to be hidden with the given picture");
            return null; //
        }
        int vidPos  = 0 ;
        ArrayList<String> vbs = new ArrayList<>();
        // ArrayList <ArrayList<StringBuffer[]>>
        //creating the thread pool for video embedding
        while (!videoIntegers.isEmpty()) {
        try {
            int nextVidInt = videoIntegers.remove(0);
            System.out.println("NEXT []----------> " + getBinaryString(nextVidInt));
            vbs.addAll(Arrays.asList(getBinaryString(nextVidInt).toString().split("")));
            for ( int i = 0; i < binaryStringBuffer.size(); i++)
            { // ArrayList<ArrayList<StringBuffer[]>>
                for ( int i1 = 0; i1 < binaryStringBuffer.get(i).size() ; i1++) { // ArrayBuffer<StringBuffer[]>

                    if ( videoIntegers.size() == 0 ) {

                        System.out.println("Video Size :::: " + "::::: " + videoIntegers.size());
                        System.out.println("Congratulations... all the video bytes are now inside the picture");
                        stopAllThreadExecutorservices(executorService);
                        System.out.println("Done creating stegano image");
                        createVideoEnpoint(i , i1);
                        appendRest(binaryStringBuffer , stegaImageBufferStringArray , i , i1);
                        createStanoImage(stegaImageBufferStringArray);


                        stopAllThreadExecutorservices(executorService);
                        System.exit(0);



                    }else if ( vbs.isEmpty() ) {
//                        System.out.println("waiting");
                        int nextVidInt1 = videoIntegers.remove(0);
//                        System.out.println("NEXT ----------> " + getBinaryString(nextVidInt1));
                        vbs.addAll(Arrays.asList(getBinaryString(nextVidInt1).toString().split("")));
//                        System.out.println("NEW VBS:: " + Arrays.toString(vbs.toArray()));

                    }else {

                        strBuf = binaryStringBuffer.get(i).get(i1);

//                        System.out.println("VBS START:: " + Arrays.toString(vbs.toArray()));
//                        binaryStringBuffer.get(i).get(i1)[0].setCharAt(7 , vbs.remove(0).charAt(0));
//                        binaryStringBuffer.get(i).get(i1)[1].setCharAt(7 , vbs.remove(0).charAt(0));
//                        binaryStringBuffer.get(i).get(i1)[2].setCharAt(7 , vbs.remove(0).charAt(0));
//                        binaryStringBuffer.get(i).get(i1)[3].setCharAt(7 , vbs.remove(0).charAt(0));

                        strBuf[0].setCharAt(7 , vbs.remove(0).charAt(0));
                        strBuf[1].setCharAt(7 , vbs.remove(0).charAt(0));
                        strBuf[2].setCharAt(7 , vbs.remove(0).charAt(0));
                        strBuf[3].setCharAt(7 , vbs.remove(0).charAt(0));


                        sba.add(new StringBuffer[]{strBuf[0] , strBuf[1] , strBuf[2] , strBuf[3]});

                        stegaImageBufferStringArray.put(i , sba);

//                        System.out.println("VBS END:: " + Arrays.toString(vbs.toArray()));
//                        System.out.println("---------- AFTER ------------"  +   strBuf[0].toString() );
//                        System.out.println("---------- AFTER ------------"  +   strBuf[1].toString() );
//                        System.out.println("---------- AFTER ------------"  +   strBuf[2].toString() );
//                        System.out.println("---------- AFTER ------------"  +   strBuf[3].toString() );
//                        System.out.println("------------------------------------------------------------------------------\n\n\n\n");

                    }

//                    for ( int i3  = 0 ; i3 < binaryStringBuffer.get(i).get(i1).length ; i3++){ // StringBuffer[]
////                        System.out.println("NOT EMPTY" + videoIntegers.size());
//                        if ( videoIntegers.size() == 0 ) {
//                            System.out.println("Video Size :::: " + "::::: " + videoIntegers.size());
//                            System.out.println("Congratulations... all the video bytes are now inside the picture");
//                            stopAllThreadExecutorservices(executorService);
//                            //create stega image from here
//                            createStanoImage(binaryStringBuffer);
//                            System.out.println("Done creating stegano image");
//                            createVideoEnpoint(i , i1 , i3);
//                            System.exit(0);
//                        }else if ( vbs.isEmpty() ){
//                            System.out.println("waiting");
//                            int nextVidInt1 = videoIntegers.remove(0);
//                            System.out.println("NEXT ----------> " + getBinaryString(nextVidInt1));
//                            vbs.addAll(Arrays.asList(getBinaryString(nextVidInt1).toString().split("")));
//
//                        }else {
//
//                            System.out.println("i3 :::: " + i3);
//                            System.out.println("VBS :: " + Arrays.toString(vbs.toArray()));
//                            StringBuffer stringBuffers = binaryStringBuffer.get(i).get(i1)[i3] ;
////                            System.out.println("----------------------------------------------------------------------");
////                            System.out.println("---------- BEFORE ------------" +   binaryStringBuffer.get(i).get(i1)[i3].toString() );
//                            binaryStringBuffer.get(i).get(i1)[i3].setCharAt(7 , vbs.remove(0).charAt(0));
////                            System.out.println("---------- AFTER ------------"  +   stringBuffers );
//                            System.out.println("---------- AFTER ------------"  +   binaryStringBuffer.get(i).get(i1)[i3].toString() );
////                            System.out.println("------------------------------------------------------------------------------\n\n\n\n");
////                            System.out.println("i :::: " + i + "i :::: " + i1 + "i3 :::: " + i3);
//                        }
//                        System.out.println("-------------------DONE-----------------");
//                        System.out.println("-------------------ok-----------------");
//
//                    }
                }
                sba = new ArrayList<>(); // empty the row of data from the sba
            }



        }catch (Exception e) {
            e.printStackTrace();
        }

        }
        return binaryStringBuffer ;

//        for (String vidByteBinString : videoIntegers) {
//
//            videoArrayBitStrings.addAll(Arrays.asList(vidByteBinString.split(""))); //returns arrays of binary values
//
//            while (!videoArrayBitStrings.isEmpty()) {
//
////                String newLSB = videoArrayBitStrings.remove(videoArrayBitStrings.size() - 1);
//                String newLSB;
//
//                if ( nextPos == 4 ) {
//                    steganoSringBufbinaryArray.add(temBuffer);
//                    temBuffer = new ArrayList<>(); //clear the string buffer array
//                            imageStringBuffer  =  getNextImageStringBufferArray(++n, binaryStringBuffer);
//                    nextPos = 0 ;
//                }
//
//                if ( videoArrayBitStrings.isEmpty() ) {
//                    //reload another video stringbuffer array
//                    imageStringBuffer  =  getNextImageStringBufferArray(ORIGINAL_IMAGE_PIXEL_LENGTH++, binaryStringBuffer); // get the imageStringBuffer ready for the next operation
//                }
//
//                n = nextPos++ ;
//
//                if (nextP == imageStringBuffer.size() ){
//                    nextP = 0 ; // resset
//                }
//                sBuf = imageStringBuffer.get(nextP); // get the next StringBuffer[]
//
//                System.out.println("Current sBuf :::: " + sBuf);
//
//                //the LSB position of the
////                sBuf[n].setCharAt( 7 , newLSB.charAt(0));
//
//                for (int i = 0 ; i < sBuf.length ;  i ++ ) {
//                    if ( videoArrayBitStrings.isEmpty() ) {
//                        //reload another video stringbuffer array
//                        imageStringBuffer  =  getNextImageStringBufferArray(ORIGINAL_IMAGE_PIXEL_LENGTH++, binaryStringBuffer); // get the imageStringBuffer ready for the next operation
//                    }
//                    newLSB = videoArrayBitStrings.remove(videoArrayBitStrings.size() - 1);
//                    sBuf[i].setCharAt( 7 , newLSB.charAt(0));
//                    if (i + 1 == sBuf.length) {
//                        setNextP(nextP, imageStringBuffer,  sBuf);
//                    }
//                }
//                System.out.println("Latest sBuf :::: " + sBuf);
//


    }
    /**
     *
     * @param index The index to get from the ArrayList<StringBuffer[]> object
     * @return
     */
    private static ArrayList<StringBuffer[]> getNextImageStringBufferArray(int index  , HashMap<Integer,ArrayList<StringBuffer[]>> stringBuffer) {
        return stringBuffer.get(index);
    }


    /**
     *
     * @param index
     * @param pixelStringBuffers
     * @param stringBuffer
     * @return
     */
    private static ArrayList<StringBuffer[]> setNextStringBufferArray(int index  , ArrayList<ArrayList<StringBuffer[]>> pixelStringBuffers , ArrayList<StringBuffer[]> stringBuffer) {
        return pixelStringBuffers.set(index,stringBuffer);
    }


    /**
     *
     * @param index
     * @param stringBuffer
     * @param p
     * @return
     */
    private static boolean setNextP(int index  , ArrayList<StringBuffer[]> stringBuffer , StringBuffer[] p) {
        try {
            stringBuffer.set(index,p);
            return true ;
        }catch (Exception e) {
            e.printStackTrace();
        }
       return false ;
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
        int b ;
        byte[] vb  = new byte[1];
        System.out.println("Reading the video bytes...\n please wait...");
        int count = 0 ;
        while ( ( b = bufferedInputStream.read() ) != -1) {
            if (++count == 800)  break;
//            for (int i = 0 ; i < b ; i++) {
//            System.out.println(getBinaryString(b));
//            getBinaryString(b);
                arrayList.add(b);
//            }
        }
        bufferedInputStream = null ; //free the memory
//        System.out.println("Video bytes read successfully");
        return arrayList ;
    }

    private static WritableRaster createStanoImage(LinkedHashMap<Integer , ArrayList<StringBuffer[]>> steganoImageStringBinaries){
//        System.out.println(stegaWriteableRaster.getHeight());
//        System.out.println(Arrays.toString(stegaWriteableRaster.getPixel(0, 0, new int[4])));


        try {
            int height = originalImageHeight ;
            int width  = getOriginalImageWidth;
            int[] test = new int[4];
            for (int i = 0; i < steganoImageStringBinaries.size(); i++) { //height
                for (int i1 = 0; i1 < steganoImageStringBinaries.get(i).size(); i1++) { //width
                    //convert StringBuffer[] to the integer values
//                    System.out.println(Arrays.toString(steganoImageStringBinaries.get(i).get(i1)));
                    StringBuffer[] sb = steganoImageStringBinaries.get(i).get(i1);
                    stegaWriteableRaster.setPixel(i1, i,getIntegerValue(sb));
//                    stegaWriteableRaster.getPixel(i1, i,test);
//                    StringBuffer[] tempArS = new StringBuffer[]{getBinaryString(test[0]), getBinaryString(test[1]), getBinaryString(test[2]) , getBinaryString(test[3]) };

//                    System.out.println(Arrays.toString(sb));
//                    System.out.println("---------------------------------------");

//                    if (i == 2){
//                        System.exit(0);
//                    }

                }
            }

            System.out.println(Arrays.toString(stegaWriteableRaster.getPixel(0, 0, new int[4])));
            BufferedImage bufferedImage = new BufferedImage(stegaWriteableRaster.getWidth() , stegaWriteableRaster.getHeight(), BufferedImage.TYPE_INT_ARGB );
            bufferedImage.setData(stegaWriteableRaster);
            ImageIO.write(bufferedImage , getFileExtentsion(getImageFile()) , new File("stegano-image.png"));

            return stegaWriteableRaster;

        }catch (Exception e) {
            e.printStackTrace();
        }
      return null;
    }


    private static ExecutorService createThreadPoolsForVideoEmbedding(int threads) {
        return Executors.newFixedThreadPool(10);
    }


    /**
     *
     * @param executorService The executor service object parsed and then shutdown
     * @return
     */
    private static boolean stopAllThreadExecutorservices(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
                return  true ;
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
    private  static List<Runnable> shutdownAndReturTheListOfWaitingThreads(ExecutorService executorService){
        return executorService.shutdownNow();

    }


    /**
     *
     * @param y
     * @param originalImageRaster
     * @param pixelMappingss
     * @return
     */
    private Future<HashMap<Integer , ArrayList<StringBuffer[]>>> readImagePixelsConcurrently(int y, Raster originalImageRaster , HashMap<Integer , ArrayList<StringBuffer[]>> pixelMappingss ) {
        ArrayList<StringBuffer[]> stringBuffers = new ArrayList<>(); /***/
        int[] pixel = new int[4];
        executorService.submit(()->{
            for ( int x = 0 ; x < getImageWidth() ; x += PIXEL_SIZE ) {
                originalImageRaster.getPixel(x, y, pixel);
                StringBuffer[] tempArS = new StringBuffer[]{getBinaryString(pixel[0]), getBinaryString(pixel[1]), getBinaryString(pixel[2]) , getBinaryString(pixel[3]) };
//                System.out.println(Arrays.toString(tempArS));
                stringBuffers.add(tempArS);
            }
            pixelMappingss.put( y ,stringBuffers );
            if (pixelMappingss.size() == getImageHeight() ) {
//                System.out.println("Done reading all the image\nEmbedding the video now... please wait");
//                stopAllThreadExecutorservices(executorService);
//                System.out.println("IMAGE PIXEL SIZE :::: " + pixelMappingss.size() * pixelMappingss.get(0).size() * 4);
//                pixelMappingss.forEach((integer, stringBuffers1) -> {
//                    System.out.println(stringBuffers1.size() * 4 * pixelMappingss.size() );
//                });
                changeLSBs(pixelMappingss,getVideoBinaryArray(null));
//                System.out.println("Video Embedding was successful...");
            }
            return pixelMappingss ;
        });
        return  null ;
    }

    /**
     *
     * @param sb The StringBuffer[] to be converted to double[]
     * @return Returns double[] which contains pixel values (RGBA)
     */
    private static double[] getIntegerValue(StringBuffer[] sb) {
      return   new double[]{
              Integer.parseInt(sb[0].toString(),2),
              Integer.parseInt(sb[1].toString(),2),
              Integer.parseInt(sb[2].toString(),2),
              Integer.parseInt(sb[3].toString(),2)
      };
    }


    /**
     *
     * @param bufferedImage
     * @return
     */
    private static ArrayList<Integer> extractVideos(BufferedImage bufferedImage){

        try {

//            WritableRaster wr =  bufferedImage.getRaster();
            WritableRaster wr =  ImageIO.read(new File("stegano-image.png")).getRaster();
            int height = wr.getHeight() ;
            int width = wr.getWidth() ;
            String tempString = "";
            ArrayList<Integer> videoExtractedIntegers = new ArrayList<>();

            int[] stegaImagePixel = new int[4];
            String[] spec = new BufferedReader(new FileReader("spec.code")).readLine().split(",");
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x+= PIXEL_SIZE ) {
                    wr.getPixel(x, y, stegaImagePixel);

                    tempString = tempString.concat(
                                            getBinaryString(stegaImagePixel[0]).toString().split("")[7] + "" +
                                            getBinaryString(stegaImagePixel[1]).toString().split("")[7] + "" +
                                            getBinaryString(stegaImagePixel[2]).toString().split("")[7] + "" +
                                            getBinaryString(stegaImagePixel[3]).toString().split("")[7]
                                            );

                    System.out.println("-----------------------------------------");
                    System.out.println(getBinaryString(stegaImagePixel[0]).toString());
                    System.out.println(getBinaryString(stegaImagePixel[1]).toString());
                    System.out.println(getBinaryString(stegaImagePixel[2]).toString());
                    System.out.println(getBinaryString(stegaImagePixel[3]).toString());
                    System.out.println("-----------------------------------------");

                    if (y == Integer.parseInt(spec[0]) && x == Integer.parseInt(spec[1])){
                        System.out.println( "Video binaries are been extracted completely from the stega image...");
                        System.exit(0);
                    }

                if (tempString.split("").length == 8) {
                        videoExtractedIntegers.add(Integer.parseInt(tempString));
                    System.out.println(tempString);
                        tempString = "" ;// clear the string
                    }
                }
            }

            return videoExtractedIntegers ;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null ;
    }


    /**
     *
     * @param fileName
     * @return
     */
    private static String getFileExtentsion(String fileName){
        return  fileName.substring(fileName.lastIndexOf(".") + 1 );
    }


    /**
     *
     * @return
     */
    private static String getImageFile(){
        return "C:\\Users\\NeuralTechX CEO\\Pictures\\Screenshots\\testing.png";
    }

    /**
     *
     * @return
     */
    private static BufferedInputStream getVideoInputStream() throws IOException {

        return new BufferedInputStream(new FileInputStream("C:\\Users\\NeuralTechX CEO\\Documents\\RSAWithSteganography\\new-encryptedvid.enc"));

    }


    /**
     *
     * @param i
     * @param i1
     * */
    private static void createVideoEnpoint(int i , int i1){
        try (FileWriter bf = new FileWriter(new File("spec.code"))){
            bf.write(i + "," + i1 );
            bf.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}