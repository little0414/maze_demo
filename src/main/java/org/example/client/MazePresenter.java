package org.example.client;

import org.example.domain.Maze;

public class MazePresenter {

    private Maze maze;

    public MazePresenter(Maze maze) {
        this.maze = maze;
    }

    /**
     * Creates a MazeView from the Maze model.
     */
    public MazeView createView() {
        return new MazeViewImpl(maze);
    }
}

