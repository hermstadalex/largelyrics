package com.largelyrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by alex on 2/6/17.
 */
@Controller
public class ArtistController {

    /**
     * GET /create  --> Create a new user and save it in the database.
     */
    @RequestMapping("/create")
    @ResponseBody
    public String create(String name, String annotations, String genius_id) {
        String userId = "";
        try {
            genius_id = genius_id.replaceAll("/$", "");
            Artist artist = new Artist(name, annotations, genius_id);
            artistDao.save(artist);
            userId = String.valueOf(artist.getId());
        }
        catch (Exception ex) {
            return "Error creating the artist: " + ex.toString();
        }
        return "User succesfully created with id = " + userId;
    }



    @Autowired
    private ArtistDao artistDao;

}
