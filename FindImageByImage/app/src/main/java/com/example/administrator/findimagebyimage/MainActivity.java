package com.example.administrator.findimagebyimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private Button find;
    private ImageView img1,img2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        find = findViewById(R.id.btn);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img1.setDrawingCacheEnabled(true);
        img2.setDrawingCacheEnabled(true);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Bitmap yuantu = BitmapFactory.decodeResource(getResources(),R.mipmap.pic1);
                Bitmap mubiao = BitmapFactory.decodeResource(getResources(),R.mipmap.pic2);*/
                Bitmap yuantu = img1.getDrawingCache();
                Bitmap mubiao = img2.getDrawingCache();
                Log.e("相似度",similarity(yuantu,mubiao));
                try {
                    boolean boo = FindImg(mubiao,yuantu);
                    if(boo){
                        Toast.makeText(MainActivity.this,"存在",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this,"不存在",Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    private  boolean FindImg(Bitmap mubiao, Bitmap yuantu) throws IOException {
        boolean isFind = false;
        String mubiaoHashCode;
        String yuantuHashCode;
        Bitmap jiequsource;
        int width = yuantu.getWidth();
        int height = yuantu.getHeight();
        int Mwidth = mubiao.getWidth();
        int Mheight = mubiao.getHeight();
        Log.e("dimen",width+"s"+height+"s"+Mwidth+"s"+Mheight);
        mubiaoHashCode = BufproduceFingerPrint(mubiao);
        //通过循环来查找图片（就是从左上到右下）
        for(int i=0;i<width-Mwidth;i++){
            for(int j= 0;j<height-Mheight;j++){
                jiequsource = Bitmap.createBitmap(yuantu, i, j, Mwidth, Mheight,
                        null,  false);
                yuantuHashCode = BufproduceFingerPrint(jiequsource);
                int difference = hammingDistance(mubiaoHashCode, yuantuHashCode);
                if(difference == 0 ){
                    isFind = true;
                    img1.setImageBitmap(jiequsource);
                    Log.e("findRst","yes");
                    break;
                }  else{
                    Log.i("findRst","no"+difference);
                }
            }
        }

        if(isFind){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 处理图片
     * @param source
     * @return
     */
    public static String BufproduceFingerPrint(Bitmap source) {
//	    BufferedImage source = ImageHelper.readPNGImage(filename);// 读取文件
        int width = 8;
        int height = 8;
        int pixelColor;
        // 第一步，缩小尺寸。
        // 将图片缩小到8x8的尺寸，总共64个像素。这一步的作用是去除图片的细节，只保留结构、明暗等基本信息，摒弃不同尺寸、比例带来的图片差异。
        Bitmap thumb = ImageHelper.zoomImage(source, width,height);

        // 第二步，简化色彩。
        // 将缩小后的图片，转为64级灰度。也就是说，所有像素点总共只有64种颜色。
        int[] pixels = new int[width * height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
//	    	  System.out.println("i=" + i +";y=" + j);
//	    	  pixelColor = thumb.getPixel(i, j);
//	    	  R = Color.red(pixelColor);
//              G = Color.green(pixelColor);
//              B = Color.blue(pixelColor);
                pixels[i * height + j] = ImageHelper.rgbToGray(thumb.getPixel(i, j));
            }
        }

        // 第三步，计算平均值。
        // 计算所有64个像素的灰度平均值。
        int avgPixel = ImageHelper.average(pixels);

        // 第四步，比较像素的灰度。
        // 将每个像素的灰度，与平均值进行比较。大于或等于平均值，记为1；小于平均值，记为0。
        int[] comps = new int[width * height];
        for (int i = 0; i < comps.length; i++) {
            if (pixels[i] >= avgPixel) {
                comps[i] = 1;
            } else {
                comps[i] = 0;
            }
        }

        // 第五步，计算哈希值。
        // 将上一步的比较结果，组合在一起，就构成了一个64位的整数，这就是这张图片的指纹。组合的次序并不重要，只要保证所有图片都采用同样次序就行了。
        StringBuffer hashCode = new StringBuffer();
        for (int i = 0; i < comps.length; i += 4) {
            int result = comps[i] * (int) Math.pow(2, 3) + comps[i + 1]
                    * (int) Math.pow(2, 2) + comps[i + 2] * (int) Math.pow(2, 1)
                    + comps[i + 2];
            hashCode.append(binaryToHex(result));
        }

        // 得到指纹以后，就可以对比不同的图片，看看64位中有多少位是不一样的。
        return hashCode.toString();
    }
    private static char binaryToHex(int binary) {
        char ch = ' ';
        switch (binary) {
            case 0:
                ch = '0';
                break;
            case 1:
                ch = '1';
                break;
            case 2:
                ch = '2';
                break;
            case 3:
                ch = '3';
                break;
            case 4:
                ch = '4';
                break;
            case 5:
                ch = '5';
                break;
            case 6:
                ch = '6';
                break;
            case 7:
                ch = '7';
                break;
            case 8:
                ch = '8';
                break;
            case 9:
                ch = '9';
                break;
            case 10:
                ch = 'a';
                break;
            case 11:
                ch = 'b';
                break;
            case 12:
                ch = 'c';
                break;
            case 13:
                ch = 'd';
                break;
            case 14:
                ch = 'e';
                break;
            case 15:
                ch = 'f';
                break;
            default:
                ch = ' ';
        }
        return ch;
    }

    /**
     * 2个是否相同，0为相同
     * @param sourceHashCode
     * @param hashCode
     * @return
     */
    public static int hammingDistance(String sourceHashCode, String hashCode) {
        int difference = 0;
        int len = sourceHashCode.length();

        for (int i = 0; i < len; i++) {
            if (sourceHashCode.charAt(i) != hashCode.charAt(i)) {
                difference++;
            }
        }

        return difference;
    }

    public static String similarity (Bitmap b,Bitmap viewBt) {
        //把图片转换为Bitmap

        int f = 0,t = 0;
        Bitmap bm_one = b;
        Bitmap bm_two = viewBt;
        //保存图片所有像素个数的数组，图片宽×高
        int[] pixels_one = new int[bm_one.getWidth()*bm_one.getHeight()];
        int[] pixels_two = new int[bm_two.getWidth()*bm_two.getHeight()];
        //获取每个像素的RGB值
        bm_one.getPixels(pixels_one,0,bm_one.getWidth(),0,0,bm_one.getWidth(),bm_one.getHeight());
        bm_two.getPixels(pixels_two,0,bm_two.getWidth(),0,0,bm_two.getWidth(),bm_two.getHeight());
        //如果图片一个像素大于图片2的像素，就用像素少的作为循环条件。避免报错
        if (pixels_one. length >= pixels_two. length) {
            //对每一个像素的RGB值进行比较
            for( int i = 0; i < pixels_two. length; i++){
                int clr_one = pixels_one[i];
                int clr_two = pixels_two[i];
                //RGB值一样就加一（以便算百分比）
                if (clr_one == clr_two) {
                    t++;
                } else {
                    f++;
                }
            }
        } else {
            for( int i = 0; i < pixels_one. length; i++){
                int clr_one = pixels_one[i];
                int clr_two = pixels_two[i];
                if (clr_one == clr_two) {
                    t++;
                } else {
                    f++;
                }
            }

        }

        return "相似度为：" +myPercent ( t, t+ f );

    }
    /**
     * 百分比的计算
     * @author xupp
     * @param y(母子)
     * @param z（分子）
     * @return 百分比（保留小数点后两位）
     */
    public static String myPercent (int y, int z) {
        String baifenbi = ""; //接受百分比的值
        double baiy = y * 1.0;
        double baiz = z * 1.0;
        double fen = baiy / baiz;
        DecimalFormat df1 = new DecimalFormat("00.00%"); //##.00%   百分比格式，后面不足2位的用0补齐
        baifenbi = df1.format(fen);
        return baifenbi;
    }

    }
