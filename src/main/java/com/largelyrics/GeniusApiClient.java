package com.largelyrics;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import 	org.apache.lucene.search.*;

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
    private final String songReferentEndpoint = "/referents?";
    private final String referentOptions = "per_page=50&text_format=plain&song_id=";

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
            return run(baseUrl + songReferentEndpoint + referentOptions + songId);
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
        NormalizedLevenshtein l = new NormalizedLevenshtein();

        try {
            JSONArray hitsArray = response.getJSONObject("response")
                    .getJSONArray("hits");

            for(int i = 0; i < hitsArray.length(); i++) {
                JSONObject currPrimaryArtist = hitsArray.getJSONObject(i)
                        .getJSONObject("result")
                        .getJSONObject("primary_artist");

                String currArtistName = currPrimaryArtist.get("name").toString();

                if(l.distance(currArtistName, artistName) < .7) {
                    return currPrimaryArtist.get("id").toString();
                }
            }

            return "No artists found by this name";
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

    /**
     * Loops through all the annotations for a single referent, and grabs their text
     *
     * @param referent The referent (part of document being annotated) to grab the annotation text from
     * @return A single string with all of the referent's annotation text.
     */
    public String getAnnotationText(JSONObject referent) {
        try {
            String annotation = "";
            JSONArray annotationList = referent.getJSONArray("annotations");
            for (int i = 0; i < annotationList.length(); i++) {
                annotation += annotationList.getJSONObject(i).getJSONObject("body").get("plain").toString();
            }

            return annotation;
        }
        catch( Exception e ) {
            return "";
        }
    }

    /**
     *
     * @param songReferents
     * @return
     */
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

        return annotations;
    }

    public static void main(String[] args) throws Exception {
        GeniusApiClient geniusApiClient = new GeniusApiClient();
        String artistId = (geniusApiClient.getArtistId("ugly duckling"));
        ArrayList<String> artistSongIds =  geniusApiClient.getArtistSongIds(artistId);

        JSONArray referents  = geniusApiClient.getSongReferents(artistSongIds.get(0));
        System.out.println(geniusApiClient.getReferentAnnotations(referents));

    }
}