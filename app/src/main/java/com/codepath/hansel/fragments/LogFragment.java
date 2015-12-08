package com.codepath.hansel.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepath.hansel.R;
import com.codepath.hansel.adapters.LogAdapter;
import com.codepath.hansel.models.Pebble;
import com.codepath.hansel.utils.DatabaseHelper;

import java.util.ArrayList;

public abstract class LogFragment extends Fragment  {
    protected SwipeRefreshLayout swipeContainer;
    protected DatabaseHelper dbHelper;
    protected LogAdapter aPebbles;
    private ListView lvPebbles;
    private ArrayList<Pebble> pebbles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        setupViews(view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dbHelper = DatabaseHelper.getInstance(getActivity());
        pebbles = new ArrayList<>();
        aPebbles = new LogAdapter(getActivity(), pebbles);
        loadPebbles();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                swipeContainer.setRefreshing(true);
                loadPebbles();
                swipeContainer.setRefreshing(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public abstract void loadPebbles();

    public void setupViews(View view) {
        lvPebbles = (ListView) view.findViewById(R.id.lvPebbles);
        lvPebbles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                Pebble pebble = aPebbles.getItem(position);
                bundle.putParcelable("pebble", pebble);

                Fragment mapFragment = new MapFragment();
                mapFragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.flContent, mapFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        lvPebbles.setAdapter(aPebbles);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPebbles();
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    protected int getCurrentUserId() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPreferences.getInt("user_id", 1);
    }
}
