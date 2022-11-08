package com.daomingedu.musicplayer.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    //累计听歌数量
    public static int count;

    //播放模式
    public static final int TYPE_ORDER = 4212;  //顺序播放
    public static final int TYPE_SINGLE = 4313; //单曲循环
    public static final int TYPE_RANDOM = 4414; //随机播放

    // 获取本地音乐封面图片
    public static Bitmap getLocalMusicBmp(ContentResolver res, String musicPic) {
        InputStream in;
        Bitmap bmp = null;
        try {
            Uri uri = Uri.parse(musicPic);
            in = res.openInputStream(uri);
            BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
            bmp = BitmapFactory.decodeStream(in, null, sBitmapOptions);
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    //格式化歌曲时间
    public static String formatTime(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        Date data = new Date(time);
        return dateFormat.format(data);
    }

    /**
     * 判断文件是否存在
     * @param httpPath
     * @return
     */
    public static Boolean existHttpPath(String httpPath){
        URL httpurl = null;
        try {
            httpurl = new URL(new URI(httpPath).toASCIIString());
            URLConnection urlConnection = httpurl.openConnection();
            // urlConnection.getInputStream();
            Long TotalSize=Long.parseLong(urlConnection.getHeaderField("Content-Length"));
            if (TotalSize <= 0){
                return false;
            }
            return true;
        } catch (Exception e) {
            Log.e("existHttpPath",httpurl + "文件不存在");
            return false;
        }
    }
}