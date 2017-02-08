package com.largelyrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

/**
 * Created by alex on 2/6/17.
 */
@Controller
public class ArtistController {

    @Autowired
    private GeniusApiClient client;

    @Autowired
    private ArtistDao artistDao;

    /**
     * GET /create  --> Create a new artist and save it in the database.
     */
    @RequestMapping("/artist")
    @ResponseBody
    public String create(String name) {
        String userId = "";
        String genius_id = client.getArtistId(name);

        try {
            Artist artist = artistDao.findByGeniusId(genius_id);

            return artist.getAnnotations();
        }
        catch (Exception ex) {
            String annotations;
            ArrayList<String> artistIds = client.getArtistSongIds(genius_id);
            annotations = client.getAllSongAnnotations(artistIds, genius_id);

            Artist artist = new Artist(name, annotations, genius_id);
            artistDao.save(artist);

            return artist.getAnnotations();
        }
    }
}
