package com.bitfly.image.tip;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.time.LocalDate;
import java.util.ArrayList;

public class TipImage {
    private static final String BASE_FOLDER = "C:\\Users\\shaun\\Pictures\\%s";
    private static final String NOTE_FILE = "C:\\Users\\shaun\\Pictures\\note.txt";
    private static final String INITIAL_IMAGE = "img2.jpg";
    private static final String INITIAL_WILL = "willOf.jpg";
    private static final String WILL_IMAGE = "will.jpg";
    private static final String FINAL_IMAGE = "finalDesktopImage.jpg";
    private static final String READED = "true";
    private static final int INITIAL_TOP = 40;
    private static String imageFilePath(String file){
        return String.format(BASE_FOLDER,file);
    }

    private void initMaskImage() throws IOException {
        String finalImage = imageFilePath(INITIAL_WILL);
        if(new File(finalImage).exists()) return;
        try(InputStream is = new FileInputStream(imageFilePath(INITIAL_IMAGE));
            InputStream mis = new FileInputStream(imageFilePath(WILL_IMAGE));
            OutputStream os = new FileOutputStream(finalImage)){
            BufferedImage bi=ImageIO.read(is);
            BufferedImage mbi=ImageIO.read(mis);
            Graphics2D g2 = (Graphics2D)bi.getGraphics();
            g2.drawImage(mbi,null,bi.getWidth() - mbi.getWidth(),INITIAL_TOP);
            ImageIO.write(bi, "JPEG",os );
        }
    }

    private void doNoteImage(String[] words)  throws IOException {
        if(words == null) return;
        try(InputStream is = new FileInputStream(imageFilePath(INITIAL_WILL));
            OutputStream os = new FileOutputStream(imageFilePath(FINAL_IMAGE))){
            BufferedImage bi=ImageIO.read(is);
            Graphics2D g2 = (Graphics2D)bi.getGraphics();
            noteWordImage(bi, words);
            ImageIO.write(bi, "JPEG",os );
        }
    }

    private void noteWordImage(BufferedImage bi,String[] words){
        String date = words[0].replace("-","月").replaceAll("$","日");
        Graphics2D g2 = (Graphics2D)bi.getGraphics();
        g2.setFont(new Font("黑体",Font.BOLD,64));//
        g2.setColor(Color.WHITE);//
        g2.drawString(date, bi.getWidth()/2 - 300, bi.getHeight()/2 + 100 + 40);
        g2.drawString(words[1], bi.getWidth()/2 - 300 , bi.getHeight()/2 + 100 + 40 + 80);
        g2.setFont(new Font("黑体",Font.BOLD ,64));//
        g2.setColor(Color.LIGHT_GRAY);//
        g2.drawString(date, bi.getWidth()/2 - 300 + 5, bi.getHeight() / 2 + 100 + 40);
        g2.drawString(words[1], bi.getWidth()/2 - 300 + 5, bi.getHeight() / 2 + 100 + 40 + 80);
    }

    private void noteImage(String file) throws IOException {
        try {
            this.initMaskImage();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        this.noteCallRandom(NOTE_FILE);

        EditDesktop.change(imageFilePath(FINAL_IMAGE));
    }

    private void noteCallRandom(String  file) throws IOException {
        int capacity = 100;
        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        ByteBuffer readed = ByteBuffer.wrap((READED).getBytes());
        long position = 0;
        java.util.List<Byte> lb = new ArrayList<>();

        try(FileChannel channel = new RandomAccessFile(file,"rw").getChannel()){
            String[] words = new String[3];
            while(channel.read(buffer,position) != -1){
                int start = 0;
                int arrpos = 0;
                byte c;
                ((Buffer)buffer).flip();
                while(buffer.hasRemaining()){
                    switch (c = buffer.get()){
                        case 13:
                            start++;
                            break;
                        case 10:
                            start++;
                            break;
                        case 44:
                            words[arrpos] = new String(convert(lb),"UTF-8");
                            lb = new ArrayList<>();
                            arrpos++;
                            break;
                        default:
                            lb.add(c);
                    }
                    position++;
                    if(start == 2){
                        break;
                    }
                }

                if(lb.size() > 0){
                    words[arrpos] = new String(convert(lb),"UTF-8");
                }

                if(isNowWord(words)){
                    this.doNoteImage(words);
                    channel.write(readed,position - READED.length() - start);
                    return;
                }
                buffer.clear();
                lb = new ArrayList<>();
            }
        }
    }

    private byte[] convert(java.util.List<Byte> lb){
        byte[] bs = new byte[lb.size()];
        for(int i=0,l=lb.size();i<l;i++){
            bs[i] = lb.get(i).byteValue();
        }
        return bs;
    }

    private boolean isNowWord(String[] words){
        if(words.length < 2 || words.length == 3 && READED.equals(words[2])) return false;
        LocalDate now = LocalDate.now();
        String[] dates = words[0].split("-");
        LocalDate recordDate = LocalDate.of(now.getYear(),Integer.parseInt(dates[0]),Integer.parseInt(dates[1]));
        return !now.isAfter(recordDate);
    }

    public static void testByteCode(String file) throws IOException {
        try( FileInputStream s = new FileInputStream(file);
             BufferedReader br = new BufferedReader(new InputStreamReader(s,"UTF-8"))){
            String line;
            while((line = br.readLine()) != null){
                System.out.println(line);
            }
        }

    }

    public static void createFile(String file) throws IOException {
        try( FileOutputStream s = new FileOutputStream(file);
             BufferedWriter br = new BufferedWriter(new OutputStreamWriter(s,"UTF-8"))){
            br.write("3-19,缴纳社保,    ");br.newLine();
            br.write("3-20,123和,    ");br.newLine();
        }
    }

    public static void main(String[] args) {
        try {
            new TipImage().createFile(NOTE_FILE);
            //new TipImage().testByteCode(NOTE_FILE);
            //new TipImage().noteImage(NOTE_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
