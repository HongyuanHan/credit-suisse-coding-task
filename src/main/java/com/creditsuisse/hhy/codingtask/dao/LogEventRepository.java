package com.creditsuisse.hhy.codingtask.dao;

import com.creditsuisse.hhy.codingtask.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogEventRepository extends JpaRepository<Event, String> {

}
