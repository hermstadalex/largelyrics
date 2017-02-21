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
        String genius_id;

        genius_id = client.getArtistId(name);

        if(genius_id.equals("")) {
            return "notfound";
        }

        Artist artist;

        try {

            artist = artistDao.findByGeniusId(genius_id);

            if(artist.getAnnotations() == null || artist.getLyrics() == null || artist.getLyrics().equals("")) {
                artistDao.delete(artist);

                throw new IndexOutOfBoundsException();
            }

            model.addAttribute("annotations", artist.getAnnotations());
            model.addAttribute("lyrics", artist.getLyrics());
            model.addAttribute("name", artist.getName());
        }
        catch (Exception ex) {
            System.out.println(ex);
            String annotations;
            String lyrics;

            ArrayList<String> artistIds = client.getArtistSongIds(genius_id);
            annotations = client.getAllSongAnnotations(artistIds, genius_id);
            lyrics = client.getLyrics(name);

            artist = new Artist(name, annotations, genius_id, lyrics);
            artistDao.save(artist);

            model.addAttribute("annotations", artist.getAnnotations());
            model.addAttribute("lyrics", artist.getLyrics());
            model.addAttribute("name", artist.getName());
        }



        return "artist";
    }

    /**
     * GET /create  --> Create a new artist and save it in the database.
     */
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/test")
    public String test() {
        return "test";
    }
}
