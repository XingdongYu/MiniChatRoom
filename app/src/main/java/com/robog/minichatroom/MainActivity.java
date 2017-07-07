package com.robog.minichatroom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MESSAGE_SEND_NEW_MSG = 0;

    private static final int MESSAGE_RECEIVE_NEW_MSG = 1;

    private static final int MESSAGE_SOCKET_CONNECTED = 2;

    private RecyclerView mRecyclerView;

    private ChatAdapter mChatAdapter;

    private List<ChatInfoModel> mData = new ArrayList<>();

    private Button mButtonSend;

    private EditText mEditText;

    private Socket mClientSocket;

    private PrintWriter mPrintWriter;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_RECEIVE_NEW_MSG:
                    setAdapterData((String)msg.obj, ChatInfoModel.TYPE_SERVER);
                    break;
                case MESSAGE_SOCKET_CONNECTED:
                    mButtonSend.setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mButtonSend = (Button) findViewById(R.id.bt_send);
        mEditText = (EditText) findViewById(R.id.edit_text);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mChatAdapter = new ChatAdapter();
        mRecyclerView.setAdapter(mChatAdapter);

        Intent intent = new Intent(this, ChatService.class);
        startService(intent);

        new Thread(){
            @Override
            public void run() {
                connectServer();
            }
        }.start();
    }

    private void connectServer() {
        Socket socket = null;
        while (socket == null){
            try {
                socket = new Socket("localhost", 8688);
                mClientSocket = socket;
                mPrintWriter = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
            while (!MainActivity.this.isFinishing()){
                String msg = br.readLine();
                if (msg != null){
                    mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG, msg).sendToTarget();
                }
            }
            mPrintWriter.close();
            br.close();
            mClientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View view){
        final String msg = mEditText.getText().toString();

        if (!TextUtils.isEmpty(msg) && mPrintWriter != null){
            mPrintWriter.println(msg);
            mEditText.setText("");
            setAdapterData(msg, ChatInfoModel.TYPE_CLIENT);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        }
    }

    private void setAdapterData(String msg, int type) {
        ChatInfoModel sendModel = new ChatInfoModel();
        sendModel.type = type;
        sendModel.content = msg;
        mChatAdapter.addData(sendModel);
        mRecyclerView.getLayoutManager().scrollToPosition(mChatAdapter.getData().size() - 1);
    }

    @Override
    protected void onDestroy() {
        if (mClientSocket != null){
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}
