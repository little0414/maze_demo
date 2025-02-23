package org.example.controller;

import org.example.client.MazeView;
import org.example.client.MazeViewImpl;
import org.example.domain.Maze;
import org.example.service.MazeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/maze")
public class MazeController {

    @Autowired
    private MazeService mazeService;

    // POST endpoint: generate a new maze and return its HTML view.
    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String createMaze(@RequestParam(defaultValue = "10") int row,
                             @RequestParam(defaultValue = "10") int col) {
        Maze maze = mazeService.generatePerfectMaze(row, col);
        MazeView view = new MazeViewImpl(maze);
        return view.render();
    }

    // GET endpoint: retrieve an existing maze by id and return its HTML view.
    @GetMapping(value = "/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public String getMaze(@PathVariable Long id) {
        Maze maze = mazeService.getMazeById(id).orElse(null);
        if (maze == null) {
            return "<html><body><h1>Maze not found</h1></body></html>";
        }
        MazeView view = new MazeViewImpl(maze);
        return view.render();
    }
}

