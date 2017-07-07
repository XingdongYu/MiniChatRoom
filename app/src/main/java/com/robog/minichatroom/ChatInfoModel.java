package com.robog.minichatroom;

import android.widget.ImageView;

/**
 * Created by yxd on 2017/7/7.
 */

public class ChatInfoModel {

    public static final int TYPE_SERVER = 0;

    public static final int TYPE_CLIENT = 1;

    public int type;

    public String content;

    public String name;

    public ImageView avatar;

}
