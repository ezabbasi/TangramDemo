package com.tpl.secondtangramdemo;

import android.util.Log;

import com.mapzen.tangram.HttpHandler;
import com.squareup.okhttp.Callback;

import java.io.File;

/**
 * Created by Ahsan-TPL on 2/14/2017.
 */

public class CustomHttpHandler extends HttpHandler {

    public CustomHttpHandler() {
        super();
    }

    @Override
    public boolean onRequest(String url, Callback cb) {


        if(url.contains("https://tile.mapzen.com/")){
            Log.e("New Request:::", "New Request- "+url);
            int zoomLevel =getZoomLevel(url);

            Log.e("Zoom", "zoom   :"+zoomLevel);
            if(zoomLevel>=13 && zoomLevel<=18 ){
                return super.onRequest(url, cb);
            }else{
                return false;
            }
        }else {
            return super.onRequest(url, cb);
        }

    }

    @Override
    public void onCancel(String url) {
        super.onCancel(url);
    }

    @Override
    public boolean setCache(File directory, long maxSize) {
        return super.setCache(directory, maxSize);
    }

    private int getZoomLevel(String url){
             String[] splitedString =  url.split("/");
             int num =Integer.parseInt(splitedString[7]);
            return num;

    }
}
