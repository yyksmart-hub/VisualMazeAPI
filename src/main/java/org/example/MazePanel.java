package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MazePanel extends JPanel {
    // גודל משבצת של פיקסל אחד (כפי שבחרת)
    private static final int CELL_SIZE = 2;
    private static final Color EMPTY_CELL_COLOR = Color.WHITE;

    private final int rows;
    private final int cols;
    private final boolean[][] isWall;
    private final MazeConfig config;
    private final List<Point> animatedPath = new ArrayList<>();

    // שמירת הצבעים כמשתני מחלקה כדי לחסוך עבודה ל-paintComponent
    private final Color wallColor;
    private final Color gridColor;
    private final Color pathColor;

    public MazePanel(BufferedImage mazeImage, MazeConfig config) {
        this.config = config;
        this.cols = mazeImage.getWidth();
        this.rows = mazeImage.getHeight();
        this.isWall = new boolean[rows][cols];

        // פענוח הצבעים פעם אחת ויחידה בזמן יצירת המבוך (Best Practice)
        this.wallColor = Color.decode(config.getWallCellColor());
        this.gridColor = Color.decode(config.getGridColor());
        this.pathColor = Color.decode(config.getPathColor());

        // הגדרת גודל החלון בהתאם לכמות המשבצות וגודלן
        setPreferredSize(new Dimension(cols * CELL_SIZE, rows * CELL_SIZE));

        decipherMaze(mazeImage);
    }

    public boolean[][] getIsWallMatrix() {
        return this.isWall;
    }

    public void addPathPoint(Point p) {
        this.animatedPath.add(p);
        repaint();
    }

    public void clearAnimation() {
        this.animatedPath.clear();
        repaint();
    }

    // שונה מ-deciphering ל-decipher (פועל ציווי כנהוג ב-Java)
    private void decipherMaze(BufferedImage mazeImage) {
        int whiteRGB = Color.WHITE.getRGB();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int pixelRGB = mazeImage.getRGB(c, r);
                this.isWall[r][c] = (pixelRGB != whiteRGB);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 1. ציור קירות ומעברים
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (this.isWall[r][c]) {
                    g.setColor(wallColor);
                } else {
                    g.setColor(EMPTY_CELL_COLOR);
                }

                // קודם כל צובעים את המשבצת (קיר או מעבר)
                g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                // --- התיקון כאן ---
                // מציירים את הרשת רק אם המשבצת גדולה מ-2, ורק אם היא *לא* קיר!
                // שינינו את התנאי ל גדול מ-1
                if (config.isDrawGrid() && CELL_SIZE > 1 && !this.isWall[r][c]) {
                    g.setColor(gridColor);
                    g.drawRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        // 2. ציור נתיב הפתרון המונפש
        for (Point p : animatedPath) {
            g.setColor(pathColor);
            g.fillRect(p.x * CELL_SIZE, p.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);

            if (config.isDrawGrid() && CELL_SIZE > 1) {
                g.setColor(gridColor);
                g.drawRect(p.x * CELL_SIZE, p.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }
}