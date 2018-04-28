package cz.ruzickalukas.sensorsgames.treasure;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;
import android.widget.Toast;

import cz.ruzickalukas.sensorsgames.R;

public class Navigation implements LocationListener {

    private GameTreasureActivity currentActivity;

    private TextView instructions;
    private LocationManager mLocationManager;
    private String provider;
    private Criteria criteria = new Criteria();
    private boolean noDefaultLocation = true;

    private Location currentLocation;
    private Location[] targetLocations = new Location[3];
    private int discovered = 0;

    Navigation(TextView instructions, GameTreasureActivity currentActivity) {
        this.instructions = instructions;
        this.currentActivity = currentActivity;
        mLocationManager = (LocationManager)
                currentActivity.getSystemService(Context.LOCATION_SERVICE);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        provider = mLocationManager.getBestProvider(criteria, true);
        currentLocation = mLocationManager.getLastKnownLocation(provider);
        if (currentLocation != null) {
            noDefaultLocation = false;
            initTargetLocations();
        } else {
            instructions.setText(currentActivity.getResources()
                    .getString(R.string.instructions_not_available));
        }
    }

    private void initTargetLocations() {
        Location target1 = new Location("");
        target1.setLatitude(currentLocation.getLatitude() + 0.0004d);
        target1.setLongitude(currentLocation.getLongitude() + (0.0004d
                * Math.cos(currentLocation.getLatitude())));
        Location target2 = new Location("");
        target2.setLatitude(currentLocation.getLatitude() + 0.0002d);
        target2.setLongitude(currentLocation.getLongitude() - 0.0003d
                * Math.cos(currentLocation.getLatitude()));
        Location target3 = new Location("");
        target3.setLatitude(currentLocation.getLatitude() + 0.0002d);
        target3.setLongitude(currentLocation.getLongitude());

        targetLocations[0] = target1;
        targetLocations[1] = target2;
        targetLocations[2] = target3;

        changeInstructions();
    }

    void register() {
        provider = mLocationManager.getBestProvider(criteria, true);
        mLocationManager.requestLocationUpdates(provider, 0, 0, this);
    }

    void unregister() {
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        if (noDefaultLocation) {
            noDefaultLocation = false;
            initTargetLocations();
        } else {
            changeInstructions();
        }
    }

    private void changeInstructions() {
        float distance = currentLocation.distanceTo(targetLocations[discovered]);
        if (distance < 1) {
            if (discovered == 2) {
                Toast.makeText(currentActivity, currentActivity.getResources()
                        .getString(R.string.final_target_achieved), Toast.LENGTH_SHORT).show();
                unregister();
                currentActivity.finalTargetAchieved();
                return;
            } else {
                Toast.makeText(currentActivity, currentActivity.getResources()
                        .getString(R.string.target_achieved), Toast.LENGTH_SHORT).show();
                discovered++;
                distance = currentLocation.distanceTo(targetLocations[discovered]);
            }
        }

        String direction = getCardinalDirection(currentLocation
                .bearingTo(targetLocations[discovered]));

        instructions.setText(String.format(currentActivity.getResources()
                        .getString(R.string.instructions), (int)distance, direction));
    }

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {
        setNewProvider();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (status == LocationProvider.OUT_OF_SERVICE ||
                status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
            setNewProvider();
        }
    }

    private void setNewProvider() {
        mLocationManager.removeUpdates(this);
        provider = mLocationManager.getBestProvider(criteria, true);
        mLocationManager.requestLocationUpdates(provider,0,0,this);
    }

    private String getCardinalDirection(float bearing) {
        if (bearing >= 337.5 || bearing < 22.5) {
            return currentActivity.getResources().getString(R.string.north);
        } else if (bearing < 67.5) {
            return currentActivity.getResources().getString(R.string.north_east);
        } else if (bearing < 112.5) {
            return currentActivity.getResources().getString(R.string.east);
        } else if (bearing < 157.5) {
            return currentActivity.getResources().getString(R.string.south_east);
        } else if (bearing < 202.5) {
            return currentActivity.getResources().getString(R.string.south);
        } else if (bearing < 247.5) {
            return currentActivity.getResources().getString(R.string.south_west);
        } else if (bearing < 292.5) {
            return currentActivity.getResources().getString(R.string.west);
        } else if (bearing < 337.5) {
            return currentActivity.getResources().getString(R.string.north_west);
        } else {
            return currentActivity.getResources().getString(R.string.no_direction);
        }
    }
}
