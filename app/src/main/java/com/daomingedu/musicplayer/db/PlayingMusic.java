package com.daomingedu.musicplayer.db;

import org.litepal.crud.LitePalSupport;

public class PlayingMusic extends LitePalSupport {


    public String title;     //歌曲名
    public String songUrl;     //歌曲地址
    public String categoryName;
    public Integer number;


    public PlayingMusic(String songUrl, String title,String categoryName,Integer number) {
        this.title = title;
        this.songUrl = songUrl;
        this.categoryName = categoryName;
        this.number = number;
    }
}
