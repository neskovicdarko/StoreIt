package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import model.Lot;

public interface LotRepository extends JpaRepository<Lot, Integer>, LotRepoSpecific{

}
