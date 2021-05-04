package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import model.Message;

public interface MessageRepository extends JpaRepository<Message, Integer> {

}
