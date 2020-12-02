package com.example.sortinggallery;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

public class DisplayForm extends AppCompatActivity {

    String jsonObj = "";

    String formDescription;

    LinearLayout addCardsHere;

    TextView formTitleView;
    TextView formDescriptionView;

    JSONObject formStructure;

    Button submitButton;

    JSONObject jsonObject;
    int count = 0;

    int id = 1270;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_form);

        addCardsHere = findViewById(R.id.display_form_linearlayout);

        Intent intent = getIntent();
        String formTitle = intent.getStringExtra("fTitle");
        String formAddress = intent.getStringExtra("fAddress");
        String username = intent.getStringExtra("fUsername");

        formTitleView = findViewById(R.id.display_form_title_textview);
        formDescriptionView = findViewById(R.id.display_form_desc_textview);
        submitButton = findViewById(R.id.display_form_submit);

        // Set Form title
        formTitleView.setText(formTitle);

        // Get .json file and convert to JSONObject
        String fullUrl = getExternalFilesDir(null) + "/" + formAddress;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fullUrl);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        DataInputStream dis = new DataInputStream(fis);
        BufferedReader br = new BufferedReader(new InputStreamReader(dis));

        String strLine = null;

        while(true){
            try {
                if (!((strLine = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            jsonObj = jsonObj + strLine;
        }


        // JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Set Form description
        try {
            formDescription = jsonObject.getString("FormDescription");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        formDescriptionView = findViewById(R.id.display_form_desc_textview);
        formDescriptionView.setText(formDescription);

        int numberOfCardViews = jsonObject.length();

        // Get Form Structure
        try {
            formStructure = jsonObject.getJSONObject("formStruct");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(formStructure.length()!=0){
            // There are cards to be added
            for(int i=1; i<=formStructure.length(); i++){

                // Get JSON object of this card
                JSONObject thisCardJsonObject = null;
                try {
                    thisCardJsonObject = formStructure.getJSONObject(String.valueOf(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    if(thisCardJsonObject.getString("FieldType").equals("bullet")){
                        String radioButtonTitle = thisCardJsonObject.getString("Title");
                        JSONObject radioButtonOptions = thisCardJsonObject.getJSONObject("options");
                        addBulletCard(radioButtonTitle, radioButtonOptions);
                    }
                    else if(thisCardJsonObject.getString("FieldType").equals("checkbox")){
                        String checkboxTitle = thisCardJsonObject.getString("Title");
                        JSONObject checkboxOptions = thisCardJsonObject.getJSONObject("options");
                        addCheckboxCard(checkboxTitle, checkboxOptions);
                    }
                    else if(thisCardJsonObject.getString("FieldType").equals("textbox")){
                        String textboxTitle = thisCardJsonObject.getString("Title");
                        addTextboxCard(textboxTitle);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        // Handling Submit Button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DisplayForm.java", "JSON Object before : " + jsonObject.toString());
                try {
                    show_children(getWindow().getDecorView());
                } catch (JSONException e) {
                    Log.d("DisplayForm.java", "Error in show_children() : " + e.getMessage());
                }
                Log.d("DisplayForm.java", "JSON Object after : " + jsonObject.toString());


                Log.d("DisplayForm.java", "Username : " + username + " >> Form Address : " + formAddress);
                File file = new File(getExternalFilesDir(null), formAddress);


                if(file.canWrite()){
                    Log.d("DisplayForm.java", "CAN WRITE!");
                }
                else{
                    Log.d("DisplayForm.java", "CANNOT WRITE!");
                }

                // File file = new File(getExternalFilesDir(null), "TP/Sheldon30112020174555.json");
                FileOutputStream outputStream = null;
                Log.d("DisplayForm.java", "JSON Create Folder File Debug : Folder exists");


                try {
                    // file.createNewFile();
                    outputStream = new FileOutputStream(file, false);

                    outputStream.write(jsonObject.toString().getBytes());
                    outputStream.flush();
                    outputStream.close();

                    Log.d("DisplayForm.java", "JSON Create Folder File Debug : File created! ");
                } catch (Exception e) {
                    Log.d("DisplayForm.java", "JSON Create Folder File Debug : File error " + e.getMessage());
                }

                // ----------------------------------------------
                // Open json file again to check if it got updated
                // ----------------------------------------------
                String fullUrl = getExternalFilesDir(null) + "/" + formAddress;
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(fullUrl);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                DataInputStream dis = new DataInputStream(fis);
                BufferedReader br = new BufferedReader(new InputStreamReader(dis));

                String strLine = null;

                while(true){
                    try {
                        if (!((strLine = br.readLine()) != null)) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    jsonObj = jsonObj + strLine;
                }

                try {
                    jsonObject = new JSONObject(jsonObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("DisplayForm.java", "Check JSON File after updation : " + jsonObject.toString());


                onBackPressed();

            }
        });


//        // Enabling Back button to return to Parent Activity i.e. .MainActivity2
//        ActionBar ab = getSupportActionBar();
//        ab.setDisplayHomeAsUpEnabled(true);

    }

    private void show_children(View v) throws JSONException {
        Log.d("DisplayForm.java", "Inside show_children()");

        ViewGroup viewgroup=(ViewGroup)v;

        for (int i=0; i<viewgroup.getChildCount(); i++) {

            View v1=viewgroup.getChildAt(i);

            // Log.d("DisplayForm.java", "View id : " + v1.getId());
            // Log.d("DisplayForm.java", "View tag : " + v1.getTag());

            if(v1.getTag()!=null){
                String tag = v1.getTag().toString();
                if(tag.equals("radio_group_tag")){
                    Log.d("DisplayForm.java", "View tag : " + v1.getTag());
                    count += 1;
                    String radioButtonSelected = null;
                    /*
                    if(((RadioGroup) v1).isChecked()){
                        int radioButtonId = ((RadioGroup)v1).getCheckedRadioButtonId();
                        RadioButton radioButton = (RadioButton)((RadioGroup)v1).findViewById(radioButtonId);
                        radioButtonSelected = (String)radioButton.getText();
                    }
                    */
                    int radioButtonId = ((RadioGroup)v1).getCheckedRadioButtonId();
                    if(radioButtonId!=-1){
                        Log.d("DisplayForm.java", "radio button id : " + radioButtonId);
                        RadioButton radioButton = (RadioButton)((RadioGroup)v1).findViewById(radioButtonId);
                        radioButtonSelected = (String)radioButton.getText();

                        int prevValueOfOption = jsonObject.getJSONObject("formStruct").getJSONObject(""+count).getJSONObject("options").getInt(radioButtonSelected);
                        jsonObject.getJSONObject("formStruct").getJSONObject(""+count).getJSONObject("options").put(radioButtonSelected, prevValueOfOption+1);
                    }


                }
                else if(tag.equals("checkbox_linearlayout_tag")){
                    Log.d("DisplayForm.java", "View tag : " + v1.getTag());
                    count += 1;
                    int linearlayoutChildCount = ((ViewGroup)v1).getChildCount();
                    ((ViewGroup)v1).getChildAt(0);
                    for(int k = 1; k<linearlayoutChildCount; k++){
                        CheckBox checkbox = (CheckBox) ((ViewGroup)v1).getChildAt(k);
                        if(checkbox.isChecked()){
                            int prevValueOfOption = jsonObject.getJSONObject("formStruct").getJSONObject(""+count).getJSONObject("options").getInt((String)checkbox.getText());
                            jsonObject.getJSONObject("formStruct").getJSONObject(""+count).getJSONObject("options").put((String)checkbox.getText(), prevValueOfOption+1);
                        }
                    }
                }
                else if(tag.equals("textbox_input_tag")){
                    count += 1;
                    Log.d("DisplayForm.java", "View tag : " + v1.getTag());
                    String edittextInputText = ((EditText)v1).getText().toString();
                    Log.d("DisplayForm.java", "Text input : " + edittextInputText);
                    jsonObject.getJSONObject("formStruct").getJSONObject(""+count).getJSONArray("submissions").put(edittextInputText);
                }
            }


            if (v1 instanceof ViewGroup) {
                // Log.d()
                show_children(v1);
            }

        }

        // Log.d("formCreate", "JSON Object :  " + obj.toString());

        Log.d("formCreate", "------------------------------------------------------------");
    }

    public void addBulletCard(String radioButtonTitle, JSONObject radioButtonOptions){
        CardView cardView = new CardView(this);
        // Setting android:layout_width and android:layout_height for cardView programmatically.
        CardView.LayoutParams layoutParamsCardview = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
        cardView.setLayoutParams(layoutParamsCardview);
        // Setting android:layout_marginLeft, android:layout_marginRight and android:layout_marginBottom for cardView programmatically.
        ViewGroup.MarginLayoutParams layoutParamsCardview1 = (ViewGroup.MarginLayoutParams)cardView.getLayoutParams();
        layoutParamsCardview1.setMargins(10, 0, 10, 15);
        cardView.requestLayout();
        cardView.setTag("radio_button_cardview_tag");

        LinearLayout linearLayout = new LinearLayout(this);
        // Setting android:layout_width and android:layout_height for linearLayout programmatically.
        LinearLayout.LayoutParams layoutParamsLinearlayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParamsLinearlayout);
        // Setting android:orientation for linearLayout programmatically.
        linearLayout.setOrientation(LinearLayout.VERTICAL);


        RadioGroup radioGroup = new RadioGroup(this);
        // Setting android:layout_width and android:layout_height for radioGroup programmatically.
        RadioGroup.LayoutParams layoutParamsRadiogroup = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
        radioGroup.setLayoutParams(layoutParamsRadiogroup);
        radioGroup.setTag("radio_group_tag");


        TextView textView = new TextView(this);
        // Setting android:layout_width and android:layout_height for textView programmatically.
        LinearLayout.LayoutParams layoutParamsTextview = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParamsTextview);
        // Setting Radio Group title
        textView.setText(radioButtonTitle);
        textView.setTag("radio_group_title_tag");

        linearLayout.addView(textView);

        Iterator<String> optionNames = radioButtonOptions.keys();

        while(optionNames.hasNext()){
            id = id+1;
            String option = optionNames.next();
            RadioButton radioButton = new RadioButton(this);
            LinearLayout.LayoutParams layoutParamsRadiobutton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            radioButton.setLayoutParams(layoutParamsRadiobutton);
            radioButton.setText(option);
            radioButton.setId(id);
            Log.d("DisplayForm.java", "Id of : " + id + " set for : " + option);
            radioGroup.addView(radioButton);
        }

        linearLayout.addView(radioGroup);
        cardView.addView(linearLayout);

        addCardsHere.addView(cardView);

    }

    public void addCheckboxCard(String checkboxTitle, JSONObject checkboxOptions){
        CardView cardView = new CardView(this);
        // Setting android:layout_width and android:layout_height for cardView programmatically.
        CardView.LayoutParams layoutParamsCardview = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
        cardView.setLayoutParams(layoutParamsCardview);
        // Setting android:layout_marginLeft, android:layout_marginRight and android:layout_marginBottom for cardView programmatically.
        ViewGroup.MarginLayoutParams layoutParamsCardview1 = (ViewGroup.MarginLayoutParams)cardView.getLayoutParams();
        layoutParamsCardview1.setMargins(10, 0, 10, 15);
        cardView.requestLayout();
        cardView.setTag("checkbox_cardview_tag");


        LinearLayout linearLayout = new LinearLayout(this);
        // Setting android:layout_width and android:layout_height for linearLayout programmatically.
        LinearLayout.LayoutParams layoutParamsLinearlayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParamsLinearlayout);
        // Setting android:orientation for linearLayout programmatically.
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setTag("checkbox_linearlayout_tag");


        TextView textView = new TextView(this);
        // Setting android:layout_width and android:layout_height for textView programmatically.
        LinearLayout.LayoutParams layoutParamsTextview = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParamsTextview);
        // Setting Checkbox title
        textView.setText(checkboxTitle);
        textView.setTag("checkbox_title_tag");

        linearLayout.addView(textView);

        Iterator<String> optionNames = checkboxOptions.keys();

        while(optionNames.hasNext()){
            String option = optionNames.next();
            CheckBox checkBox = new CheckBox(this);
            LinearLayout.LayoutParams layoutParamsCheckbox = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            checkBox.setLayoutParams(layoutParamsCheckbox);
            checkBox.setText(option);
            checkBox.setTag("checkbox_option_tag");
            linearLayout.addView(checkBox);
        }

        cardView.addView(linearLayout);

        addCardsHere.addView(cardView);

    }

    public void addTextboxCard(String textboxTitle){
        CardView cardView = new CardView(this);
        // Setting android:layout_width and android:layout_height for cardView programmatically.
        CardView.LayoutParams layoutParamsCardview = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
        cardView.setLayoutParams(layoutParamsCardview);
        // Setting android:layout_marginLeft, android:layout_marginRight and android:layout_marginBottom for cardView programmatically.
        ViewGroup.MarginLayoutParams layoutParamsCardview1 = (ViewGroup.MarginLayoutParams)cardView.getLayoutParams();
        layoutParamsCardview1.setMargins(10, 0, 10, 15);
        cardView.requestLayout();
        cardView.setTag("textbox_cardview_tag");

        LinearLayout linearLayout = new LinearLayout(this);
        // Setting android:layout_width and android:layout_height for linearLayout programmatically.
        LinearLayout.LayoutParams layoutParamsLinearlayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParamsLinearlayout);
        // Setting android:orientation for linearLayout programmatically.
        linearLayout.setOrientation(LinearLayout.VERTICAL);


        TextView textView = new TextView(this);
        // Setting android:layout_width and android:layout_height for textView programmatically.
        LinearLayout.LayoutParams layoutParamsTextview = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParamsTextview);
        // Setting Textbox title
        textView.setText(textboxTitle);
        textView.setTag("textbox_title_tag");


        EditText editText = new EditText(this);
        // Setting android:layout_width and android:layout_height for editText programmatically.
        LinearLayout.LayoutParams layoutParamsEdittext = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(layoutParamsEdittext);
        editText.setTag("textbox_input_tag");


        linearLayout.addView(textView);
        linearLayout.addView(editText);

        cardView.addView(linearLayout);

        addCardsHere.addView(cardView);

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }


}
