package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

}
