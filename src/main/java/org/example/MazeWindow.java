package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class MazeWindow extends JFrame {
    private final MazePanel mazePanel;
    private final MazeConfig config;
    private boolean isAnimating = false;


    // הוספנו את int logicalWidth, int logicalHeight לסוף החתימה
    public MazeWindow(BufferedImage mazeImage, MazeConfig config, int logicalWidth, int logicalHeight) {
        this.config = config;
        setTitle("Maze Play");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // מעבירים את המידות לפאנל החדש
        mazePanel = new MazePanel(mazeImage, config, logicalWidth, logicalHeight);
        add(mazePanel, BorderLayout.CENTER);

        JButton checkSolutionBtn = new JButton("Check Solution");
        checkSolutionBtn.addActionListener(e -> handleCheckSolution());
        add(checkSolutionBtn, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void handleCheckSolution() {
        if (isAnimating) {
            return;
        }

        mazePanel.clearAnimation();

        boolean[][] isWallMatrix = mazePanel.getIsWallMatrix();
        List<Point> path = MazeSolver.solveBFS(isWallMatrix);

        if (path == null || path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No solution found", "Result", JOptionPane.INFORMATION_MESSAGE);
        } else {
            startAnimation(path);
        }
    }

    private void startAnimation(List<Point> path) {
        isAnimating = true;

        Timer timer = new Timer(config.getAnimationDelayMs(), null);
        final int[] index = {0};

        timer.addActionListener(e -> {
            if (index[0] < path.size()) {
                mazePanel.addPathPoint(path.get(index[0]));
                index[0]++;
            } else {
                timer.stop();
                isAnimating = false;
            }
        });
        timer.start();
    }
}