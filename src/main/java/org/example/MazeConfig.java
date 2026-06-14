package org.example;


public class MazeConfig {
    private String wallCellColor;
    private String pathColor;
    private boolean drawGrid;
    private String gridColor;
    private int animationDelayMs;

    // קונסטרקטור ריק - חובה עבור ספריות לקריאת JSON
    public MazeConfig() {
    }

    // גטרים וסטרים (Getters & Setters)
    public String getWallCellColor() {
        return wallCellColor;
    }

    public void setWallCellColor(String wallCellColor) {
        this.wallCellColor = wallCellColor;
    }

    public String getPathColor() {
        return pathColor;
    }

    public void setPathColor(String pathColor) {
        this.pathColor = pathColor;
    }

    public boolean isDrawGrid() {
        return drawGrid;
    }

    public void setDrawGrid(boolean drawGrid) {
        this.drawGrid = drawGrid;
    }

    public String getGridColor() {
        return gridColor;
    }

    public void setGridColor(String gridColor) {
        this.gridColor = gridColor;
    }

    public int getAnimationDelayMs() {
        return animationDelayMs;
    }

    public void setAnimationDelayMs(int animationDelayMs) {
        this.animationDelayMs = animationDelayMs;
    }

    // פונקציית toString שתעזור לנו להדפיס בקלות את הנתונים למסך ולראות שהכל עובד
    @Override
    public String toString() {
        return "MazeConfig{" +
                "wallCellColor='" + wallCellColor + '\'' +
                ", pathColor='" + pathColor + '\'' +
                ", drawGrid=" + drawGrid +
                ", gridColor='" + gridColor + '\'' +
                ", animationDelayMs=" + animationDelayMs +
                '}';
    }
}
