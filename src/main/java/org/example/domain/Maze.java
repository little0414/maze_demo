package org.example.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class Maze {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Renamed fields to avoid reserved keywords.
    @Column(name = "maze_row", nullable = false)
    private int mazeRow;

    @Column(name = "maze_col", nullable = false)
    private int mazeCol;

    // For the obstacles collection, override the column names for the embedded Cell.
    @ElementCollection
    @CollectionTable(name = "maze_obstacles", joinColumns = @JoinColumn(name = "maze_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "row", column = @Column(name = "cell_row", nullable = false)),
            @AttributeOverride(name = "col", column = @Column(name = "cell_col", nullable = false))
    })
    private Set<Cell> obstacles = new HashSet<>();

    // For the steps collection, override the embedded Cell column names as well.
    @ElementCollection
    @CollectionTable(name = "maze_steps", joinColumns = @JoinColumn(name = "maze_id"))
    @OrderColumn(name = "step_order")
    @AttributeOverrides({
            @AttributeOverride(name = "row", column = @Column(name = "cell_row", nullable = false)),
            @AttributeOverride(name = "col", column = @Column(name = "cell_col", nullable = false))
    })
    private List<Cell> steps;

    private LocalDateTime createdAt;

    public Maze() { }

    public Maze(int mazeRow, int mazeCol, Set<Cell> obstacles, List<Cell> steps) {
        this.mazeRow = mazeRow;
        this.mazeCol = mazeCol;
        this.obstacles = obstacles;
        this.steps = steps;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters.
    public Long getId() {
        return id;
    }
    public int getMazeRow() {
        return mazeRow;
    }
    public int getMazeCol() {
        return mazeCol;
    }
    public Set<Cell> getObstacles() {
        return obstacles;
    }
    public List<Cell> getSteps() {
        return steps;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setMazeRow(int mazeRow) {
        this.mazeRow = mazeRow;
    }
    public void setMazeCol(int mazeCol) {
        this.mazeCol = mazeCol;
    }
    public void setObstacles(Set<Cell> obstacles) {
        this.obstacles = obstacles;
    }
    public void setSteps(List<Cell> steps) {
        this.steps = steps;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
