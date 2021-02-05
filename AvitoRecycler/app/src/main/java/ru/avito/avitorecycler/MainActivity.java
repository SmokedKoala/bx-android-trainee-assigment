package ru.avito.avitorecycler;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    String[] data;
    List<CardFiller> cardFillers;
    List<CardFiller> removedCardFillers;
    private Timer timer = new Timer();
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardFillers = new ArrayList<>();
        removedCardFillers = new ArrayList<>();
//если приложение уже было запущено до разворота экрана, восстановить данные
        if (savedInstanceState==null){
            data = new String[15];
            initialisationData();
        } else {
            System.out.print("\nremovedCardFillers:");
            for (int i =0; i< savedInstanceState.getStringArrayList("savedRemovedCardFillers").size();i++) {
                removedCardFillers.add(new CardFiller(savedInstanceState.getStringArrayList("savedRemovedCardFillers").get(i)));
                System.out.print(" "+savedInstanceState.getStringArrayList("savedRemovedCardFillers").get(i));}
            System.out.print("\nsavedCardFillers:");
            for (int i =0; i< savedInstanceState.getStringArrayList("savedCardFillers").size();i++) {
                cardFillers.add(new CardFiller(savedInstanceState.getStringArrayList("savedCardFillers").get(i)));
                System.out.print(" "+savedInstanceState.getStringArrayList("savedCardFillers").get(i));}
        }
        recyclerView = findViewById(R.id.recyclerView);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); //создание списка с сеткой из 2 колонок
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4)); //создание списка с сеткой из 4 колонок
        recyclerViewAdapter = new RecyclerViewAdapter(this, cardFillers, removedCardFillers);
        recyclerView.setAdapter(recyclerViewAdapter);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);

//        второй поток, добавляющий элемент раз в 5 секунд
        timerTask = new TimerTask() {
            @Override
            public void run() {
                final int index = recyclerViewAdapter.addCard();
//                обновление view в главном потоке
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerViewAdapter.notifyItemInserted(index);
                    }
                });
            }
        };
        timer.schedule(timerTask,5000,5000);

    }
//сохранение данных перед поворотом экрана
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        сохранение текущих карточек перед заканчиванием активности
        timerTask.cancel();
        ArrayList<String> savedCardFillers = new ArrayList<>() ;
        for (int i =0; i< cardFillers.size();i++){
            savedCardFillers.add(cardFillers.get(i).getId());
        }
//        сохранение удалённых карточек перед заканчиванием активности
        ArrayList<String> savedRemovedCardFillers = new ArrayList<>() ;
        for (int i =0; i< recyclerViewAdapter.getRemovedData().size();i++){
            savedRemovedCardFillers.add(recyclerViewAdapter.getRemovedData().get(i).getId());
        }
        outState.putStringArrayList("savedCardFillers", savedCardFillers);
        outState.putStringArrayList("savedRemovedCardFillers", savedRemovedCardFillers);
        super.onSaveInstanceState(outState);
    }


    public void initialisationData() {
        for (int i = 0; i < data.length; i++) {
            cardFillers.add(new CardFiller(String.valueOf(i+1)));
        }
    }
}



