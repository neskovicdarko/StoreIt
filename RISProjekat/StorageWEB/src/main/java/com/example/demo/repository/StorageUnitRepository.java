package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import model.StorageUnit;

public interface StorageUnitRepository extends JpaRepository<StorageUnit, Integer>, StorageUnitRepoSpecific{

}
