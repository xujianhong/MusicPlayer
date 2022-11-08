package com.daomingedu.musicplayer.db;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * Description
 * Created by jianhongxu on 2021/10/19
 */
public class CategoryBean extends LitePalSupport {


    public String categoryName;

    public CategoryBean(String categoryName) {
        this.categoryName = categoryName;
    }
}
