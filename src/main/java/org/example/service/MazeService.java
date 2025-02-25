package org.example.service;

import org.example.domain.Cell;
import org.example.domain.Maze;
import org.example.repository.MazeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.lang.Math.min;

@Service
public class MazeService {

    @Autowired
    private MazeRepository mazeRepository;

    // Static direction arrays for up, right, down, left.
    private static final int[] DR = {-1, 0, 1, 0};
    private static final int[] DC = {0, 1, 0, -1};

    /**
     * Generates a perfect maze with a unique solution path and extra dead-end branches.
     * Free cells are determined by carving out a unique solution path and then carving branches.
     */
    public Maze generatePerfectMaze(int row, int col) {
        boolean[][] free = new boolean[row][col];
        Random rand = new Random();

        // Generate the unique solution path by moving right or down.
        List<Cell> solutionPath = generateSolutionPath(row, col, free, rand);

        // Carve extra dead-end branches from the solution path cells.
        for (Cell cell : solutionPath) {
            if (rand.nextDouble() < 0.8) { // 80% chance to attempt a branch
                carveBranch(cell, row, col, free, solutionPath, rand);
            }
        }

        // Mark as obstacles every cell that is not free.
        Set<Cell> obstacles = new HashSet<>();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (!free[i][j]) {
                    obstacles.add(new Cell(i, j));
                }
            }
        }
        Maze maze = new Maze(row, col, obstacles, solutionPath);
        mazeRepository.save(maze);
        return maze;
    }

    public Optional<Maze> getMazeById(Long id) {
        return mazeRepository.findById(id);
    }

    /**
     * Generates the unique solution path by moving right or down, starting from (0,0)
     * and ending at (row-1, col-1). It marks the corresponding cells as free.
     */
    private List<Cell> generateSolutionPath(int row, int col, boolean[][] free, Random rand) {
        List<Cell> solutionPath = new ArrayList<>();
        int r = 0, c = 0;
        solutionPath.add(new Cell(r, c));
        free[r][c] = true;
        while (r != row - 1 || c != col - 1) {
            if (r == row - 1) {
                c++;
            } else if (c == col - 1) {
                r++;
            } else {
                if (rand.nextBoolean()) {
                    c++;
                } else {
                    r++;
                }
            }
            solutionPath.add(new Cell(r, c));
            free[r][c] = true;
        }
        return solutionPath;
    }

    /**
     * Attempts to carve a branch from the given solution cell.
     * First obtains valid directions from the solution cell
     * If any are available, it picks one at random as the initial branch direction,
     * determines a random branch length, and then for each subsequent branch step
     * it picks a new valid direction randomly.
     */
    private void carveBranch(Cell cell, int row, int col, boolean[][] free, List<Cell> solutionPath, Random rand) {
        List<Integer> validDirs = getValidDirections(cell, row, col, free, solutionPath, cell);
        if (validDirs.isEmpty()) {
            return;
        }
        int initialDir = validDirs.get(rand.nextInt(validDirs.size()));
        int br = cell.getRow() + DR[initialDir];
        int bc = cell.getCol() + DC[initialDir];
        free[br][bc] = true;
        int branchLength = rand.nextInt(min(row, col)) + 1;
        for (int i = 1; i < branchLength; i++) {
            List<Integer> branchValidDirs = getValidDirections(new Cell(br, bc), row, col, free, solutionPath, new Cell(br, bc));
            if (branchValidDirs.isEmpty()) {
                break;
            }
            int chosenDir = branchValidDirs.get(rand.nextInt(branchValidDirs.size()));
            br += DR[chosenDir];
            bc += DC[chosenDir];
            free[br][bc] = true;
        }
    }

    /**
     * Returns a list of valid directions (0: up, 1: right, 2: down, 3: left) from a given cell.
     * A direction is valid if:
     *   - The neighbor cell is within bounds.
     *   - The neighbor cell is not already free.
     *   - The neighbor cell is not in the solution path.
     *   - The neighbor cell does not have any adjacent free cell (other than the specified exclude cell).
     */
    private List<Integer> getValidDirections(Cell cell, int row, int col, boolean[][] free, List<Cell> solutionPath, Cell exclude) {
        List<Integer> validDirs = new ArrayList<>();
        for (int d = 0; d < 4; d++) {
            int nr = cell.getRow() + DR[d];
            int nc = cell.getCol() + DC[d];
            if (nr >= 0 && nr < row && nc >= 0 && nc < col
                    && !free[nr][nc]
                    && !solutionPath.contains(new Cell(nr, nc))
                    && !hasAdjacentFreeCell(nr, nc, row, col, free, exclude)) {
                validDirs.add(d);
            }
        }
        return validDirs;
    }

    private boolean hasAdjacentFreeCell(int r, int c, int row, int col, boolean[][] free, Cell exclude) {
        for (int i = 0; i < 4; i++) {
            int nr = r + DR[i];
            int nc = c + DC[i];
            if (nr >= 0 && nr < row && nc >= 0 && nc < col) {
                if (free[nr][nc] && !(nr == exclude.getRow() && nc == exclude.getCol())) {
                    return true;
                }
            }
        }
        return false;
    }
}
