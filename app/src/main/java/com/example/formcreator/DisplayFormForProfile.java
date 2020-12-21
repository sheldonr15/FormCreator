package com.example.formcreator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;


public class DisplayFormForProfile extends AppCompatActivity {
    String jsonObj = "";

    String formDescription;

    LinearLayout addCardsHere;

    TextView formTitleView;
    TextView formDescriptionView;

    Button deleteButton;
    Button downloadButton;

    JSONObject formStructure;
    JSONObject jsonObject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_form_for_profile);

        addCardsHere = findViewById(R.id.display_form_linearlayout);
        deleteButton = findViewById(R.id.display_form_delete);
        downloadButton = findViewById(R.id.display_form_download);


        Intent intent = getIntent();
        String formTitle = intent.getStringExtra("fTitle");
        String formAddress = intent.getStringExtra("fAddress");

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "default");

        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(DisplayFormForProfile.this)
                        .setTitle("Confirm Deletion?")
                        .setMessage("Are you sure you want to DELETE?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FormCardsDatabaseHelper db = new FormCardsDatabaseHelper(DisplayFormForProfile.this);
                                int res = db.deleteForm(username, formTitle, formAddress, getExternalFilesDir(null).getPath());
                                if(res==0){
                                    Toast.makeText(DisplayFormForProfile.this, "No rows were deleted :(", Toast.LENGTH_SHORT).show();
                                }
                                else if(res==1){
                                    Toast.makeText(DisplayFormForProfile.this, "Form deleted Successfully!", Toast.LENGTH_SHORT).show();
                                }

                                onBackPressed();
                            }
                        }).create().show();

                /*
                FormCardsDatabaseHelper db = new FormCardsDatabaseHelper(DisplayFormForProfile.this);
                int res = db.deleteForm(username, formTitle, formAddress);
                if(res==0){
                    Toast.makeText(DisplayFormForProfile.this, "No rows were deleted :(", Toast.LENGTH_SHORT).show();
                }
                else if(res==1){
                    Toast.makeText(DisplayFormForProfile.this, "Form deleted Successfully!", Toast.LENGTH_SHORT).show();
                }

                onBackPressed();
                */

            }
        });


        formTitleView = findViewById(R.id.display_form_title_textview);
        formDescriptionView = findViewById(R.id.display_form_desc_textview);

        // Set Form title
        formTitleView.setText(formTitle);

        // Get .json file and convert to JSONObject
        String fullUrl = this.getExternalFilesDir(null) + "/" + formAddress;
        Log.d("DisplayFormForProfile", "file location is : " + fullUrl);
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
        Log.d("DisplayFormForProfile", "JSON Object : " + jsonObject.toString());

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

        // Download .csv of results
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // File outputFile = new File(Environment.DIRECTORY_DOWNLOADS, username + ".csv");
                    File outputFile = new File(getExternalFilesDir(null) + "/" + username, username + ".csv");
                    // File outputFile = new File(getExternalFilesDir(null) + "/TP", username + ".csv");

                    Log.d("DisplayFormForProfile", "Directory of Downloads : " + Environment.DIRECTORY_DOWNLOADS);
                    Log.d("DisplayFormForProfile", "Path of file : " + outputFile.getAbsolutePath());

                    if(outputFile.createNewFile()){
                        Log.d("DisplayFormForProfile", "File Created");
                    }
                    else{
                        Log.d("DisplayFormForProfile", "File already exists");
                        outputFile.delete();
                        outputFile.createNewFile();
                    }


                    try{
                        CSVWriter writer = new CSVWriter(new FileWriter(outputFile), ',');

                        // Add Form Title
                        String[] formTitle = new String[2];
                        String formTitleString = jsonObject.getString("FormTitle");
                        formTitle[0] = "Form Title";
                        formTitle[1] = formTitleString;
                        writer.writeNext(formTitle);

                        // Add Form Description
                        String[] formDescription = new String[2];
                        String formDescriptionString = jsonObject.getString("FormDescription");
                        formDescription[0] = "Form Description";
                        formDescription[1] = formDescriptionString;
                        writer.writeNext(formDescription);

                        // Add Total Visits
                        /*
                        String[] totalVisits = new String[2];
                        int totalVisitsNumber = jsonObject.getInt("TotalVisits");
                        totalVisits[0] = "Number of form submissions";
                        totalVisits[1] = String.valueOf(totalVisitsNumber);
                        writer.writeNext(totalVisits);
                        */

                        // Get Form Structure
                        JSONObject formStruct = jsonObject.getJSONObject("formStruct");

                        for(int i=1; i<=formStruct.length(); i++){
                            String[] emptyLine = new String[1];
                            emptyLine[0] = "";
                            writer.writeNext(emptyLine);

                            JSONObject currentField = formStruct.getJSONObject(""+i);

                            if(currentField.getString("FieldType").equalsIgnoreCase("bullet")){
                                writer.writeAll(addRadioButton(currentField));
                            }
                            else if(currentField.getString("FieldType").equalsIgnoreCase("checkbox")){
                                writer.writeAll(addCheckbox(currentField));
                            }
                            else if(currentField.getString("FieldType").equalsIgnoreCase("textbox")){
                                writer.writeAll(addTextBox(currentField));
                            }

                        }

                        writer.close();

                    }
                    catch (Exception e){
                        Log.d("DisplayFormForProfile", "Error : " + e.getMessage());
                    }


                    Toast.makeText(DisplayFormForProfile.this, "CSV File saved in " + getExternalFilesDir(null) + "/" + username, Toast.LENGTH_LONG).show();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

