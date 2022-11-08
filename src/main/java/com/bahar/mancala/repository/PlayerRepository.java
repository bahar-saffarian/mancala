package com.bahar.mancala.repository;

import com.bahar.mancala.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
