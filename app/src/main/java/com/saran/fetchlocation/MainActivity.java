package com.saran.fetchlocation;

import android.Manifest;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.saran.fetchlocation.location.CurrentLocationProvider;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static pub.devrel.easypermissions.EasyPermissions.hasPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn =(Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CurrentLocationProvider(MainActivity.this).checkGpsSettings(new CurrentLocationProvider.OnGPSSettingsChangeListener() {
                    @Override
                    public void onGPSEnabled() {

                        if (hasPermissions(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION))
                        {
                            getCurrentLocation();
                        }else {
                            EasyPermissions.requestPermissions(MainActivity.this,"Location need",100,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION);
                        }

                    }
                });


            }});
    }



    private void getCurrentLocation() {

        new CurrentLocationProvider(MainActivity.this).getCurrentLocation(new CurrentLocationProvider.OnCurrentLocationReceivedListener() {
            @Override
            public void onLocationReceived(Location location) {

                final double latitude = location.getLatitude();
                final double longitude = location.getLongitude();
                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    String countrycode = addresses.get(0).getCountryName();
                    String locality = addresses.get(0).getLocality();
                    Log.d("location_D", "onLocationReceived: " + countrycode + "  long: " + locality);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

        getCurrentLocation();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

        new AppSettingsDialog.Builder(this).build().show();

    }
}
