package com.bahar.mancala.repository;

import com.bahar.mancala.model.Pit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PitRepository extends JpaRepository<Pit, Long> {
}
