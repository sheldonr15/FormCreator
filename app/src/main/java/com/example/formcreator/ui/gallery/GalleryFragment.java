package com.example.sortinggallery.ui.gallery;

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

import com.example.sortinggallery.FormCardsDatabaseHelper;
import com.example.sortinggallery.FormObject;
import com.example.sortinggallery.R;
import com.example.sortinggallery.adapter.RecyclerViewAdapter;
import com.example.sortinggallery.adapter.RecyclerViewAdapterForProfile;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    // Recycler Content
    private RecyclerView recyclerView;
    private RecyclerViewAdapterForProfile recyclerViewAdapterForProfile;
    private ArrayList<FormObject> formArrayList;
    private ArrayAdapter<String> arrayAdapter;
    Context context;

    private GalleryViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        context = container.getContext();

        FormCardsDatabaseHelper db = new FormCardsDatabaseHelper(context);

        // RecyclerView Initialization
        recyclerView = root.findViewById(R.id.recycler_view_profile);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // RecyclerView content
        formArrayList = new ArrayList<>();

        // db.deleteAllRecords();

        List<FormObject> formList = null;
        try {
            formList = db.getAllFormsForProfile(context);
        } catch (ParseException e) {
            Log.d("MainActivity2.java", "getAllForms is useless");
            e.printStackTrace();
        }

        for(FormObject formObject : formList){
            formArrayList.add(formObject);
        }

        recyclerViewAdapterForProfile = new RecyclerViewAdapterForProfile(context, formArrayList);
        recyclerView.setAdapter(recyclerViewAdapterForProfile);

        return root;
    }
}