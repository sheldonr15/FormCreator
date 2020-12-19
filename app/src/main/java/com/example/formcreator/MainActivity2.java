package com.example.sortinggallery;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sortinggallery.adapter.RecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    // Boolean loggedInFlag;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        myEdit.putBoolean("loggedInFlag", true);
        myEdit.commit();
        Log.v("MainActivity2.java", "Set as true : " + sharedPreferences.getBoolean("loggedInFlag", false));
        Log.v("MainActivity2.java", "Username : " + sharedPreferences.getString("username", "default"));


        // Get status of loggedInFlag from Login.java
        //if(getIntent().getExtras() != null){

            //loggedInFlag = getIntent().getBooleanExtra("EXTRA_SESSION_ID", false);

        //}
        //else {
            //loggedInFlag = true;
        //}

        // Sets 'activity_main2.xml' as screen.
        setContentView(R.layout.activity_main2);



        // R.id.toolbar from 'app_bar_main.xml' (Top horizontal bar)
        // Add toolbar to screen
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // R.id.fab from 'app_bar_main.xml' (Bottom - right circle button)
        // View is the parent class of FloatingActionButton
        // Snackbar shows a message at the bottom of the screen.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent onFormClick = new Intent(MainActivity2.this, formCreator.class);
                startActivity(onFormClick);
                // startActivityForResult(onFormClick, 420);
            }
        });



        // R.id.drawer_layout from 'activity_main2.xml' (Entire screen which sits on top of other screen)
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // R.id.nav_view from 'activity_main2.xml' (What you see when you slide a navigation drawer)
        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderName = (TextView) headerView.findViewById(R.id.nav_header_name);
        navHeaderName.setText(sharedPreferences.getString("username", "default"));

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
            logout();
            return true;
        });

        //onBackPressed();

    }

    /*
    @Override
    public void onBackPressed(){
        MainActivity2.this.finish();
        getParent().finish();
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }

    public boolean logout(){
        // loggedInFlag = false;
        myEdit.putBoolean("loggedInFlag", false);
        myEdit.commit();
        Log.v("MainActivity2.java", "Set as false on log out : " + sharedPreferences.getBoolean("loggedInFlag", false));

        Intent logOutIntent = new Intent(MainActivity2.this, Login.class);
        // logOutIntent.putExtra("EXTRA_SESSION_ID", loggedInFlag);
        startActivity(logOutIntent);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}