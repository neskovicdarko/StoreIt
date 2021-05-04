package com.example.demo.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import model.Lot;

@Repository
@Transactional
public class LotRepoSpecificImpl implements LotRepoSpecific{

	@PersistenceContext
	EntityManager em;
	
	@Override
	public void persistLot(Lot l) {
		em.persist(l);
	}

}
