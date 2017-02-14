package com.largelyrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * Created by alex on 2/6/17.
 */
@Controller
public class ViewController {

    @Autowired
    private GeniusApiClient client;

    @Autowired
    private ArtistDao artistDao;

    /**
     * GET /create  --> Create a new artist and save it in the database.
     */
    @RequestMapping("/artist")
    public String artist(String name, Model model) {
        String userId = "";
        String genius_id = client.getArtistId(name);

        try {
            Artist artist = artistDao.findByGeniusId(genius_id);

            model.addAttribute("annotations", artist.getAnnotations());
            model.addAttribute("name", artist.getName());

            return "artist";
        }
        catch (Exception ex) {
            String annotations;
            ArrayList<String> artistIds = client.getArtistSongIds(genius_id);
            annotations = client.getAllSongAnnotations(artistIds, genius_id);

            Artist artist = new Artist(name, annotations, genius_id);
            artistDao.save(artist);

            model.addAttribute("annotations", annotations);
            model.addAttribute("name", artist.getName());

            return "artist";
        }
    }

    /**
     * GET /create  --> Create a new artist and save it in the database.
     */
    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
