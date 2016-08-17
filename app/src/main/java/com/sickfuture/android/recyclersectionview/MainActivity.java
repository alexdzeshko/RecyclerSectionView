package com.sickfuture.android.recyclersectionview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("List");
        menu.add("Recycler");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle() == "List") {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_container, new ListFragment()).commit();
        } else if (item.getTitle() == "Recycler") {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_container, new RecyclerFragment()).commit();
        }
        return super.onOptionsItemSelected(item);
    }
}
