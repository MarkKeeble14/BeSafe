package com.example.besafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings.Secure;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Adapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    public static final String OTP_REGEX = "[0-9]{1,6}";

    /* __________________________________*/
    private int stage = 0;
    private int count = 0;
    /* __________________________________*/

    private Location currentBestLocation = null;
    protected LocationManager locationManager;
    protected LocationListener locationListener;

    private FirebaseDatabase database;

    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    TextView latTextView, lonTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SmsReceiver.bindListener(new SmsListener() {

            @Override
            public void messageReceived(String messageText) {
                //From the received text string you may do string operations to get the required OTP
                //It depends on your SMS format
                Log.e("Text", messageText);
                Toast.makeText(MainActivity.this,"Message: " + messageText,Toast.LENGTH_LONG).show();

                /* __________________________________*/
                boolean help;
                // boolean safe;
                // int floor;
                /* __________________________________*/

                switch (messageText) {
                    case "1": sendSMS();
                    help = true;
                    break;
                    case "2": sendSMS();
                    help = false;
                    break;
                }
            }
        });
        setContentView(R.layout.activity_main);

        latTextView = findViewById(R.id.latTextView);
        lonTextView = findViewById(R.id.longTextView);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        if (checkPermissions()) {
            sendSMS();
        } else {
            requestPermissions();
        }
    }

    public void sendSMS() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://besafe-55c8d.firebaseio.com/");
        DatabaseReference myRef = database.getReference("User");

        // Read FireBase
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Snapshot
                String re = dataSnapshot.child("phoneNo").getValue(String.class);

                // Send Text
                if (re.length() != 0) {
                    SmsManager sms = SmsManager.getSmsManagerForSubscriptionId(10);
                    int count = getCount();
                    switch (count) {
                        case 0: sms.sendTextMessage(re, null, "Do you need help?:\n1) Yes\n2) No", null, null);
                        break;
                        case 1: sms.sendTextMessage(re, null, "Are You Currently in a Safe Space?:\n1) Yes\n2) No", null, null);
                        break;
                        case 2: sms.sendTextMessage(re, null, "Which floor are you on?:\n(NUMBER): ", null, null);
                    }
                    incrementCount();
                    Log.d("Sent", "Message Sent to: " + re);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int getCount() {
        return count;
    }

    private void incrementCount() {
        count++;
    }

    @SuppressLint("MissingPermission")
    private String getPhoneNumber() {
        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = tMgr.getLine1Number();

        return phoneNumber;
    }

    private String getDeviceID() {
        String android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

        return android_id;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latTextView.setText(location.getLatitude()+"");
                                    lonTextView.setText(location.getLongitude()+"");

                                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://besafe-55c8d.firebaseio.com/");
                                    DatabaseReference myRef = database.getReference("User");

                                    UserLocation ul = new UserLocation(location.getLatitude(), location.getLongitude());

                                    User user = new User(getDeviceID(), getPhoneNumber(), ul, false);
                                    myRef.setValue(user);

                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latTextView.setText(mLastLocation.getLatitude()+"");
            lonTextView.setText(mLastLocation.getLongitude()+"");
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
            }
        }
    }
}

