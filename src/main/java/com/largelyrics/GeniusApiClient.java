package com.largelyrics;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class GeniusApiClient {
    private OkHttpClient client = new OkHttpClient();

    private final String token = "Bearer 4mfDBVzCnp2S1Fc0l0K0cfqOrQYjRrb-OHi8W1f-PPU7LNLI6-cXY2E727-1gHYR";
    private final String baseUrl = "https://api.genius.com";
    private final String searchEndpoint = "/search?q=";
    private final String artistEndpoint = "/artists/";
    private final String songEndpoint = "/songs?per_page=";
    private final int songsPerPage = 50;
    private final String songReferentEndpoint = "/referents?";
    private final String referentOptions = "per_page=50&text_format=plain&song_id=";

    String apiKey = "e7344163d32efb5558f76b363c713bfd";
    MusixMatch musixMatch = new MusixMatch(apiKey);



    private JSONObject run(String url) throws Exception {
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
            System.out.println(artistName);
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

                System.out.println(currArtistName);

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

    public String getLyrics(String artistName) {

        String lyricsStr = "";

        try {
            List<Track> tracks = musixMatch.searchTracks("", artistName, "", 1, 200, true);

            for (Track trk : tracks) {
                try {
                    TrackData trkData = trk.getTrack();

                    int id = trkData.getTrackId();

                    Lyrics lyrics = musixMatch.getLyrics(id);
                    lyricsStr += lyrics.getLyricsBody();
                }
                catch(Exception e) {
                    continue;
                }

            }

            return lyricsStr;

        }
        catch(Exception e) {
            return lyricsStr;
        }
    }

    /**
     *
     * @param songReferents
     * @return
     */
    private String getReferentAnnotations(JSONArray songReferents) {

        String annotations = "";

        for(int i = 0; i < songReferents.length(); i++) {
            try {
                JSONObject referent = songReferents.getJSONObject(i);
                annotations += getAnnotationText(referent);
            }
            catch(Exception e) {
                continue;
            }
        }

        return annotations;
    }


    public String getAllSongAnnotations(ArrayList<String> songIds, String artistId) {
        GeniusApiClient geniusClient = new GeniusApiClient();
        ArrayList<String> annotationList = new ArrayList<String>();

        for (String songId : songIds) {
            annotationList.add(geniusClient.getReferentAnnotations(geniusClient.getSongReferents(songId)));
        }

        String annotationString = squashAnnotationArray(annotationList);

        return annotationString;

    }

    private String squashAnnotationArray(ArrayList<String> annotations) {

        String annotationString = "";

        for( String annotation : annotations ) {
            annotationString += annotation;
        }

        return annotationString;
    }
}