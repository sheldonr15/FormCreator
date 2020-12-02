package com.example.sortinggallery.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sortinggallery.DisplayForm;
import com.example.sortinggallery.FormObject;
import com.example.sortinggallery.R;
import com.example.sortinggallery.formCreator;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<FormObject> formObjectList;

    public RecyclerViewAdapter(Context context, List<FormObject> formObjectList){
        this.context = context;
        this.formObjectList = formObjectList;
    }

    // Where to get the single card as ViewHolder object
    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);
    }

    // What will happen after we create the ViewHolder object
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        FormObject formObject = formObjectList.get(position);

        holder.formTitle.setText(formObject.getFormTitle());
        holder.usernameOfForm.setText(formObject.getUsername());
    }

    // How many items?
    @Override
    public int getItemCount() {
        return formObjectList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView formTitle;
        public TextView usernameOfForm;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            formTitle = itemView.findViewById(R.id.form_title);
            usernameOfForm = itemView.findViewById(R.id.username_of_form);
        }

        @Override
        public void onClick(View view) {
            int position = this.getAdapterPosition();
            FormObject formObject = formObjectList.get(position);
            String form_title = formObject.getFormTitle();
            String form_address = formObject.getFormAddress();
            String form_username = formObject.getUsername();

            Intent intent = new Intent(context, DisplayForm.class);
            intent.putExtra("fTitle", form_title);
            intent.putExtra("fAddress", form_address);
            intent.putExtra("fUsername", form_username);
            context.startActivity(intent);

            Log.d("RecyclerViewAdapter", "CLICKED");
        }
    }
}
