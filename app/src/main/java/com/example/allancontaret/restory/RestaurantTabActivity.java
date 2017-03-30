package com.example.allancontaret.restory;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class RestaurantTabActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    SharedPreferences settings;
    public static final String PREFS_RESTO = "MyPrefsFile";
    Restaurant restaurant;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getSupportActionBar().setElevation(4);
        restaurant = (Restaurant) getIntent().getSerializableExtra("MyClass");
        setTitle(restaurant.name);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.getCurrentItem();
        mViewPager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrollStateChanged(int state) {
                        //Called when the scroll state changes.
                    }

                    @Override
                    public void onPageScrolled(int position,
                                               float positionOffset, int positionOffsetPixels) {
                        //This method will be invoked when the current page is scrolled,
                        //either as part of a programmatically initiated smooth scroll
                        //or a user initiated touch scroll.
                    }

                    @Override
                    public void onPageSelected(int position) {
                        //This method will be invoked when a new page becomes selected.
                        Log.i("dddfgdfgdgf", String.valueOf(position));
                    }
                }
        );

        mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        settings = getSharedPreferences(PREFS_RESTO, 0);
        fab = (FloatingActionButton) findViewById(R.id.buttonFavoriteStar);
        favoriteButtonText(settings);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionToFavorites(settings, restaurant.name, restaurant.id);
            }
        });

    }

    private void favoriteButtonText(SharedPreferences settings) {
        if (!settings.contains(String.valueOf(restaurant.id))){
            fab.setImageResource(android.R.drawable.btn_star_big_off);
        } else {
            fab.setImageResource(android.R.drawable.btn_star_big_on);
        }
    }

    private void actionToFavorites(SharedPreferences settings, String keyName, int restaurantId) {
        String message;
        SharedPreferences.Editor editor = settings.edit();

        // verification si favoris existe
        if (!settings.contains(String.valueOf(restaurantId))){
            editor.putString(String.valueOf(restaurantId), keyName);
            message = getString(R.string.toast_add_favorite);
        } else {
            editor.remove(String.valueOf(restaurantId));
            message = getString(R.string.toast_rm_favorite);
        }

        // Commit the edits!
        editor.apply();
        favoriteButtonText(settings);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Restaurant restaurant = (Restaurant) getActivity().getIntent().getSerializableExtra("MyClass");
            Log.i("intent-p", String.valueOf(restaurant.name));
            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);

            TextView textRestoName = (TextView) rootView.findViewById(R.id.textRestoName);
            textRestoName.setText(restaurant.name);

            TextView textRestoDescription = (TextView) rootView.findViewById(R.id.textRestoDescription);
            textRestoDescription.setText(restaurant.description);

            TextView textRestoAddress = (TextView) rootView.findViewById(R.id.textRestoAddress);
            textRestoAddress.setText(restaurant.address);

            Glide.with(getActivity().getApplicationContext())
                    .load("http://api.gregoirejoncour.xyz/images/" + restaurant.image)
                    .fitCenter()
                    .into((ImageView) rootView.findViewById(R.id.imageViewRestaurant));

            return rootView;
        }
    }

    public static class MapViewFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

        double latitude;
        double longitude;
        Restaurant restaurant;
        MapView mMapView;
        private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
        private static GoogleMap googleMap;
        LatLng locationRestaurant;

        @Override
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                    if (/*grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED*/PermissionUtils.isPermissionGranted(permissions, grantResults,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Log.i("permission", "OK");
                        enableMyLocation();
                    } else {
                        Log.i("permission", "not OK");
                    }
                }
            }
        }

        /**
         * Enables the My Location layer if the fine location permission has been granted.
         */
        private void enableMyLocation() {
            // For showing a move to my location button
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
/*                PermissionUtils.requestPermission((AppCompatActivity) getActivity(), MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION, true);*/
            } else {
                Log.i("permission", "permission ok");
                googleMap.setMyLocationEnabled(true);
            }
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);

            if (isVisibleToUser) {
                enableMyLocation();
                resetCameraRestaurantLocation();
            } else Log.d("MyFragment", "Fragment is not visible.");
        }

        private void resetCameraRestaurantLocation() {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(locationRestaurant).zoom(18).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.location_fragment, container, false);

            mMapView = (MapView) rootView.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);

            mMapView.onResume(); // needed to get the map to display immediately
            restaurant = (Restaurant) getActivity().getIntent().getSerializableExtra("MyClass");
            Geocoder geocoder = new Geocoder(getContext());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocationName(restaurant.address, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (addresses != null) {
                latitude = addresses.get(0).getLatitude();
                longitude = addresses.get(0).getLongitude();
            }
            locationRestaurant = new LatLng(latitude, longitude);
            // image button select type map
            ImageButton map = (ImageButton) rootView.findViewById(R.id.buttonMapType);
            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.dialog_map_type_title)
                            .setSingleChoiceItems(
                                    new String[]{"Normal", "Satellite", "Terrain", "Hybrid"},
                                    (googleMap.getMapType() - 1),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int item) {
                                            switch (item) {
                                                case 0:
                                                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                                    break;
                                                case 1:
                                                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                                    break;
                                                case 2:
                                                    googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                                    break;
                                                case 3:
                                                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                                    break;
                                                default:
                                                    googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                                            }
                                            dialog.dismiss();
                                        }
                                    }
                            )
                            .show();
                }
            });
            ImageButton marker = (ImageButton) rootView.findViewById(R.id.buttonGoToMarker);
            marker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetCameraRestaurantLocation();
                }
            });

            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(46.227638, 2.213749)).zoom(5).build();
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                    googleMap.getUiSettings().setCompassEnabled(true);
                    googleMap.getUiSettings().setMapToolbarEnabled(true);
                    googleMap.getUiSettings().setZoomGesturesEnabled(true);
                    googleMap.getUiSettings().setScrollGesturesEnabled(true);
                    googleMap.getUiSettings().setTiltGesturesEnabled(true);
                    googleMap.getUiSettings().setRotateGesturesEnabled(true);
                    // For dropping a marker at a point on the Map
                    googleMap.addMarker(new MarkerOptions().position(locationRestaurant).title(restaurant.name).snippet(restaurant.address));
                }
            });

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            mMapView.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
            mMapView.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mMapView.onDestroy();
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
            mMapView.onLowMemory();
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 1) {
                return new MapViewFragment();
            } else {
                return PlaceholderFragment.newInstance(position + 1);
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Détails";
                case 1:
                    return "Carte";
            }
            return null;
        }
    }
}
