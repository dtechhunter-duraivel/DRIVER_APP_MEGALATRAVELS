package com.megalatravels.duraivel.cabbooking;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    GoogleMap map;
    LocationManager locationManager;
    GoogleApiClient googleApiClient;
    double lat;
    double lan;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String ipos;
    String categ;
    Button con;
    TextView t1;
    PlaceAutocompleteFragment autocompleteFragment;
    @SuppressLint("ResourceAsColor")
    @Override

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

con=(Button)findViewById(R.id.n1);
     //   Intent intent = getIntent();
       // Bundle extras = intent.getExtras();
        ipos = "0";
        categ ="0";
//Toast.makeText(getApplicationContext(),ipos,Toast.LENGTH_SHORT).show();
  //      Toast.makeText(getApplicationContext(),categ,Toast.LENGTH_SHORT).show();
//        continu=findViewById(R.id.cnt);
        final View parentLayout = findViewById(android.R.id.content);
        t1 =(TextView)findViewById(R.id.t1);

/*continu.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

    }
});*/AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder().setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry("IN")
                .build();
con.setVisibility(View.GONE);
       autocompleteFragment = (PlaceAutocompleteFragment)
               getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        LatLng southwestLatLng= new LatLng(10.736344,78.615761);
        LatLng northeastLatLng= new LatLng(10.895345,78.762295);
       autocompleteFragment.setBoundsBias(new LatLngBounds(southwestLatLng, northeastLatLng));
        ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(15);
        ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setHint("FROM LOCATION");
        ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setBackgroundResource(R.drawable.searchb);
        ((ImageButton)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button)).setVisibility(View.GONE);

        autocompleteFragment.setFilter(autocompleteFilter);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                ((ImageButton)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button)).setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), place.getName(), Toast.LENGTH_LONG).show();
                rgeocode(place.getLatLng());
               // Toast.makeText(getApplicationContext(),String.valueOf(lat)+String.valueOf(lan),Toast.LENGTH_LONG).show();
                LatLng currentLocation = new LatLng(lat, lan);
              con.setVisibility(View.VISIBLE);

                final double clat=place.getLatLng().latitude;
                final double clon=place.getLatLng().longitude;
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(place.getLatLng());
                markerOptions.title("Pickup Point");
                map.clear();
                map.addMarker(markerOptions);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude), 14.0f));
              con.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Intent i = new Intent(MainActivity.this,Main2Activity.class);
                      Bundle extras = new Bundle();
                      extras.putString("Latitude",String.valueOf(clat));
                      extras.putString("Longitude",String.valueOf(clon));
                      extras.putString("pos",ipos);
                      extras.putString("categ",categ);
                      extras.putString("pickloc",place.getName().toString());
                      i.putExtras(extras);
                      startActivity(i);

                  }
              });
                Snackbar snackbar = Snackbar
                        .make(parentLayout, "PICK UP LOCATION: "+place.getName(), Snackbar.LENGTH_INDEFINITE).setActionTextColor(Color.WHITE)
                        .setAction("CONTINUE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                            }
                        });

              //  snackbar.show();

            }

            @Override
            public void onError(Status status) {

            }
        });
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
       // map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

       locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
          //  Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1,1, this);




    }


    void rgeocode(LatLng l)
    {
        lat = l.latitude;
        lan = l.longitude;
        //Toast.makeText(getApplicationContext(),String.valueOf(lat),Toast.LENGTH_SHORT);
      /*  Geocoder geocoder;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            String locationName =loc;
            List<Address> addressList = geocoder.getFromLocationName(locationName, 5);
            if (addressList != null && addressList.size() > 0) {
                lat = (double) (addressList.get(0).getLatitude());
                lan = (double) (addressList.get(0).getLongitude());
                 }
        } catch (IOException e) {
            e.printStackTrace();
        }*/


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng currentLocation = new LatLng(10.7905, 78.7047);
        LatLng endLocation = new LatLng(10.7905, 78.7047);
         MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(endLocation);
        markerOptions.title("Tiruchirappalli,Tamilnadu,India.");
        map.addMarker(markerOptions);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(endLocation.latitude, endLocation.longitude), 14.0f));

        float[] results = new float[1];
        //Toast.makeText(getApplicationContext(), String.valueOf(SphericalUtil.computeDistanceBetween(currentLocation, endLocation)),Toast.LENGTH_LONG).show();
       // double distance;


    }




void setmyloc(double lati,double longi)
{
    Geocoder geocoder;
    List<Address> addresses;
    geocoder = new Geocoder(this, Locale.getDefault());

    try {
        addresses = geocoder.getFromLocation(lati, longi, 5); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        // Toast.makeText(getApplicationContext(),city,Toast.LENGTH_SHORT).show();
    }
    catch ( Exception e)
    {


    }
}




    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius=6371;//radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= Radius*c;
        double km=valueResult/1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec =  Integer.valueOf(newFormat.format(km));
        double meter=valueResult%1000;
        int  meterInDec= Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value",""+valueResult+"   KM  "+kmInDec+" Meter   "+meterInDec);

        return Radius * c;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        //setmyloc(location.getLatitude(),location.getLongitude());
        // map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17.));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {


    }

}