package com.eliemichel.polyfinite.application.gameplay;

import javafx.scene.Group;

public class GameCamera {

    private Group canvasGroup;
    private double canvasTranslateX = 0;
    private double canvasTranslateY = 0;
    private double zoomLevel = 1.0;
    private double dragStartX;
    private double dragStartY;
    private boolean isDragging = false;
    private double totalDragDistance = 0;

    public GameCamera(Group canvasGroup) {
        this.canvasGroup = canvasGroup;
    }

    public void handleZoom(double deltaY) {
        double zoomFactor = (deltaY > 0) ? 1.1 : 0.9;
        double newZoom = zoomLevel * zoomFactor;

        if (newZoom < 0.5 || newZoom > 3.0) {
            return;
        }

        zoomLevel = newZoom;

        canvasGroup.setScaleX(zoomLevel);
        canvasGroup.setScaleY(zoomLevel);

        System.out.println("Zoom: " + zoomLevel);
    }

    public void handleDragStart(double x, double y) {
        dragStartX = x;
        dragStartY = y;
        isDragging = true;
        totalDragDistance = 0;
    }

    public void handleDragEnd() {
        isDragging = false;
    }

    public void handleDrag(double x, double y) {
        double deltaX = x - dragStartX;
        double deltaY = y - dragStartY;

        totalDragDistance += Math.abs(deltaX) + Math.abs(deltaY);

        canvasTranslateX += deltaX;
        canvasTranslateY += deltaY;

        canvasGroup.setTranslateX(canvasTranslateX);
        canvasGroup.setTranslateY(canvasTranslateY);

        dragStartX = x;
        dragStartY = y;
    }

    public int[] screenToTile(double clickX, double clickY, int gridWidth, int gridHeight, int tileSize) {
        double actualCanvasWidth = gridWidth * tileSize;
        double actualCanvasHeight = gridHeight * tileSize;

        double screenWidth = 1920;
        double screenHeight = 1080;
        double canvasCenterX = screenWidth / 2.0;
        double canvasCenterY = screenHeight / 2.0;

        double relX = clickX - canvasCenterX;
        double relY = clickY - canvasCenterY;

        double afterPanX = relX - canvasTranslateX;
        double afterPanY = relY - canvasTranslateY;

        double afterZoomX = afterPanX / zoomLevel;
        double afterZoomY = afterPanY / zoomLevel;

        double canvasX = afterZoomX + (actualCanvasWidth / 2.0);
        double canvasY = afterZoomY + (actualCanvasHeight / 2.0);

        int col = (int) (canvasX / tileSize);
        int row = (int) (canvasY / tileSize);

        System.out.println("Click: (" + clickX + ", " + clickY + ")");
        System.out.println("Tile: row=" + row + ", col=" + col);

        if (row < 0 || row >= gridHeight || col < 0 || col >= gridWidth) {
            return null;
        }

        return new int[]{row, col};
    }

    public boolean isDragging() {
        return isDragging;
    }

    public double getTotalDragDistance() {
        return totalDragDistance;
    }

    public double getZoomLevel() {
        return zoomLevel;
    }
}