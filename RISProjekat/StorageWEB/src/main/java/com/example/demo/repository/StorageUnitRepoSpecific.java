package com.example.demo.repository;

import model.Lot;

public interface StorageUnitRepoSpecific {
	public boolean addUnits(Lot l);
	public void addAdditionalUnits(Lot l, Integer num);
}
