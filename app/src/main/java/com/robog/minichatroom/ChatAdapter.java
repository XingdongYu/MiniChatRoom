package com.robog.minichatroom;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yxd on 2017/7/7.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatInfoModel> mData = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case ChatInfoModel.TYPE_CLIENT:
                return new ClientViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_info_client, parent, false));
            case ChatInfoModel.TYPE_SERVER:
                return new ServerViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_info_server, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ClientViewHolder){
            ((ClientViewHolder)holder).tvInfoClient.setText(mData.get(position).content);
        }
        else if (holder instanceof ServerViewHolder){
            ((ServerViewHolder)holder).tvInfoServer.setText(mData.get(position).content);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ClientViewHolder extends RecyclerView.ViewHolder{

        private TextView tvInfoClient;

        public ClientViewHolder(View itemView) {
            super(itemView);
            tvInfoClient = itemView.findViewById(R.id.tv_info_client);
        }
    }

    class ServerViewHolder extends RecyclerView.ViewHolder{

        private TextView tvInfoServer;

        public ServerViewHolder(View itemView) {
            super(itemView);
            tvInfoServer = itemView.findViewById(R.id.tv_info_server);
        }
    }

    public void addData(ChatInfoModel chatInfoModel){
        mData.add(chatInfoModel);
        notifyDataSetChanged();
    }

    public List<ChatInfoModel> getData(){
        return mData;
    }
}
