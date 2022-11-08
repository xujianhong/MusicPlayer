package com.daomingedu.musicplayer.bean;

import java.util.Objects;

public class Music {


    public String title;      //歌曲名
    public String songUrl;    //歌曲地址
    public String categoryName;
    public Integer number;


    public Music(String songUrl, String title,String categoryName,Integer number) {
        this.title = title;
        this.songUrl = songUrl;
        this.categoryName = categoryName;
        this.number = number;

    }

    //重写equals方法, 使得可以用contains方法来判断列表中是否存在Music类的实例
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Music music = (Music) o;
        return Objects.equals(title, music.title);
    }
}
