package com.example.swetha_swaminathan.smartshopper;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static AppState appState = new AppState();
    public static List<Product> scannedItems = new ArrayList<Product>();
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final TextView storeName = (TextView)findViewById(R.id.storeName);
        final TextView storeDesc = (TextView)findViewById(R.id.storeDesc);
        final RatingBar storeRatings =(RatingBar)findViewById(R.id.ratings);
        final FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        /*
        * The following code example shows setting an AutocompleteFilter on a PlaceAutocompleteFragment to
        * set a filter returning only results with a precise address.
        */

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("Swetha", "Place: " + place.getName());//get place details here
                Log.i("Swetha", "Place: " + place.getLatLng());//get place details here
                appState.setLocationSelected(true);
                appState.setStoreDetails(place);
                appState.setStoreSelected(true);
                fabAdd.setVisibility(View.VISIBLE);
                findViewById(R.id.place_autocomplete_fragment).setVisibility(View.GONE);
                storeName.setVisibility(View.VISIBLE);
                storeDesc.setVisibility(View.VISIBLE);
                storeRatings.setVisibility(View.VISIBLE);
                storeRatings.setRating(place.getRating());
                storeName.setText(place.getName());
                storeDesc.setText(place.getAddress()+"\nPh: "+place.getPhoneNumber());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("Swetha", "An error occurred: " + status);
            }
        });


        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("Barcode"))
        {
            Barcode barcode = (Barcode) intent.getParcelableExtra("Barcode");
            if(!scannedItems.contains((barcode.displayValue)))
            {
                Product prod = new Product();
                prod.setProductName(barcode.displayValue);
                prod.setBarcodeValue(barcode.rawValue);
                prod.setQuatity(1);
                scannedItems.add(prod);

            }

        }


        /*final FloatingActionButton fabSearch = (FloatingActionButton) findViewById(R.id.fabSearch);
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(appState.isLocationSelected())
                {
                    final String stores[] = new String[] {"Walmart, Story road", "Walmart, Willow Glen", "Walmart, Milpitas"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Nearby Stores");
                    builder.setItems(stores, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            appState.setStoreName(stores[which]);
                            appState.setStoreSelected(true);
                            fabSearch.setVisibility(View.GONE);
                            fabAdd.setVisibility(View.VISIBLE);
                            findViewById(R.id.place_autocomplete_fragment).setVisibility(View.GONE);
                            storeName.setText(stores[which]);
                            storeName.setVisibility(View.VISIBLE);
                        }
                    });
                    builder.show();
                }


            }
        }); */

        if(appState.isStoreSelected())
        {
            fabAdd.setVisibility(View.VISIBLE);
            findViewById(R.id.place_autocomplete_fragment).setVisibility(View.GONE);
            storeName.setText(appState.getStoreName());
            storeName.setVisibility(View.VISIBLE);
            storeDesc.setVisibility(View.VISIBLE);
            storeRatings.setVisibility(View.VISIBLE);            storeRatings.setRating(appState.getStoreDetails().getRating());
            storeName.setText(appState.getStoreDetails().getName());
            storeDesc.setText(appState.getStoreDetails().getAddress()+"\nPh: "+appState.getStoreDetails().getPhoneNumber());
        }

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, scannedItems, prepareListData());

        // setting list adapter
        expListView.setAdapter(listAdapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private  HashMap<String, List<String>> prepareListData() {
        HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();


        // Adding child data
        List<String> prodDetails = new ArrayList<String>();
        prodDetails.add("Manufacturer: Unilever");
        prodDetails.add("Size: 200g");
        prodDetails.add("Refund: 15 days");


        for(Product item:scannedItems)
        {
            listDataChild.put(item.getProductName(),prodDetails);
        }

        return listDataChild;
    }


    /** Called when the user clicks the Send button */
    public void sendMessage() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
