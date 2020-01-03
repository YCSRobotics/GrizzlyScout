package com.ycsrobotics.grizzlyscout;

import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EditTeamFragment.OnFragmentInteractionListener, HomePageFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        initializeDefaults();

        //load our database driver
        try {
            DriverManager.registerDriver((Driver) Class.forName("org.sqldroid.SQLDroidDriver").newInstance());
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "Failed to register SQLDroidDriver");
            throw new RuntimeException("Failed to register SQLDroidDriver");
        }

        String jdbcUrl = "jdbc:sqldroid:" + this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/matches.db";
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl);

            Log.v(getString(R.string.app_name), "Using database directory ".concat(jdbcUrl));
            //TODO update for 2020 game
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS MATCHES (UniqueKey integer primary key, Match_Number integer, Team_Number integer, " +
                    "Sandstorm_Hab_Level integer, Sandstorm_Cargoship_Hatches_Scored integer, Sandstorm_Cargoship_Cargo_Scored integer, " +
                    "Sandstorm_Low_Hatches_Scored integer, Sandstorm_Low_Cargo_Scored integer, Sandstorm_Mid_Hatches_Scored integer, " +
                    "Sandstorm_Mid_Cargo_Scored integer, Sandstorm_High_Hatches_Scored integer, Sandstorm_High_Cargo_Scored integer, " +
                    "Sandstorm_Hatches_Dropped integer, Sandstorm_Cargo_Dropped integer, Teleop_Cargoship_Hatches_Scored integer, " +
                    "Teleop_Cargoship_Cargo_Scored integer, Teleop_Low_Hatches_Scored integer, Teleop_Low_Cargo_Scored integer," +
                    "Teleop_Mid_Hatches_Scored integer, Teleop_Mid_Cargo_Scored integer, Teleop_High_Hatches_Scored integer," +
                    "Teleop_High_Cargo_Scored integer, Teleop_Hatches_Dropped integer, Teleop_Cargo_Dropped integer," +
                    "MechanicalIssues integer, AdditionalNotes string)");
            connection.close();

            Log.i(getString(R.string.app_name), "Connection to local grizzlyscout_icon database successful!");
        } catch (SQLException e) {
            Log.e(getString(R.string.app_name), "Error connecting to grizzlyscout_icon database!", e);
            throw new RuntimeException(e);
        }
    }

        public void initializeDefaults() {
            TextView view = findViewById(R.id.bottom_nav_version);
            view.setText(getString(R.string.app_name).concat(" Version: ").concat(BuildConfig.VERSION_NAME));

        showSplashFragment(null);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showTeamFragment() {
        Fragment fragmentTeam = new EditTeamFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, fragmentTeam);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.scout_new_team:
                Log.i(getString(R.string.app_name), "Scout New Team option has been selected");
                showTeamFragment();
                break;
            case R.id.edit_existing_team:
                Log.i(getString(R.string.app_name), "Edit Existing Team option has been selected");
                showTeamFragment();
                break;
            default:
                //should never happen
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void showSplashFragment(View view) {
        Fragment fragmentSplash = new HomePageFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, fragmentSplash);
        transaction.commit();
    }
}
