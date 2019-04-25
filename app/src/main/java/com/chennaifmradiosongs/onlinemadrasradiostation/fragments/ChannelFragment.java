package com.chennaifmradiosongs.onlinemadrasradiostation.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;

import com.chennaifmradiosongs.onlinemadrasradiostation.ImageLoaderDefintion;
import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.activities.RadioPlayActivity;
import com.chennaifmradiosongs.onlinemadrasradiostation.adapters.AdapterListRadio;
import com.chennaifmradiosongs.onlinemadrasradiostation.models.ItemListRadio;
import com.chennaifmradiosongs.onlinemadrasradiostation.database.RadioDatabase;

import java.util.ArrayList;

/**
 * Created by AswinBalaji on 25-Jan-18.
 */

public class ChannelFragment extends Fragment {
    GridView listView;
    SearchView editsearch;

    ItemListRadio itemListRadio;
    ArrayList<ItemListRadio> arrayItemListRadio;
    RadioDatabase databaseHandler;


    Bundle bundle;
    AdapterListRadio adapterListRadio;

    public static ChannelFragment newInstance(ArrayList<ItemListRadio> url) {

        ChannelFragment fragment = new ChannelFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("urllist", url);
        fragment.setArguments(bundle);
        return fragment;
    }

    // Store instance variables based on arguments passed
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoaderDefintion.initImageLoader(getActivity());
        arrayItemListRadio = new ArrayList<ItemListRadio>();
        arrayItemListRadio = getArguments().getParcelableArrayList("urllist");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.channel_popup, container, false);

        databaseHandler = new RadioDatabase(getActivity());

        // Locate the EditText in listview_main.xml
        editsearch = rootView.findViewById(R.id.search);
        if (editsearch != null) {
            editsearch.setQueryHint("Search...");
            editsearch.setIconifiedByDefault(false);
            editsearch.setSubmitButtonEnabled(true);
            editsearch.setFocusable(false);
            hideKeyboard();
        }
        listView = rootView.findViewById(R.id.list_radio);

        if (arrayItemListRadio != null) {
            adapterListRadio = new AdapterListRadio(getActivity(),
                    R.layout.lsv_item_list_radio, arrayItemListRadio);
            listView.setAdapter(adapterListRadio);
        }
        editsearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                editsearch.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterListRadio.filter(newText.trim());
                listView.invalidate();
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (arrayItemListRadio != null) {
                    itemListRadio = arrayItemListRadio.get(i);
                }
                String radioId = itemListRadio.getRadioId();

                startActivity(new Intent(getActivity(), RadioPlayActivity.class).putExtra("Radio_id", radioId)
                        .putExtra("Radio_position", i)
                        .putExtra("favorite_activity", 0));
                hideKeyboard();
            }
        });
        return rootView;

    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
