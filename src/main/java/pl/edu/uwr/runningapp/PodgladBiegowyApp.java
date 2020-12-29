package pl.edu.uwr.runningapp;
import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;



public class PodgladBiegowyApp extends Application {
    private static PodgladBiegowyApp singleton;
    private List<Location> mLocations;

    public List<Location> getmLocations(){
        return mLocations;
    }
    public List<Location> setmLocations(){mLocations = new ArrayList<>();
    return mLocations;}


    public PodgladBiegowyApp getInstance(){
        return singleton;
    }

    public void onCreate(){
        super.onCreate();
        singleton = this;
        mLocations = new ArrayList<>();
    }
}
