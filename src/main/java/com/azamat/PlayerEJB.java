package com.azamat;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by azamat on 11/20/16.
 */

@Stateless
@Named
public class PlayerEJB {
    @PersistenceContext(unitName = "gamePU")
    private EntityManager em;

    public void createPlayer() {
        em.persist(new Player("testPlayer", 0, 0));
    }

}
