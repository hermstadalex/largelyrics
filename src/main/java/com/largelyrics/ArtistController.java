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
    @RequestMapping("/create")
    @ResponseBody
    public String create(String name) {
        String userId = "";
        try {
            String genius_id = client.getArtistId(name);

            String annotations;
            ArrayList<String> artistIds = client.getArtistSongIds(genius_id);
            annotations = client.getAllSongAnnotations(artistIds, genius_id);

            Artist artist = new Artist(name, annotations, genius_id);
            artistDao.save(artist);
            userId = String.valueOf(artist.getId());
        }
        catch (Exception ex) {
            return "Error creating the artist: " + ex.toString();
        }
        return "User succesfully created with id = " + userId;
    }

    /**
     * GET /get-by-email  --> Return the id for the user having the passed
     * email.
     */
    @RequestMapping("/get")
    @ResponseBody
    public String getByEmail(String genius_id) {
        String artistId = "";
        try {
            Artist artist = artistDao.findByGeniusId(genius_id);
            artistId = String.valueOf(artist.getId());
            return artist.getAnnotations();
        }
        catch (Exception ex) {
            return "User not found";
        }
    }

}