//        // Enabling Back button to return to Parent Activity i.e. .MainActivity2
//        ActionBar ab = getSupportActionBar();
//        ab.setDisplayHomeAsUpEnabled(true);

    }

    public static List<String[]> addRadioButton(JSONObject currentField){

        List<String[]> iterString = new ArrayList<String[]>();


        // Add Field Type
        String[] currentString = new String[2];
        currentString[0] = "Field Type";
        currentString[1] = "Radio Buttons";
        iterString.add(currentString);
        // writer.writeNext(currentString);

        // Add Field Type
        String titleOfField = null;
        try{
            titleOfField = currentField.getString("Title");
        }
        catch(JSONException e){
            System.out.println(e.getMessage());
        }
        currentString = new String[2];
        currentString[0] = "Field Title";
        currentString[1] = titleOfField;
        iterString.add(currentString);
        // writer.writeNext(currentString);

        // Add options
        currentString = new String[2];
        currentString[0] = "Options";
        currentString[1] = "Results";
        iterString.add(currentString);
        // writer.writeNext(currentString);

        // Add options fields
        Iterator<?> options = null;
        try{
            // int optionsSize = currentField.getJSONObject("options").length();
            options = currentField.getJSONObject("options").keys();
        }
        catch(JSONException e){
            System.out.println(e.getMessage());
        }

        while(options.hasNext()){
            String option = (String)options.next();
            int count = 0;
            try{
                count = currentField.getJSONObject("options").getInt(option);
            }
            catch(JSONException e){
                System.out.println(e.getMessage());
            }
            Log.d("DisplayFormForProfile", " value of count for " + option + " is : " + count);
            currentString = new String[2];
            currentString[0] = option;
            currentString[1] = String.valueOf(count);
            iterString.add(currentString);
            // writer.writeNext(currentString);
        }

        return iterString;
    }

    public static List<String[]> addCheckbox(JSONObject currentField){

        System.out.println("In Checkbox");
        List<String[]> iterString = new ArrayList<String[]>();


        // Add Field Type
        String[] currentString = new String[2];
        currentString[0] = "Field Type";
        currentString[1] = "Checkbox";
        iterString.add(currentString);
        // writer.writeNext(currentString);

        // Add Field Type
        String titleOfField = null;
        try{
            titleOfField = currentField.getString("Title");
        }
        catch(JSONException e){
            System.out.println(e.getMessage());
        }
        currentString = new String[2];
        currentString[0] = "Field Title";
        currentString[1] = titleOfField;
        iterString.add(currentString);
        // writer.writeNext(currentString);

        // Add options
        currentString = new String[2];
        currentString[0] = "Options";
        currentString[1] = "Results";
        iterString.add(currentString);
        // writer.writeNext(currentString);

        // Add options fields
        Iterator<?> options = null;
        try{
            // int optionsSize = currentField.getJSONObject("options").length();
            options = currentField.getJSONObject("options").keys();
        }
        catch(JSONException e){
            System.out.println(e.getMessage());
        }

        while(options.hasNext()){
            String option = (String)options.next();
            int count = 0;
            try{
                count = currentField.getJSONObject("options").getInt(option);
            }
            catch(JSONException e){
                System.out.println(e.getMessage());
            }
            currentString = new String[2];
            currentString[0] = option;
            currentString[1] = String.valueOf(count);
            iterString.add(currentString);
            // writer.writeNext(currentString);
        }

        return iterString;
    }

    public static List<String[]> addTextBox(JSONObject currentField){

        System.out.println("In TextBox");
        List<String[]> iterString = new ArrayList<String[]>();


        // Add Field Type
        String[] currentString = new String[2];
        currentString[0] = "Field Type";
        currentString[1] = "Text Inputs";
        iterString.add(currentString);
        // writer.writeNext(currentString);

        // Add Field Type
        String titleOfField = null;
        try{
            titleOfField = currentField.getString("Title");
        }
        catch(JSONException e){
            System.out.println(e.getMessage());
        }
        currentString = new String[2];
        currentString[0] = "Field Title";
        currentString[1] = titleOfField;
        iterString.add(currentString);
        // writer.writeNext(currentString);

        // Add options
        currentString = new String[1];
        currentString[0] = "Submissions";
        iterString.add(currentString);
        // writer.writeNext(currentString);


        // Add submissions

        JSONArray submissionArray = null;
        int submissionArrayLength = 0;
        try{
            submissionArray = currentField.getJSONArray("submissions");
            submissionArrayLength = submissionArray.length();
        }
        catch(JSONException e){
            System.out.println(e.getMessage());
        }

        for(int k = 0; k<submissionArrayLength; k++){
            currentString = new String[1];
            try{
                currentString[0] = submissionArray.getString(k);
                iterString.add(currentString);
            }
            catch(JSONException e){
                System.out.println(e.getMessage());
            }
        }



        return iterString;
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


        TextView textView = new TextView(this);
        // Setting android:layout_width and android:layout_height for textView programmatically.
        LinearLayout.LayoutParams layoutParamsTextview = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParamsTextview);
        // Setting Radio Group title
        textView.setText(radioButtonTitle);

        linearLayout.addView(textView);

        Iterator<String> optionNames = radioButtonOptions.keys();

        while(optionNames.hasNext()){
            String option = optionNames.next();
            RadioButton radioButton = new RadioButton(this);
            LinearLayout.LayoutParams layoutParamsRadiobutton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            radioButton.setLayoutParams(layoutParamsRadiobutton);
            radioButton.setText(option);
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
        // Setting Chechbox title
        textView.setText(checkboxTitle);

        linearLayout.addView(textView);

        Iterator<String> optionNames = checkboxOptions.keys();

        while(optionNames.hasNext()){
            String option = optionNames.next();
            CheckBox checkBox = new CheckBox(this);
            LinearLayout.LayoutParams layoutParamsCheckbox = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            checkBox.setLayoutParams(layoutParamsCheckbox);
            checkBox.setText(option);
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


        EditText editText = new EditText(this);
        // Setting android:layout_width and android:layout_height for editText programmatically.
        LinearLayout.LayoutParams layoutParamsEdittext = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(layoutParamsEdittext);


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
