package com.bahar.mancala.repository;

import com.bahar.mancala.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
