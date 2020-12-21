package com.example.formcreator.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.formcreator.FormCardsDatabaseHelper;
import com.example.formcreator.FormObject;
import com.example.formcreator.MainActivity2;
import com.example.formcreator.R;
import com.example.formcreator.adapter.RecyclerViewAdapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    // Recycler Content
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<FormObject> formArrayList;
    private ArrayAdapter<String> arrayAdapter;
    Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        context = container.getContext();

        FormCardsDatabaseHelper db = new FormCardsDatabaseHelper(context);

        // RecyclerView Initialization
        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // RecyclerView content
        formArrayList = new ArrayList<>();

        // db.deleteAllRecords();

        List<FormObject> formList = null;
        try {
            formList = db.getAllForms(context);
        } catch (ParseException e) {
            Log.d("MainActivity2.java", "getAllForms is useless");
            e.printStackTrace();
        }

        for(FormObject formObject : formList){
            formArrayList.add(formObject);
        }

        recyclerViewAdapter = new RecyclerViewAdapter(context, formArrayList);
        recyclerView.setAdapter(recyclerViewAdapter);

        return root;
    }
}