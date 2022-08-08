package com.bol.mancala.repository;

import com.bol.mancala.model.Pit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PitRepository extends JpaRepository<Pit, Long> {
}
