package com.bol.mancala.repository;

import com.bol.mancala.model.Stone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoneRepository extends JpaRepository<Stone, Long> {
}
