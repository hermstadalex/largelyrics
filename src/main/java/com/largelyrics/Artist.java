package com.largelyrics;

import javax.persistence.*;

/**
 * Created by alex on 2/6/17.
 */
@Entity
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private String geniusId;


    @Lob
    private String annotations;

    public Artist() {}

    public Artist(String name, String annotations, String geniusId) {
        this.name = name;
        this.annotations = annotations;
        this.geniusId = geniusId;
    }

    public String getName() {
        return name;
    }
    public String getAnnotations() {
        return annotations;
    }
    public String getGeniusId() {
        return geniusId;
    }
    public long getId() {
        return id;
    }
}
