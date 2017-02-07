package com.largelyrics;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

/**
 * Created by alex on 2/6/17.
 */
@Transactional
public interface ArtistDao extends CrudRepository<Artist, Long> {
    public Artist findByGeniusId(String geniusId);
}
