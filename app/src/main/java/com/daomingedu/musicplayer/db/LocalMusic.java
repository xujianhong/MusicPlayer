package com.daomingedu.musicplayer.db;

import org.litepal.crud.LitePalSupport;

/**
 * Description
 * Created by jianhongxu on 2021/10/19
 */
public class LocalMusic extends LitePalSupport {
    public String title;     //歌曲名
    public String songUrl;     //歌曲地址
    public String categoryName;
    public Integer number;



    public LocalMusic(String songUrl, String title, String categoryName,Integer number) {
        this.title = title;
        this.songUrl = songUrl;
        this.categoryName = categoryName;
        this.number = number;
    }
}
