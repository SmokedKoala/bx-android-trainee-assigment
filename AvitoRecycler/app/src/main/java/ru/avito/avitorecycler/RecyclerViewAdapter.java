package ru.avito.avitorecycler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static android.content.ContentValues.TAG;
import static java.lang.Integer.parseInt;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<CardFiller> mData;
    private List<CardFiller> removedData;
    private LayoutInflater mInflater;
    private int counter;

    // конструктор
    RecyclerViewAdapter(Context context, List<CardFiller> data, List<CardFiller> removedData) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.counter = 0;
        if (removedData.size()==0){
            this.removedData = new ArrayList<>();
        } else {
            this.removedData = removedData;
        }
        for (int i = 0; i< mData.size();i++){
            if (parseInt(mData.get(i).getId())>counter){
                counter = parseInt(mData.get(i).getId())+1;
            }
        }
    }

    public List<CardFiller> getmData() {
        return mData;
    }

    // поднимает изображение карточки из xml
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.card, parent, false);
        return new ViewHolder(view);

    }

    // привязка данных к карточкам по позициям
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.myTextView.setText(mData.get(position).getId());
        holder.itemView.findViewById(R.id.button_to_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"removed item is "+ getItem(holder.getAdapterPosition()));
                removedData.add(new CardFiller(getItem(holder.getAdapterPosition())));
                mData.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });

    }


    // кол-во карточек
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView myTextView;

        ViewHolder(final View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.card_num);

        }
    }

    // получение данных по клику на карточку
    String getItem(int id) {
        return mData.get(id).getId();
    }

    List<CardFiller> getRemovedData(){return this.removedData;}

    //добавление нового cardview с новым индексом или индексом удалённого ранее
    int addCard(){
        int index=(int)(Math.random()*(mData.size()));
        if (removedData.size()==0) {
            Log.i(TAG, index + " position for " + counter);
            mData.add(index, new CardFiller(String.valueOf(counter)));
            counter++;
        } else {
            mData.add(index,removedData.get(0));
            if (parseInt(removedData.get(0).getId())>counter) {
                counter = parseInt(mData.get(0).getId())+1;
            }
            removedData.remove(0);
        }
        return index;
    }
}

