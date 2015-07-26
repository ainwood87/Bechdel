package com.example.ainwood.bechdel;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

    private final Activity activity = this;
    private SearchFragment searchFragment;
    private SearchView searchView;
    private BroadcastReceiver itemClick = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getExtras().getLong("bechdelID");
            String url = "http://bechdeltest.com/view/" + id;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layouut.activity_main);
//        handleIntent(getIntent());
        searchFragment = (SearchFragment) getFragmentManager().findFragmentById(android.R.id.content);
        if (searchFragment == null) {
            searchFragment = new SearchFragment();
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content,
                            searchFragment).commit();
            LocalBroadcastManager.getInstance(this).registerReceiver(itemClick, new IntentFilter("openPage"));
        }
    }
//    public void onNextPage(View v) {
//        searchFragment.onNextPage();
//    }
//    public void onPrevPage(View v) {
//        searchFragment.onPrevPage();
//    }
    @Override
    protected void onNewIntent(Intent intent)
    {
        searchView.clearFocus();
        searchFragment.handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        ComponentName name = getComponentName();
        SearchableInfo info = searchManager.getSearchableInfo(name);
        searchView.setSearchableInfo(info);
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
}
