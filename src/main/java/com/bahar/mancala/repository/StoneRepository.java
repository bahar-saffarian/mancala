package com.bahar.mancala.repository;

import com.bahar.mancala.model.Stone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoneRepository extends JpaRepository<Stone, Long> {
}
