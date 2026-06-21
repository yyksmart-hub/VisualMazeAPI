package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MazePanel extends JPanel {
    // ה-CELL_SIZE כבר לא קבוע סטטי (1), אלא משתנה סופי שמחושב בבנאי!
    private final int CELL_SIZE;
    private static final Color EMPTY_CELL_COLOR = Color.WHITE;

    private final int rows;
    private final int cols;
    private final boolean[][] isWall;
    private final MazeConfig config;
    private final List<Point> animatedPath = new ArrayList<>();

    private final Color wallColor;
    private final Color gridColor;
    private final Color pathColor;

    // הווספנו לבנאי את הפרמטרים logicalWidth ו-logicalHeight (הגודל שהמשתמש הקליד, למשל 30x30)
    public MazePanel(BufferedImage mazeImage, MazeConfig config, int logicalWidth, int logicalHeight) {
        this.config = config;
        this.cols = logicalWidth;   // כעת זה 30 ולא 600
        this.rows = logicalHeight;  // כעת זה 30 ולא 600

        // חישוב אוטומטי: כמה פיקסלים בתמונה מרכיבים משבצת מבוך אחת? (למשל 600/30 = 20 פיקסלים)
        this.CELL_SIZE = mazeImage.getWidth() / logicalWidth;

        this.isWall = new boolean[rows][cols];

        this.wallColor = Color.decode(config.getWallCellColor());
        this.gridColor = Color.decode(config.getGridColor());
        this.pathColor = Color.decode(config.getPathColor());

        // קובעים את גודל הפאנל שיהיה בדיוק בגודל התמונה המקורית מהשרת
        setPreferredSize(new Dimension(mazeImage.getWidth(), mazeImage.getHeight()));

        int whiteRGB = Color.WHITE.getRGB();

        // פענוח המבוך ברמת המשבצת (בודקים את הפיקסל שבמרכז כל משבצת)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // מוצאים את נקודת האמצע של המשבצת הנוכחית על התמונה האמיתית
                int pixelX = c * CELL_SIZE + CELL_SIZE / 2;
                int pixelY = r * CELL_SIZE + CELL_SIZE / 2;

                // הגנה קטנה מחריגה מגבולות התמונה
                if (pixelX >= mazeImage.getWidth()) pixelX = mazeImage.getWidth() - 1;
                if (pixelY >= mazeImage.getHeight()) pixelY = mazeImage.getHeight() - 1;

                int pixelRGB = mazeImage.getRGB(pixelX, pixelY);
                // אם הפיקסל במרכז הוא לא לבן - סימן שכל המשבצת הזו היא קיר!
                this.isWall[r][c] = (pixelRGB != whiteRGB);
            }
        }
    }

       @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // --- שכבה 1: ציור כל הרקעים (קירות ומעברים) ---
        // בשלב זה אנחנו עושים רק fillRect, בלי לגעת בגריד בכלל!
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (this.isWall[r][c]) {
                    g.setColor(wallColor);
                } else {
                    g.setColor(EMPTY_CELL_COLOR);
                }
                // צביעת ריבוע הרקע
                g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        // --- שכבה 2: ציור הרשת (Grid) מעל הרקעים המוכנים ---
        // הלולאה הזו רצה אחרי שכל הרקעים צוירו, ולכן הצבע של השרת בחיים לא יידרס!
        if (config.isDrawGrid() && CELL_SIZE > 1) {
            g.setColor(gridColor); // שימוש בצבע המדויק מהשרת
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    // אם אתה רוצה שהגריד יופיע רק בשבילים הלבנים (כמו שהיה מקודם):
                    if (!this.isWall[r][c]) {
                        g.drawRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        }

        // --- שכבה 3: ציור נתיב הפתרון המונפש ---
        synchronized (animatedPath) {
            g.setColor(pathColor);
            for (Point p : animatedPath) {
                g.fillRect(p.x * CELL_SIZE, p.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }
 
    public boolean[][] getIsWallMatrix() {
        return this.isWall;
    }

    public void addPathPoint(Point p) {
        synchronized (animatedPath) {
            animatedPath.add(p);
        }
        repaint();
    }

    public void clearAnimation() {
        synchronized (animatedPath) {
            animatedPath.clear();
        }
        repaint();
    }
}
