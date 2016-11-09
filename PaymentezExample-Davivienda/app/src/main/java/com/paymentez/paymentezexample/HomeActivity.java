package com.paymentez.paymentezexample;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.paymentez.paymentezexample.easysolutions.EasySolutionsActivity;
import com.paymentez.paymentezexample.todo1.Todo1CollectActivity;
import com.paymentez.paymentezexample.utils.Constants;

import net.easysol.dsb.BlockedConnectionListener;
import net.easysol.dsb.DSB;
import net.easysol.dsb.UpdateListener;
import net.easysol.dsb.device_protector.DeviceProtectorEventListener;
import net.easysol.dsb.malware_protector.overlay.OverlapingApp;
import net.easysol.dsb.malware_protector.overlay.OverlayListener;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context myContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        myContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_list) {
            Intent intent = new Intent(this, ListCardsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_add_card) {
            Intent intent = new Intent(this, AddCardActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_verify) {
            Intent intent = new Intent(this, VerifyTransactionActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_todoen1) {
            Intent intent = new Intent(this, Todo1CollectActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_easy_solutions) {
            Intent intent = new Intent(this, EasySolutionsActivity.class);
            startActivity(intent);
        }




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
