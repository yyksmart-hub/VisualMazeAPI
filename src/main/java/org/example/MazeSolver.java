package org.example;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MazeSolver {

    // קבועים - מערך המייצג את ארבעת כיווני התנועה האפשריים (למעלה, למטה, שמאלה, ימינה)
    private static final int[][] DIRECTIONS = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };

    /**
     * פונקציה למציאת המסלול במבוך בעזרת אלגוריתם BFS.
     * מקבלת את מערך הקירות, ומחזירה רשימת נקודות המייצגות את המסלול מהתחלה לסוף.
     * אם אין פתרון, מחזירה null.
     */
    public static List<Point> solveBFS(boolean[][] isWall) {
        int rows = isWall.length;
        int cols = isWall[0].length;

        Point start = new Point(0, 0); // משבצת שמאלית עליונה
        Point end = new Point(cols - 1, rows - 1); // משבצת ימנית תחתונה

        // מקרה קצה: אם נקודת ההתחלה או הסיום הן בעצמן קיר, אי אפשר לפתור
        if (isWall[start.y][start.x] || isWall[end.y][end.x]) {
            return null;
        }

        boolean[][] visited = new boolean[rows][cols]; // מעקב אחרי משבצות שכבר בדקנו
        Point[][] parent = new Point[rows][cols]; // מערך ששומר "מאיפה הגענו" לכל משבצת, כדי שנוכל לשחזר את המסלול בסוף
        Queue<Point> queue = new LinkedList<>();

        // אתחול האלגוריתם
        queue.add(start);
        visited[start.y][start.x] = true;

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            // אם הגענו לנקודת הסיום, עוצרים ומשחזרים את המסלול
            if (current.equals(end)) {
                return reconstructPath(parent, end);
            }

            // סורקים את כל השכנים של המשבצת הנוכחית
            for (int[] dir : DIRECTIONS) {
                int newY = current.y + dir[0];
                int newX = current.x + dir[1];

                // בודקים 3 תנאים:
                // 1. אנחנו בתוך גבולות המבוך. 2. המשבצת אינה קיר. 3. טרם ביקרנו בה.
                if (newY >= 0 && newY < rows && newX >= 0 && newX < cols
                        && !isWall[newY][newX] && !visited[newY][newX]) {

                    visited[newY][newX] = true;
                    parent[newY][newX] = current; // שומרים את ההורה
                    queue.add(new Point(newX, newY));
                }
            }
        }

        // אם התור התרוקן ולא הגענו ל-end, סימן שאין מסלול אפשרי
        return null;
    }

    /**
     * פונקציית עזר פרטית: הולכת אחורה מנקודת הסיום עד להתחלה בעזרת מערך ההורים,
     * ובונה את רשימת המסלול המלאה.
     */
    private static List<Point> reconstructPath(Point[][] parent, Point end) {
        List<Point> path = new ArrayList<>();
        Point current = end;

        while (current != null) {
            path.add(current);
            current = parent[current.y][current.x];
        }

        // מכיוון שהלכנו מהסוף להתחלה, הרשימה הפוכה. נהפוך אותה חזרה.
        Collections.reverse(path);
        return path;
    }
}