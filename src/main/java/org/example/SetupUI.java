package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SetupUI {

    // --- קבועים (טיפול ב"מספרי הקסם") ---
    private static final int DEFAULT_MAZE_SIZE = 30;
    private static final int MIN_MAZE_SIZE = 5;
    private static final int MAX_MAZE_SIZE = 100;
    // קבועים עבור גודל חלון ההגדרות
    private static final int WINDOW_SIZE = 400;

    // קבועים עבור עימוד הגריד (GridLayout)
    private static final int GRID_ROWS = 0;    // 0 אומר כמות שורות דינמית בהתאם לרכיבים
    private static final int GRID_COLS = 2;    // 2 עמודות (שם הרכיב והערך שלו)
    private static final int GRID_GAP = 10;  // רווח  בפיקסלים בין התאים

    // אובייקטים שנשמור ברמת המחלקה
    private final ApiClient apiClient;
    private volatile MazeConfig currentConfig;
    // תוויות לתצוגת ההגדרות
    private JLabel wallColorLabel;
    private JLabel pathColorLabel;
    private JLabel drawGridLabel;
    private JLabel gridColorLabel;
    private JLabel delayLabel;

    public SetupUI() {
        apiClient = new ApiClient();

        JFrame frame = new JFrame("Maze Generator Setup");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_SIZE, WINDOW_SIZE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridLayout(GRID_ROWS, GRID_COLS, GRID_GAP, GRID_GAP));

        // --- סקשן 1: תצוגת ההגדרות מהשרת ---
        frame.add(new JLabel("--- Render Config ---"));
        frame.add(new JLabel("--------------------"));

        // שימוש בפונקציית העזר החדשה כדי לקצר ולנקות את הקוד
        wallColorLabel = new JLabel("Waiting...");
        addRow(frame, "Wall Color:", wallColorLabel);

        pathColorLabel = new JLabel("Waiting...");
        addRow(frame, "Path Color:", pathColorLabel);

        drawGridLabel = new JLabel("Waiting...");
        addRow(frame, "Draw Grid:", drawGridLabel);

        gridColorLabel = new JLabel("Waiting...");
        addRow(frame, "Grid Color:", gridColorLabel);

        delayLabel = new JLabel("Waiting...");
        addRow(frame, "Anim Delay (ms):", delayLabel);

        // כפתור רענון להגדרות
        JButton refreshConfigButton = new JButton("Refresh Config");
        refreshConfigButton.addActionListener(e -> fetchAndUpdateConfig(frame));
        frame.add(new JLabel("")); // תא ריק ליישור
        frame.add(refreshConfigButton);


        // --- סקשן 2: בחירת גודל המבוך ---
        frame.add(new JLabel("--- Maze Size ---"));
        frame.add(new JLabel("-----------------"));

        JTextField widthField = new JTextField(String.valueOf(DEFAULT_MAZE_SIZE));
        addRow(frame, "Width (" + MIN_MAZE_SIZE + "-" + MAX_MAZE_SIZE + "):", widthField);

        JTextField heightField = new JTextField(String.valueOf(DEFAULT_MAZE_SIZE));
        addRow(frame, "Height (" + MIN_MAZE_SIZE + "-" + MAX_MAZE_SIZE + "):", heightField);

        // כפתור הבאת המבוך
        JButton getMazeButton = new JButton("GET MAZE");
        getMazeButton.addActionListener(e -> {

            if (currentConfig == null) {
                JOptionPane.showMessageDialog(frame, "Please wait for config to load or click Refresh.", "Missing Config", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int width = parseDimension(widthField.getText());
            int height = parseDimension(heightField.getText());

            // עדכון הממשק למקרה שהייתה שגיאת הקלדה
            widthField.setText(String.valueOf(width));
            heightField.setText(String.valueOf(height));

            System.out.println("Fetching maze image...");

            // --- התיקון הקריטי: הפעלת פניית הרשת ב-Thread נפרד כדי לא לתקוע את המסך ---
            new Thread(() -> {
                BufferedImage mazeImage = apiClient.fetchMazeImage(width, height);

                // חזרה ל-UI Thread כדי לעדכן את המסך
                SwingUtilities.invokeLater(() -> {
                    if (mazeImage != null) {
                        // שנה את השורה המקורית שהייתה: new MazeWindow(img, currentConfig);
                         // לשורה הזו (כאשר width ו-height הם הערכים שנלקחו משדות הטקסט):
                        new MazeWindow(mazeImage, currentConfig, width, height);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to fetch maze image.", "Network Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }).start();
        });

        frame.add(new JLabel(""));
        frame.add(getMazeButton);

        frame.setVisible(true);

        // הבאת נתונים ראשונית בעת פתיחת החלון
        fetchAndUpdateConfig(frame);
    }

    /**
     * פונקציית עזר לקיצור הקוד: מוסיפה שורה של תווית ורכיב לפאנל
     */
    private void addRow(JFrame frame, String labelText, Component component) {
        frame.add(new JLabel("  " + labelText));
        frame.add(component);
    }

    /**
     * פונקציה שפונה לשרת ב-Thread נפרד, ומעדכנת את התוויות במסך
     */
    private void fetchAndUpdateConfig(JFrame frame) {
        System.out.println("Fetching render config...");

        // --- התיקון הקריטי: פנייה לשרת ברקע כדי שהחלון לא יקפא בפתיחה ---
        new Thread(() -> {
            MazeConfig newConfig = apiClient.fetchRenderConfig();

            // חזרה ל-UI Thread לעדכון התוויות
            SwingUtilities.invokeLater(() -> {
                if (newConfig != null) {
                    currentConfig = newConfig;
                    wallColorLabel.setText(currentConfig.getWallCellColor());
                    pathColorLabel.setText(currentConfig.getPathColor());
                    drawGridLabel.setText(String.valueOf(currentConfig.isDrawGrid()));
                    gridColorLabel.setText(currentConfig.getGridColor());
                    delayLabel.setText(String.valueOf(currentConfig.getAnimationDelayMs()));
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to load config from server.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }).start();
    }

    private int parseDimension(String text) {
        try {
            int value = Integer.parseInt(text.trim());
            // שימוש בקבועים במקום מספרי הקסם
            if (value >= MIN_MAZE_SIZE && value <= MAX_MAZE_SIZE) {
                return value;
            }
        } catch (NumberFormatException ignored) {
        }
        return DEFAULT_MAZE_SIZE;
    }
}