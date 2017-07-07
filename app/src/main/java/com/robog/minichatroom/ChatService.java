package com.robog.minichatroom;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Created by yxd on 2017/7/7.
 */

public class ChatService extends Service {

    private boolean mIsServiceDestroyed = false;

    private int mMontageIndex = 0;

    private String[] mDefinedMessages = new String[]{
            "我觉得你很帅诶~",
            "我喜欢你~",
            "晚上一起去吃饭吧(^o^)/~",
            "O(∩_∩)O",
            "你好无聊",
            "你烦不烦啊",
            "你走开啊，别烦我",
            "我刚刚看了部电影笑死了，电影的名字是...不告诉你，哈哈~",
            "我不听我不听",
            "分手",
            "我不想跟你说话",
            "再见"
    };

    @Override
    public void onCreate() {
        new Thread(new ChatServer()).start();
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ChatServer implements Runnable{

        @Override
        public void run() {
            ServerSocket serverSocket;
            try {
                serverSocket = new ServerSocket(8688);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            while (!mIsServiceDestroyed){
                try {
                    final Socket client = serverSocket.accept();
                    new Thread(){
                        @Override
                        public void run() {
                            responseClient(client);
                            super.run();
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void responseClient(Socket socket){
        try {
            //接收消息
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //发送消息
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.println("在吗？");
            while (!mIsServiceDestroyed){
                String str = in.readLine();
                if (str == null){
                    //断开
                    break;
                }
                String msg;
                if (mMontageIndex > 3){
                    int i = new Random().nextInt(mDefinedMessages.length);
                    msg = mDefinedMessages[i];
                }else {
                    msg = mDefinedMessages[mMontageIndex];
                    mMontageIndex ++;
                }

                out.println(msg);
            }
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed = true;
        super.onDestroy();
    }
}
