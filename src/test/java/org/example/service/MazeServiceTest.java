package org.example.service;

import org.example.domain.Cell;
import org.example.domain.Maze;
import org.example.repository.MazeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.HashSet;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MazeServiceTest {

    @Mock
    private MazeRepository mazeRepository;

    @InjectMocks
    private MazeService mazeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGeneratePerfectMaze() {
        int rows = 10;
        int cols = 10;
        // When saving, return the same maze instance.
        when(mazeRepository.save(any(Maze.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Maze maze = mazeService.generatePerfectMaze(rows, cols);

        assertNotNull(maze, "Maze should not be null");
        assertEquals(rows, maze.getMazeRow(), "Maze row count should be " + rows);
        assertEquals(cols, maze.getMazeCol(), "Maze col count should be " + cols);

        assertNotNull(maze.getSteps(), "Solution steps should not be null");
        assertFalse(maze.getSteps().isEmpty(), "Solution steps should not be empty");
        assertEquals(new Cell(0, 0), maze.getSteps().get(0), "Solution must start at (0,0)");
        assertEquals(new Cell(rows - 1, cols - 1), maze.getSteps().get(maze.getSteps().size() - 1),
                "Solution must end at (" + (rows - 1) + "," + (cols - 1) + ")");

        verify(mazeRepository, times(1)).save(maze);
    }

    @Test
    public void testGeneratePerfectMazeHasUniqueSolution() {
        int rows = 10;
        int cols = 10;

        when(mazeRepository.save(any(Maze.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Maze maze = mazeService.generatePerfectMaze(rows, cols);

        // Build a grid of booleans representing free cells.
        // A cell is free if it is NOT in the obstacles set.
        boolean[][] free = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                free[i][j] = !maze.getObstacles().contains(new Cell(i, j));
            }
        }
        // Use DFS to count all distinct paths from (0,0) to (rows-1, cols-1).
        boolean[][] visited = new boolean[rows][cols];
        int pathCount = countPaths(maze, free, 0, 0, visited);
        assertEquals(1, pathCount, "There should be exactly one solution path in the generated maze.");
    }

    /**
     * Helper method to count the number of distinct paths from (r, c) to the exit
     * using DFS on a grid where a cell is free if free[r][c] is true.
     */
    private int countPaths(Maze maze, boolean[][] free, int r, int c, boolean[][] visited) {
        // If we've reached the exit cell, count one solution.
        if (r == maze.getMazeRow() - 1 && c == maze.getMazeCol() - 1) {
            return 1;
        }
        visited[r][c] = true;
        int count = 0;
        int[] dr = {-1, 0, 1, 0};
        int[] dc = {0, 1, 0, -1};
        for (int d = 0; d < 4; d++) {
            int nr = r + dr[d];
            int nc = c + dc[d];
            if (nr >= 0 && nr < maze.getMazeRow() && nc >= 0 && nc < maze.getMazeCol()
                    && free[nr][nc] && !visited[nr][nc]) {
                count += countPaths(maze, free, nr, nc, visited);
            }
        }
        visited[r][c] = false; // backtrack
        return count;
    }

    @Test
    public void testGetMazeById() {
        Maze maze = new Maze(10, 10, new HashSet<>(), null);
        when(mazeRepository.findById(any(Long.class))).thenReturn(Optional.of(maze));

        Optional<Maze> result = mazeService.getMazeById(1L);
        assertTrue(result.isPresent(), "Maze should be present");
        assertEquals(maze, result.get(), "Retrieved maze should match the expected maze");

        verify(mazeRepository, times(1)).findById(1L);
    }
}

