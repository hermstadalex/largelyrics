package com.largelyrics;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alex on 2/2/17.
 */
public final class GeniusApiClient {
    private final OkHttpClient client = new OkHttpClient();
    private final String token = "Bearer 4mfDBVzCnp2S1Fc0l0K0cfqOrQYjRrb-OHi8W1f-PPU7LNLI6-cXY2E727-1gHYR";
    private final String baseUrl = "https://api.genius.com";
    private final String searchEndpoint = "/search?q=";
    private final String artistEndpoint = "/artists/";
    private final String songEndpoint = "/songs?per_page=";
    private final int songsPerPage = 50;
    private final String songReferentEndpoint = "/referents?song_id=";

    private final JSONObject run(String url) throws Exception {
        Request newRequest = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token)
                .build();

        try (Response response = client.newCall(newRequest).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(response.body().string());

        }
    }

    private JSONObject artistIdResponse(String artistName) {
        try {
            return run(baseUrl + searchEndpoint + URLEncoder.encode(artistName, "UTF-8"));
        }
        catch(Exception ignored) {
            return null;
        }
    }

    private JSONObject artistSongResponse(String artistId) {
        try {
            return run( baseUrl + artistEndpoint + artistId + songEndpoint + songsPerPage);
        }
        catch(Exception ignored) {
            return null;
        }
    }

    private JSONObject songReferentResponse(String songId) {
        try {
            return run(baseUrl + songReferentEndpoint + songId);
        }
        catch(Exception ignored) {
            return null;
        }
    }

    public ArrayList<String> getArtistSongIds(String artistId) {
        JSONObject response = artistSongResponse(artistId);

        ArrayList<String> songIds = new ArrayList<String>();

        try {
            JSONArray songList = response.getJSONObject("response")
                    .getJSONArray("songs");

            for( int i = 0; i < songList.length(); i++) {
                songIds.add(songList.getJSONObject(i).get("id").toString());
            }

            return songIds;
        }
        catch(JSONException e) {
            return null;
        }
    }


    public String getArtistId(String artistName) {

        JSONObject response = artistIdResponse(artistName);

        try {
            return response.getJSONObject("response")
                    .getJSONArray("hits")
                    .getJSONObject(0)
                    .getJSONObject("result")
                    .getJSONObject("primary_artist")
                    .get("id").toString();
        }
        catch(JSONException e) {
            return "Error has occured";
        }
    }

    public JSONArray getSongReferents(String songId) {
        JSONObject response = songReferentResponse(songId);
        try {
            return response.getJSONObject("response")
                    .getJSONArray("referents");
        }
        catch(JSONException e) {
            return null;
        }
    }

    public String getAnnotationText(JSONObject referent) {
        try {

        }
        catch( Exception e ) {
            return "";
        }
    }

    public ArrayList<String> getReferentAnnotations(JSONArray songReferents) {

        ArrayList<String> annotations = new ArrayList<String>();

        for(int i = 0; i < songReferents.length(); i++) {
            try {
                JSONObject referent = songReferents.getJSONObject(i);
                annotations.add(getAnnotationText(referent));
            }
            catch(Exception e) {
                continue;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        GeniusApiClient geniusApiClient = new GeniusApiClient();
        String artistId = (geniusApiClient.getArtistId("Kendrick Lamar"));
        ArrayList<String> artistSongIds =  geniusApiClient.getArtistSongIds(artistId);

    }
}