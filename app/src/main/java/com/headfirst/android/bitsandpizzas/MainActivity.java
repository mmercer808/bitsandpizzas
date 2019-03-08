package com.headfirst.android.bitsandpizzas;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.ShareActionProvider;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private ShareActionProvider shareActionProvider;
    private String[] titles;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int currentPosition = 0;

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            selectItem(position);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titles = getResources().getStringArray(R.array.titles);
        drawerList = (ListView)findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        // Initialize the ListView
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_activated_1, titles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        // if the MainActivity is newly created, use the selectItem()
        // method to display TopFragment
        if (savedInstanceState != null) {
            // display the correct fragment
            currentPosition = savedInstanceState.getInt("position");
            setActionBarTitle(currentPosition);
        } else {
            selectItem(0);
        }

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.open_drawer, R.string.close_drawer) {
            //Called when a drawer has settled in a completely closed
            // state
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // inValidateOptionsMenu() tells Android to recreate
                // the menu items. We want to change the visibility
                // of the Share action if the drawer is opened or
                // closed
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        // set the ActionBarDrawerToggle as the DrawerLayout's
        // drawer listener
        drawerLayout.setDrawerListener(drawerToggle);

        // enable the Up icon so it can be used by the
        // ActionBarDrawerToggle
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // listens for changes to the back stack -- this includes when a fragment
        // transaction is added to the back stack, and when the user clicks on
        // the back button to navigate to a previous back stack entry.
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        FragmentManager fragMan = getSupportFragmentManager();
                        Fragment fragment = fragMan.findFragmentByTag("visible_fragment");
                        if(fragment instanceof TopFragment) {
                            currentPosition = 0;
                        }
                        if(fragment instanceof PizzaFragment) {
                            currentPosition = 1;
                        }
                        if(fragment instanceof PastaFragment) {
                            currentPosition = 2;
                        }
                        if(fragment instanceof StoresFragment) {
                            currentPosition = 3;
                        }
                        setActionBarTitle(currentPosition);
                        // set the action bar title and highlight the correct
                        // item in the drawer ListView
                        drawerList.setItemChecked(currentPosition, true);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        setIntent("This is example text");
        return super.onCreateOptionsMenu(menu);
    }

    // called when the user clicks on an items in the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // returns true if ActionBarDrawerToggle has handled
        // being clicked. If it returns false, this means that
        // another action item in the action bar has been clicked
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true; // if the ActionBarToggle is clicked, let it handle
        }                   // what happens
        switch(item.getItemId()) {
            case R.id.action_create_order:
                Intent intent = new Intent(this, OrderActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", currentPosition);
    }

    // onPrepareOptionsMenu() gets called whenever
    // inValidateOptionsMenu() gets called
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        // set the share actions visibility to false if the drawer is
        // open, set it to true if it isn't -- hides/shows icon
        menu.findItem(R.id.action_share).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // sync the toggle state after onRestoreInstanceState
        // has occurred. [ActionBarDrawerToggle is in sync with
        // the state of the drawer]
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // so any configuration changes get passed
        // to the ActionBarToggle
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void setIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }

    private void selectItem(int position) {
        currentPosition = position;
        Fragment fragment;
        switch(position) {
            case 1: fragment = new PizzaFragment();
                break;
            case 2: fragment = new PastaFragment();
                break;
            case 3: fragment = new StoresFragment();
                break;
            default: fragment = new TopFragment();
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment, "visible_fragment");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        setActionBarTitle(position); // display the right title in the action bar
        drawerLayout.closeDrawer(drawerList);
    }

    private void setActionBarTitle(int position) {
        String title;
        if(position == 0) {
            title = getResources().getString(R.string.app_name);
        } else {
            title = titles[position];
        }

        getSupportActionBar().setTitle(title);
    }
}
