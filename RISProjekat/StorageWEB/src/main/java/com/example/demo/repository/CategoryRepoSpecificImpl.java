package com.example.demo.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import model.Category;

@Repository
@Transactional 
public class CategoryRepoSpecificImpl implements CategoryRepoSpecific{

	@PersistenceContext
	EntityManager em;
	
	@Override
	public void persistCategory(Category cat) {
		em.persist(cat);	
	}

}
