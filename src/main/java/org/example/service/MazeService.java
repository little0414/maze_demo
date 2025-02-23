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

    /**
     * Generates a perfect maze with a unique solution path and extra dead-end branches.
     * Free cells are determined by carving out a unique solution path and then carving branches.
     * When carving branches, if the surrounding of a candidate cell (except the branch's origin or previous cell)
     * contains any free cell, that candidate is excluded.
     * Additionally, for each step in a branch the direction is chosen randomly from the valid ones.
     */
    public Maze generatePerfectMaze(int row, int col) {
        boolean[][] free = new boolean[row][col];
        List<Cell> solutionPath = new ArrayList<>();
        Random rand = new Random();
        int r = 0, c = 0;
        solutionPath.add(new Cell(r, c));
        free[r][c] = true;
        // Generate the unique solution path by moving right or down.
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

        // Carve extra dead-end branches from the solution path cells.
        int[] dr = {-1, 0, 1, 0}; // up, right, down, left
        int[] dc = {0, 1, 0, -1};
        for (Cell cell : solutionPath) {
            if (rand.nextDouble() < 0.8) { // 80% chance to attempt a branch
                // Gather all valid initial directions from the solution cell.
                List<Integer> validDirs = new ArrayList<>();
                for (int d = 0; d < 4; d++) {
                    int nr = cell.getRow() + dr[d];
                    int nc = cell.getCol() + dc[d];
                    // Check in bounds, not already free, not on the solution path,
                    // and ensure no adjacent free cell (except the branch's origin cell).
                    if (nr >= 0 && nr < row && nc >= 0 && nc < col
                            && !free[nr][nc]
                            && !solutionPath.contains(new Cell(nr, nc))
                            && !hasAdjacentFreeCellExcluding(nr, nc, row, col, free, cell)) {
                        validDirs.add(d);
                    }
                }
                // If there are valid directions, pick one at random for the first branch cell.
                if (!validDirs.isEmpty()) {
                    int initialDir = validDirs.get(rand.nextInt(validDirs.size()));
                    int br = cell.getRow() + dr[initialDir];
                    int bc = cell.getCol() + dc[initialDir];
                    free[br][bc] = true;
                    // Determine branch length randomly (at most the minimum of rows and col).
                    int branchLength = rand.nextInt(min(row, col)) + 1;
                    // For each subsequent branch step, choose a new random direction among valid ones.
                    for (int i = 1; i < branchLength; i++) {
                        List<Integer> branchValidDirs = new ArrayList<>();
                        // Calculate valid directions for the current branch cell.
                        for (int d = 0; d < 4; d++) {
                            int nrr = br + dr[d];
                            int nrc = bc + dc[d];
                            // Here we exclude the immediate previous branch cell by using the current branch cell as "exclude".
                            if (nrr >= 0 && nrr < row && nrc >= 0 && nrc < col
                                    && !free[nrr][nrc]
                                    && !solutionPath.contains(new Cell(nrr, nrc))
                                    && !hasAdjacentFreeCellExcluding(nrr, nrc, row, col, free, new Cell(br, bc))) {
                                branchValidDirs.add(d);
                            }
                        }
                        if (branchValidDirs.isEmpty()) {
                            break;
                        }
                        int chosenDir = branchValidDirs.get(rand.nextInt(branchValidDirs.size()));
                        br = br + dr[chosenDir];
                        bc = bc + dc[chosenDir];
                        free[br][bc] = true;
                    }
                }
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

    /**
     * Checks if the cell at (r, c) has any adjacent free cell (in the four cardinal directions)
     * other than the specified exclude cell.
     */
    private boolean hasAdjacentFreeCellExcluding(int r, int c, int row, int col, boolean[][] free, Cell exclude) {
        int[] dr = {-1, 0, 1, 0};
        int[] dc = {0, 1, 0, -1};
        for (int i = 0; i < 4; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];
            if (nr >= 0 && nr < row && nc >= 0 && nc < col) {
                if (free[nr][nc] && !(nr == exclude.getRow() && nc == exclude.getCol())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Optional<Maze> getMazeById(Long id) {
        return mazeRepository.findById(id);
    }
}
