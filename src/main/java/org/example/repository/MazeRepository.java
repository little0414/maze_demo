package org.example.repository;

import org.example.domain.Maze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MazeRepository extends JpaRepository<Maze, Long> {
}

