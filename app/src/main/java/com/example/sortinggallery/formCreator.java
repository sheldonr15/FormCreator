package com.example.sortinggallery.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.sortinggallery.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

public class formCreator extends AppCompatActivity {
    DatePickerDialog picker;

    Button toBtn;
    EditText toEditText;
    Button fromBtn;
    EditText fromEditText;

    int toDay;
    int toMonth;
    int toYear;

    int fromDay;
    int fromMonth;
    int fromYear;

    Button addFieldBtn;
    Button deleteFieldBtn;
    int selectedItemIndex;

    private LinearLayout parentLinearLayout;
    private LinearLayout linearParent;
    // private ScrollView parentLinearLayout;

    boolean fieldCreated = false;

    Button submit;

    TextInputEditText formTitle;
    TextInputEditText formDesc;
    EditText dateFrom;
    EditText dateTo;

    int count = 0;

    JSONObject obj = new JSONObject();

    int addBulletId = 0;                                // For the individual options
    int uniqueIDforBulletCheckText = 200;               // For the entire card (bullet / checkbox / text field)
    int uniqueIDforBulletCheckTextTitle = 1000;         // For the title of each card


    static Socket s;
    static ServerSocket ss;
    static InputStreamReader isr;
    static BufferedReader br;
    static String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_creator);

        toBtn = (Button)findViewById(R.id.toBtn);
        toEditText = (EditText)findViewById(R.id.toEditText);

        fromBtn = (Button)findViewById(R.id.fromBtn);
        fromEditText = (EditText)findViewById(R.id.fromEditText);

        addFieldBtn = (Button)findViewById(R.id.addField);
        // deleteFieldBtn = (Button)findViewById(R.id.cross_button);

        //addBulletField = (Button)findViewById()
        submit = findViewById(R.id.submit);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        toBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                toDay = cldr.get(Calendar.DAY_OF_MONTH);
                toMonth = cldr.get(Calendar.MONTH);
                toYear = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(formCreator.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                toEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                toYear = year;
                                toMonth = monthOfYear+1;
                                toDay = dayOfMonth;
                                Log.d("formCreator.java", "TO/FROM on to click : " + toYear + "  month : " + toMonth + "  day : " + toDay);
                            }
                        }, toYear, toMonth, toDay);

                if(fromYear==0){
                    picker.getDatePicker().setMinDate(cldr.getTimeInMillis()-1000);
                    // Log.d("formCreator.java", "on 'to' click and 'from' with default : year : " + fromYear + "  month : " + fromMonth + "  day : " + fromDay);
                    Log.d("formCreator.java", "TO/FROM 'from' is not set. From date : " + fromYear + " / " + fromMonth + " / " + fromDay);
                } else {
                    // Log.d("formCreator.java", "on 'to' click and when 'from' is set : year : " + fromYear + "  month : " + fromMonth + "  day : " + fromDay);
                    Log.d("formCreator.java", "TO/FROM 'from' is set. From date : " + fromYear + " / " + fromMonth + " / " + fromDay);
                    Calendar toMinDate = Calendar.getInstance();
                    toMinDate.set(fromYear, fromMonth-1, fromDay);
                    picker.getDatePicker().setMinDate(toMinDate.getTimeInMillis()-1000);
                }

                picker.show();
            }
        });

        fromBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                fromDay = cldr.get(Calendar.DAY_OF_MONTH);
                fromMonth = cldr.get(Calendar.MONTH);
                fromYear = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(formCreator.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                fromEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                fromYear = year;
                                fromMonth = monthOfYear+1;
                                fromDay = dayOfMonth;
                                Log.d("formCreator.java", "TO/FROM on from click : " + fromYear + "  month : " + fromMonth + "  day : " + fromDay);
                            }
                        }, fromYear, fromMonth, fromDay);

                // Log.d("formCreator.java", "onClick: year : " + fromYear + "  month : " + fromMonth + "  day : " + fromDay);

                // Calendar forMinDate = Calendar.getInstance();
                // forMinDate.set(year, month, day);
                // forMinDate.set(2020, 11, 10);

                picker.getDatePicker().setMinDate(cldr.getTimeInMillis()-1000);
                // picker.getDatePicker().setMinDate(System.currentTimeMillis()-1000);

                if(toYear==0){
                    picker.getDatePicker().setMinDate(cldr.getTimeInMillis()-1000);
                    //Log.d("formCreator.java", "on 'from' click and 'to' with default : year : " + toYear + "  month : " + toMonth + "  day : " + toDay);
                    Log.d("formCreator.java", "TO/FROM 'to' is not set. To date : " + toYear + " / " + toMonth + " / " + toDay);
                } else {
                    // Log.d("formCreator.java", "on 'form' click and when 'to' is set : year : " + fromYear + "  month : " + fromMonth + "  day : " + fromDay);
                    Log.d("formCreator.java", "TO/FROM 'to' is set. To date : " + toYear + " / " + toMonth + " / " + toDay);
                    Calendar toMaxDate = Calendar.getInstance();
                    toMaxDate.set(toYear, toMonth-1, toDay);
                    picker.getDatePicker().setMaxDate(toMaxDate.getTimeInMillis()-1000);
                    picker.getDatePicker().setMinDate(cldr.getTimeInMillis()-1000);
                }

                picker.show();
            }
        });

        // parentLinearLayout = (LinearLayout) findViewById(R.id.emptyLayout);
        // parentLinearLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);
        parentLinearLayout = findViewById(R.id.parent_linear_layout);

        // linearParent = findViewById(R.id.linear_parent);

        addFieldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //AlertDialog selectField = (AlertDialog) onCreateDialog();
                //selectField.show();
                onCreateDialog();

            }
        });
        Log.d("formCreator.java", "DIALOG > 'HERE' FIRST ");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                formTitle = (TextInputEditText)findViewById(R.id.form_title);
                formDesc = (TextInputEditText)findViewById(R.id.form_desc);
                dateFrom = (EditText)findViewById(R.id.fromEditText);
                dateTo = (EditText)findViewById(R.id.toEditText);

                Log.d("formCreate", "JSON Debug : Form Title ID" + formTitle.getId());

                String formTitleString = formTitle.getText().toString().trim();
                String formDescString = formDesc.getText().toString().trim();
                String dateFromString = dateFrom.getText().toString().trim();
                String dateToString = dateTo.getText().toString().trim();

                try {
                    obj.put("FormTitle", formTitleString);
                    obj.put("FormDescription", formDescString);
                    obj.put("DateFrom", dateFromString);
                    obj.put("DateTo", dateToString);
                    obj.put("formStruct", new JSONObject());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    show_children(getWindow().getDecorView());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("formCreate", "JSON Object final :  " + obj.toString());

                // Send JSON String to Laptop
                // MessageSender messageSender = new MessageSender();
                // messageSender.execute(obj.toString());

                // -----------------------------

                // Send JSON String to Laptop using MessageSender2
                /*
                Log.d("formCreate", "JSON File Debug : Connecting to server . . . ");
                ClientThread clientThread = new ClientThread();
                Thread thread = new Thread(clientThread);
                thread.start();
                Log.d("formCreate", "JSON File Debug : Connection Started ");

                if (null != clientThread) {
                    clientThread.sendMessage(obj.toString());
                }

                Log.d("formCreate", "JSON File Debug : Disconnecting");
                clientThread = null;
                */



                // Save JSON File in Mobile
                String state = Environment.getExternalStorageState();
                if(!Environment.MEDIA_MOUNTED.equals(state)) {
                    Log.d("formCreate", "JSON File Debug : File not mounted! ");
                }

                File file = new File(getExternalFilesDir(null), "formMaker.json");

                FileOutputStream outputStream = null;

                try {
                    file.createNewFile();
                    outputStream = new FileOutputStream(file, true);

                    outputStream.write(obj.toString().getBytes());
                    outputStream.flush();
                    outputStream.close();

                    Log.d("formCreate", "JSON File Debug : File created! ");
                } catch (Exception e) {
                    Log.d("formCreate", "JSON File Debug : File error " + e.getMessage());
                }

            }
        });




    }

    private void show_children(View v) throws JSONException {

        ViewGroup viewgroup=(ViewGroup)v;

        for (int i=0; i<viewgroup.getChildCount(); i++) {

            View v1=viewgroup.getChildAt(i);

            if (v1 instanceof ViewGroup) show_children(v1);

            if(v1.getTag()==null){
                Log.d("formCreate", "This : Empty");
                continue;
            }
            else {
                if(v1.getTag().toString().equals("bullet_title")){
                    // Debug
                    Log.d("formCreate", "JSON Debug : bullet_title");
                    Log.d("formCreate", "JSON Debug : bullet_title ID" + v1.getId());

                    // Bullet Title
                    TextInputEditText bulletTitle = (TextInputEditText) findViewById(v1.getId());
                    String bulletTitleString = bulletTitle.getText().toString().trim();

                    count += 1;
                    // Insert index of bullet
                    JSONObject formStructKey = obj.getJSONObject("formStruct");
                    formStructKey.put(""+count, new JSONObject());

                    // Name field type as 'bullet'
                    JSONObject bulletFieldType = obj.getJSONObject("formStruct").getJSONObject(""+count);
                    bulletFieldType.put("FieldType", "bullet");
                    bulletFieldType.put("Title", bulletTitleString);
                }
                else if(v1.getTag().toString().equals("checkbox_title")){
                    // Debug
                    Log.d("formCreate", "JSON Debug : checkbox_title");

                    // Checkbox Title
                    TextInputEditText checkboxTitle = (TextInputEditText) findViewById(v1.getId());
                    String checkboxTitleString = checkboxTitle.getText().toString().trim();

                    count += 1;
                    // Insert index of checkbox
                    JSONObject formStructKey = obj.getJSONObject("formStruct");
                    formStructKey.put(""+count, new JSONObject());

                    // Name field type as 'checkbox'
                    JSONObject checkboxFieldType = obj.getJSONObject("formStruct").getJSONObject(""+count);
                    checkboxFieldType.put("FieldType", "checkbox");
                    checkboxFieldType.put("Title", checkboxTitleString);
                }
                else if(v1.getTag().toString().equals("text_field")){
                    // Debug
                    Log.d("formCreate", "JSON Debug : text_field");
                    // Textbox Title
                    TextInputEditText textTitle = (TextInputEditText) findViewById(v1.getId());
                    String TextTitleString = textTitle.getText().toString().trim();

                    count += 1;
                    // Insert index of textbox
                    JSONObject formStructKey = obj.getJSONObject("formStruct");
                    formStructKey.put(""+count, new JSONObject());

                    // Name field type as 'textbox'
                    JSONObject textboxFieldType = obj.getJSONObject("formStruct").getJSONObject(""+count);
                    textboxFieldType.put("FieldType", "textbox");
                    textboxFieldType.put("Title", TextTitleString);
                    textboxFieldType.put("submissions", new JSONArray());

                }
                else if(v1.getTag().toString().equals("bullet_field")) {
                    // Debug
                    Log.d("formCreate", "JSON Debug : bullet_field");

                    // Option Title
                    TextInputEditText FieldText = (TextInputEditText) findViewById(v1.getId());
                    String FieldTextString = FieldText.getText().toString().trim();
                    Log.d("formCreate", "JSON Debug : HEREx1 and count : " + count);

                    JSONObject insertBulletField = obj.getJSONObject("formStruct").getJSONObject(""+count);
                    Log.d("formCreate", "JSON Debug : HEREx2");
                    if(insertBulletField.has("options")){
                        Log.d("formCreate", "JSON Debug : has 'options' and to put : " + FieldTextString + " and id : " + v1.getId());
                        JSONObject Options = insertBulletField.getJSONObject("options");
                        Options.put(FieldTextString, 0);
                    }
                    else{
                        Log.d("formCreate", "JSON Debug : does not have 'options' and to put : " + FieldTextString + " and id : " + v1.getId());
                        insertBulletField.put("options", new JSONObject());
                        JSONObject Options = insertBulletField.getJSONObject("options");

                        Options.put(FieldTextString, 0);
                    }
                }
                else if(v1.getTag().toString().equals("submit_button")){
                    break;
                }
                else {
                    continue;
                }
                Log.d("formCreate", "This : " + v1.getTag().toString());
                // Log.d("formCreate", "This : " + v1.toString() + " ID : " + v1.getId());
            }
            // Log.d("formCreate", "This : " + v1.getTag().toString());
            // Log.d("formCreate", "This : id() : " + v1.getId() + " >> object : " + v1.toString());
            // if(v1.getId()==View.NO_ID || v1.getResources().getResourceName(v.getId())=="#0xffffffff" ) {
            //     Log.d("formCreate", "This : no name");
            // }
            // else{
            //     Log.d("formCreate", "This : " + v1.getResources().getResourceName(v.getId()));
            // }

        }


        // Log.d("formCreate", "JSON Object :  " + obj.get("formStruct").toString());
        Log.d("formCreate", "JSON Object :  " + obj.toString());

//        try {
//            File testFileExists = new File("json_array_output.json");
//            if (testFileExists.exists()){
//                // FileWriter file = new FileWriter("E:/Sheldon/FormMakerJson/json_array_output.json");
//                FileWriter file = new FileWriter("json_array_output.json");
//                file.write(obj.toString());
//                file.close();
//            }
//            else{
//                Log.d("formCreate", "JSON Debug : File does not exist! ");
//            }
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        Log.d("formCreate", "------------------------------------------------------------");
    }

    // @Override
    // public Dialog onCreateDialog() {
    public void onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(formCreator.this);
        builder.setTitle(R.string.pick_color)
                .setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        selectedItemIndex = which+1;
                        if(selectedItemIndex==1 || selectedItemIndex==2 || selectedItemIndex==3){
                            fieldCreated = true;
                            Log.d("formCreator.java", "DIALOG > 'Bullet Point selected' selectedItemIndex is : " + selectedItemIndex + " Is a field created? : " + fieldCreated);
                            onAddField(parentLinearLayout, selectedItemIndex);
                            Log.d("formCreator.java", "DIALOG > 'HERE' ");

                        }
                        else {
                            Log.d("formCreator.java", "DIALOG > 'Bullet Point NOT selected' selectedItemIndex is : " + selectedItemIndex);
                        }
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });
        Log.d("formCreator.java", "DIALOG > 'else' on 'Add field' button click, selectedItemIndex is : " + selectedItemIndex);
        //return builder.create();
        builder.create().show();
    }

    public void onAddField(View v, int selectedItem) {
        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // final CardView rowView = (CardView) inflater.inflate(R.layout.bullet_point, null);

        // final View rowView=inflater.inflate(R.layout.bullet_point, null);
        View rowView = null;

        if(selectedItem==1){
            rowView=inflater.inflate(R.layout.bullet_field, null);

            // Change ID of the card
            Log.d("formCreate", "JSON Object :  bullet field id : " + rowView.getId() + " and field number : " + uniqueIDforBulletCheckText);
            rowView.setId(uniqueIDforBulletCheckText);
            uniqueIDforBulletCheckText += 1;
            Log.d("formCreate", "JSON Object :  bullet field id : " + rowView.getId() + " and field number : " + uniqueIDforBulletCheckText);

            // Change ID of card title
            View changeBulletTitle = rowView.findViewById(R.id.bullet_title_text);
            changeBulletTitle.setId(uniqueIDforBulletCheckTextTitle);
            uniqueIDforBulletCheckTextTitle +=1;

        }
        else if (selectedItem==2){
            Log.d("formCreator.java", "Checkbox Selected " + selectedItem);
            rowView=inflater.inflate(R.layout.checkbox_field, null);

            // Change ID of the card
            Log.d("formCreate", "JSON Object :  bullet field id : " + rowView.getId() + " and field number : " + uniqueIDforBulletCheckText);
            rowView.setId(uniqueIDforBulletCheckText);
            uniqueIDforBulletCheckText += 1;
            Log.d("formCreate", "JSON Object :  bullet field id : " + rowView.getId() + " and field number : " + uniqueIDforBulletCheckText);

            // Change ID of card title
            View changeBulletTitle = rowView.findViewById(R.id.checkbox_title_text);
            changeBulletTitle.setId(uniqueIDforBulletCheckTextTitle);
            uniqueIDforBulletCheckTextTitle +=1;
        }
        else if (selectedItem==3){
            rowView=inflater.inflate(R.layout.text_field, null);

            // Change ID of the card
            Log.d("formCreate", "JSON Object :  bullet field id : " + rowView.getId() + " and field number : " + uniqueIDforBulletCheckText);
            rowView.setId(uniqueIDforBulletCheckText);
            uniqueIDforBulletCheckText += 1;
            Log.d("formCreate", "JSON Object :  bullet field id : " + rowView.getId() + " and field number : " + uniqueIDforBulletCheckText);

            // Change ID of card title
            View changeBulletTitle = rowView.findViewById(R.id.text_title_text);
            changeBulletTitle.setId(uniqueIDforBulletCheckTextTitle);
            uniqueIDforBulletCheckTextTitle +=1;
        }


        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);

        if(!addFieldBtn.isShown()){
            Log.d("formCreator.java", "DIALOG > 'Add Field' button is invisible. ");
            ScrollView sv = findViewById(R.id.scroll_layout);
            sv.scrollTo(0, sv.getBottom());
        }
        else {
            Log.d("formCreator.java", "DIALOG > 'Add Field' button is visible. ");
        }
    }

    public void onAddBulletField(View v) throws JSONException {

        //if(selectedItemIndex==1) {
        //    linearParent = findViewById(R.id.linear_parent_bullet);
        //}
        //else if (selectedItemIndex==2) {
        //    linearParent = findViewById(R.id.linear_parent_checkbox);
        //}

        // linearParent = findViewById(R.id.linear_parent_bullet);
        linearParent = (LinearLayout) v.getParent();

        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // final CardView rowView = (CardView) inflater.inflate(R.layout.bullet_point, null);

        // final View rowView=inflater.inflate(R.layout.bullet_point, null);
        final View bulletView=inflater.inflate(R.layout.add_to_bullet, null);
        View changeBulletTextfieldId = bulletView.findViewWithTag("bullet_field");
        //show_children(bulletView);

        Log.d("formCreate", "JSON Object :  bullet field id : " + changeBulletTextfieldId.getId() + " and field number : " + addBulletId);
        changeBulletTextfieldId.setId(addBulletId);
        addBulletId += 1;
        Log.d("formCreate", "JSON Object :  bullet field id : " + changeBulletTextfieldId.getId() + " and field number : " + addBulletId);

        // Add the new row before the add field button.
        linearParent.addView(bulletView, linearParent.getChildCount() - 1);
        Log.d("formCreator.java", "DIALOG > linearParent number of child views : " + linearParent.getChildCount() );

    }

    public void onAddCheckboxField(View v) {

        // linearParent = findViewById(R.id.linear_parent_checkbox);
        linearParent = (LinearLayout) v.getParent();

        ViewGroup mainBox = (ViewGroup) v.getParent().getParent().getParent().getParent();
        ViewParent indexOf = v.getParent().getParent().getParent();

        Log.d("formCreator.java", "Index of View in parent : " + mainBox.indexOfChild((View)indexOf));

        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // final CardView rowView = (CardView) inflater.inflate(R.layout.bullet_point, null);

        // final View rowView=inflater.inflate(R.layout.bullet_point, null);
        final View checkboxView=inflater.inflate(R.layout.add_to_bullet, null);
        View changeCheckboxTextfieldId = checkboxView.findViewWithTag("bullet_field");
        //show_children(bulletView);

        Log.d("formCreate", "JSON Object :  bullet field id : " + changeCheckboxTextfieldId.getId() + " and field number : " + addBulletId);
        changeCheckboxTextfieldId.setId(addBulletId);
        addBulletId += 1;
        Log.d("formCreate", "JSON Object :  bullet field id : " + changeCheckboxTextfieldId.getId() + " and field number : " + addBulletId);

        // Add the new row before the add field button.
        linearParent.addView(checkboxView, linearParent.getChildCount() - 1);
        Log.d("formCreator.java", "DIALOG > linearParent number of child views : " + linearParent.getChildCount() );

    }


    public void onDelete(View v) {
        Log.d("formCreator.java", "DIALOG > Delete Button clicked!!");
        parentLinearLayout.removeView((View) v.getParent().getParent().getParent().getParent());
    }

    public void onBulletDelete(View v) {
        linearParent = (LinearLayout) v.getParent().getParent();
        Log.d("formCreator.java", "DIALOG > Delete Button clicked!!");
        linearParent.removeView((View) v.getParent());
    }

    public void onTextfieldDelete(View v) {
        Log.d("formCreator.java", "DIALOG > Delete Button clicked!!");
        parentLinearLayout.removeView((View) v.getParent().getParent().getParent());
    }


    // Sending data to local pc
    class ClientThread implements Runnable {

        private Socket socket;
        private BufferedReader input;

        @Override
        public void run() {

            try {
                // InetAddress serverAddr = InetAddress.getByName("10.0.2.2");
                socket = new Socket("192.168.0.104", 7800);
                //socket.setSoTimeout(50*1000);

                while (!Thread.currentThread().isInterrupted()) {

                    this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String message = input.readLine();
                    if (null == message || "Disconnect".contentEquals(message)) {
                        Thread.interrupted();
                        message = "Server Disconnected.";
                        Log.d("MessageSender2", "JSON File Debug : " + message);
                        break;
                    }

                    Log.d("MessageSender2", "JSON File Debug 2 : " + message);
                    Log.d("formCreate", "JSON File Debug : Message Sent");

                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            } catch (UnknownHostException e1) {
                Log.d("MessageSender2", "JSON File Debug run() UnknownHostException : " + e1.getMessage());
                e1.printStackTrace();
            } catch (IOException e1) {
                Log.d("MessageSender2", "JSON File Debug run() IOException : " + e1.getMessage());
                e1.printStackTrace();
            }

        }

        void sendMessage(final String message) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (null != socket) {
                            PrintWriter out = new PrintWriter(new BufferedWriter(
                                    new OutputStreamWriter(socket.getOutputStream())),
                                    true);
                            out.println(message);
                        }
                    } catch (Exception e) {
                        Log.d("MessageSender2", "JSON File Debug 'Send Message Error' : " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

}
