package com.largelyrics;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Created by alex on 2/6/17.
 */
@Transactional
@Repository
public interface ArtistDao extends CrudRepository<Artist, Long> {
    public Artist findByGeniusId(String geniusId);
}
