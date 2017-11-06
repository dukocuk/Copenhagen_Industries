package com.durankose.copenhagen_industries.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.durankose.copenhagen_industries.R;


public class DevicesFragment extends Fragment {

    /** Formaalet med TAG er at kunne fejlfinde hvis noget skulle gaa galt.
     *  Vi kan så proppe den TAG i en LOG.d og eks. få
     *  "D/DevicesFragment: Du er inde i Devices"
     */
    private final String TAG = this.getClass().getSimpleName();
    public DevicesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "Du er inde i Devices");
        View view = inflater.inflate(R.layout.fragment_devices, container, false);

        final String[] menuItems = {"Type 92 machine gun", "AG-043", "AK-47"};

        ListView listView = view.findViewById(R.id.list_view);


        class CustomAdapter extends BaseAdapter {

            @Override
            public int getCount() {
                return menuItems.length;
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View view1 = inflater.inflate(R.layout.list_items, null);

                TextView mTextView = (TextView) view1.findViewById(R.id.itemText);
                ImageView mImageView = (ImageView) view1.findViewById(R.id.itemImage);

                mTextView.setText(menuItems[i]);
                return view1;
            }

        }

        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment;

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    Toast.makeText(getActivity(), "clicked first weapon", Toast.LENGTH_SHORT).show();
                    fragment = new WeaponControlFragment();
                    fragmentTransaction.replace(R.id.layout_for_fragment, fragment, fragment.getTag()).commit();
                } else if (i == 1){
                    Toast.makeText(getActivity(), "clicked second weapon", Toast.LENGTH_SHORT).show();
                    fragment = new WeaponControlFragment();
                    fragmentTransaction.replace(R.id.layout_for_fragment, fragment, fragment.getTag()).commit();
                } else if (i == 2){
                    Toast.makeText(getActivity(), "clicked third weapon", Toast.LENGTH_SHORT).show();
                    fragment = new WeaponControlFragment();
                    fragmentTransaction.replace(R.id.layout_for_fragment, fragment, fragment.getTag()).commit();
                }
            }
        });

        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.devices_fragment));
    }



}
