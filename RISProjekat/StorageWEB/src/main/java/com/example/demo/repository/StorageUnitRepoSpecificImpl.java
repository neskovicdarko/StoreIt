package com.example.demo.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import model.Lot;
import model.StorageUnit;

@Repository
@Transactional (noRollbackFor = Exception.class)
public class StorageUnitRepoSpecificImpl implements StorageUnitRepoSpecific {

	@PersistenceContext
	EntityManager em;

	@Override
	public boolean addUnits(Lot l) {
		try {

			for (int i = 1; i <= l.getNumberOfStorageUnits(); i++) {

				StorageUnit su = new StorageUnit();
				su.setLot(l);
				su.setUnitNum(i);
				em.persist(su);
			}

		} catch (Exception e) {
			System.out.println("OVO JE GRESKA!");
			e.printStackTrace();
			System.out.println("OVO JE GRESKA!");
			return false;
		}
		return true;
	}

	@Override
	public void addAdditionalUnits(Lot l, Integer num) {
		
		int oldNumberOfStorageUnits = l.getNumberOfStorageUnits();
		for (int i =  1; i <= num; i++) {
			StorageUnit su = new StorageUnit();
			su.setLot(l);
			su.setUnitNum(i + oldNumberOfStorageUnits);
			em.persist(su);
		}
		
		l = em.find(Lot.class, l.getLotId());
		l.setNumberOfStorageUnits(num + oldNumberOfStorageUnits);
		em.merge(l);
	}

}
