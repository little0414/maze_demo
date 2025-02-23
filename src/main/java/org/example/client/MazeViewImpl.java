package org.example.client;

import org.example.domain.Cell;
import org.example.domain.Maze;

import java.util.List;

public class MazeViewImpl implements MazeView {

    private Maze maze;

    public MazeViewImpl(Maze maze) {
        this.maze = maze;
    }

    @Override
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head>");
        sb.append("<meta charset='UTF-8'>");
        sb.append("<title>Maze Viewer</title>");
        sb.append("<style>");
        sb.append("table.maze { border-collapse: collapse; margin-bottom: 20px; }");
        sb.append("table.maze td { width: 30px; height: 30px; border: 1px solid #000; text-align: center; vertical-align: middle; font-weight: bold; }");
        sb.append("td.obstacle { background-color: #333; }");
        sb.append("td.free { background-color: #fff; }");
        sb.append("td.solution { background-color: #8f8; }");
        sb.append("</style>");
        sb.append("</head><body>");
        // Display the heading with Maze ID included.
        sb.append("<h1>Maze Viewer (ID: ").append(maze.getId()).append(")</h1>");

        // First show the maze layout without solution numbers.
        sb.append("<h2>Maze</h2>");
        sb.append(generateMazeTableWithoutSolution());

        // Then show the full solution with step numbers.
        sb.append("<h2>Step-by-Step Solution</h2>");
        sb.append(generateMazeTableWithSolution());

        sb.append("</body></html>");
        return sb.toString();
    }

    /**
     * Generates an HTML table showing the maze layout.
     * Obstacles are rendered as dark cells; free cells are blank.
     */
    private String generateMazeTableWithoutSolution() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class='maze'>");
        for (int r = 0; r < maze.getMazeRow(); r++) {
            sb.append("<tr>");
            for (int c = 0; c < maze.getMazeCol(); c++) {
                Cell cell = new Cell(r, c);
                if (maze.getObstacles().contains(cell)) {
                    sb.append("<td class='obstacle'></td>");
                } else {
                    sb.append("<td class='free'></td>");
                }
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    /**
     * Generates an HTML table showing the full solution.
     * Each cell on the solution path is marked with its order (starting at 1).
     */
    private String generateMazeTableWithSolution() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class='maze'>");
        for (int r = 0; r < maze.getMazeRow(); r++) {
            sb.append("<tr>");
            for (int c = 0; c < maze.getMazeCol(); c++) {
                Cell cell = new Cell(r, c);
                if (maze.getObstacles().contains(cell)) {
                    sb.append("<td class='obstacle'></td>");
                } else {
                    int index = getStepIndex(cell);
                    if (index >= 0) {
                        sb.append("<td class='solution'>").append(index + 1).append("</td>");
                    } else {
                        sb.append("<td class='free'></td>");
                    }
                }
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    /**
     * Returns the (0-based) index of the cell in the solution steps.
     * If the cell is not part of the solution, returns -1.
     */
    private int getStepIndex(Cell cell) {
        List<Cell> steps = maze.getSteps();
        for (int i = 0; i < steps.size(); i++) {
            Cell step = steps.get(i);
            if (step.getRow() == cell.getRow() && step.getCol() == cell.getCol()) {
                return i;
            }
        }
        return -1;
    }
}
