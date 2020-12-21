package com.example.formcreator.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.formcreator.DisplayForm;
import com.example.formcreator.DisplayFormForProfile;
import com.example.formcreator.FormObject;
import com.example.formcreator.R;

import java.util.List;

public class RecyclerViewAdapterForProfile extends RecyclerView.Adapter<RecyclerViewAdapterForProfile.ViewHolder> {
    private Context context;
    private List<FormObject> formObjectList;

    public RecyclerViewAdapterForProfile(Context context, List<FormObject> formObjectList){
        this.context = context;
        this.formObjectList = formObjectList;
    }

    // Where to get the single card as ViewHolder object
    @NonNull
    @Override
    public RecyclerViewAdapterForProfile.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new RecyclerViewAdapterForProfile.ViewHolder(view);
    }

    // What will happen after we create the ViewHolder object
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterForProfile.ViewHolder holder, int position) {
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

            Intent intent = new Intent(context, DisplayFormForProfile.class);
            intent.putExtra("fTitle", form_title);
            intent.putExtra("fAddress", form_address);
            context.startActivity(intent);

            Log.d("RecyclerViewAdapter", "CLICKED");
        }
    }
}
