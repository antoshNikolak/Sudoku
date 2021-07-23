package sample;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.*;

public class Game {

    private Canvas canvas;
    private GraphicsContext gc;

    private Cell[][] cells = new Cell[9][9];

    public Game() {

    }

    private void init() {
        this.canvas = (Canvas) Main.root.lookup("#gameCanvas");
        this.gc = canvas.getGraphicsContext2D();

        for (int column = 0; column < 9; column++) {
            for (int row = 0; row < 9; row++) {
                cells[column][row] = new Cell(column, row);
            }
        }
    }

    private void enableUserInteraction() {
        Pane pane = Main.root;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (cells[j][i].getValue() == 0) {
                    TextField textField = new TextField();
                    textField.setPromptText("txt");
                    textField.setLayoutY(i * 50);
                    textField.setLayoutX(j * 50);
                    textField.setMaxWidth(50);
                    textField.setMinWidth(50);
                    textField.setPrefWidth(50);
                    textField.setPrefHeight(50);
                    pane.getChildren().add(textField);
                }
            }
        }
    }


    public void start() {
        init();
        createSudoku();
        enableUserInteraction();

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                render();
            }
        };
        animationTimer.start();
    }


    private void createSudoku() {


        addValue(0, 0);
        removeValue();
    }


    private void clearInvisCells() {
        for (Cell cell : invisibleCells) {
            cell.setValue(0);
        }
    }

    private void removeValue() {
        final int cellsToTakeOut = 20;

        do {
            Cell cell = findRandomVisibleCell();
            int cellOriginalValue = cell.getValue();
            invisibleCells.add(cell);
            clearInvisCells();

            int solutions = checkSudokuIsSolvable(0, 0);

            if (solutions > 1) {
                System.out.println("cell added");
                cell.setValue(cellOriginalValue);
                invisibleCells.remove(cell);
            }

        } while (invisibleCells.size() != cellsToTakeOut);
    }

    private ArrayList<Cell> invisibleCells = new ArrayList<>();

    private int checkSudokuIsSolvable(int invisibleCellsSolved, int possibleSolutions) {
        int counter = 0;
        while (true) {
            counter++;
            if (checkSudokuSolved(invisibleCellsSolved)) {
                return possibleSolutions + 1;
            }

            Cell cell = invisibleCells.get(invisibleCellsSolved);
            if (counter > 9) {
                cell.setValue(0);
                return possibleSolutions;
            }
            cell.setValue(counter);

            if (!checkConstraints(cell.getRow(), cell.getColumn())) {
                continue;
            }

            possibleSolutions = checkSudokuIsSolvable(invisibleCellsSolved + 1, possibleSolutions);
            if (possibleSolutions > 1) {
                return possibleSolutions;
            }
//            if (solved) {
//                return possibleSolution > 1;//return false if only one solution
//            }


        }


    }

    private boolean checkSudokuSolved(int numOfInvisibleCellsSolved) {
        return numOfInvisibleCellsSolved >= invisibleCells.size();
    }


    //go to first invisible cell
    //check if its has any values available
    //if not replenish values, backtrack
    // if yes assign it a random value from its its values available list
    // check contraints, if it doesnt work try again (continue)
    // if all invisible cells have value assigned, solved

    //Sudoku is unsolvable when the first invisible cell has been backtracked
    //to 10 times and has to be replenished

    //store values available for each cell
    //if no values available, backtrack and replenish values
    //if there are values available, pick a random value, assign it to cell


    //continue: give same cell another try
    //return true: do when finished solving
    //false to backtrack



    private Cell findRandomVisibleCell() {
        while (true) {
            Random random = new Random();
            int x = random.nextInt(cells.length);
            int y = random.nextInt(cells.length);
            if ((cells[y][x].getValue() == 0)) {
                continue;
            }
            return cells[y][x];

        }

    }
    //stop at 9 values


    private boolean addValue(int row, int column) {
        while (true) {

            Cell currentCell = cells[column][row];
            ArrayList<Integer> valuesAvailable = currentCell.getValuesAvailable();

            if (currentCell.getValuesAvailable().size() == 0) {
                currentCell.replenishValuesAvailable();
                currentCell.setValue(0);
                return false;
            }

            int randomItem = valuesAvailable.get(new Random().nextInt(valuesAvailable.size()));
            this.cells[column][row].setValue(randomItem); //todo currentCell

            currentCell.getValuesAvailable().remove(Integer.valueOf(randomItem));
            if (!checkConstraints(row, column)) {
                continue;//try again with different values
            }
            if (row == 8 && column == 8) {
                return true;
            }
            if (column == 8) {
                if (!addValue(row + 1, 0)) continue;
            } else {
                if (!addValue(row, column + 1)) continue;
            }

            return true;

        }


    }

    private boolean checkConstraints(int row, int column) {
        return !(checkColumnDuplicates(column)
                || checkRowDuplicate(row)
                || checkGridDuplicates(column, row));

    }


    private boolean checkGridDuplicates(int column, int row) {
        ArrayList<Integer> gridValues = new ArrayList<>(9);

        int columnLowerBound = getGridCount(column);
        int columnUpperBound = columnLowerBound + 3;
        int rowLowerBound = getGridCount(row);
        int rowUpperBound = rowLowerBound + 3;


        for (int columnIndex = columnLowerBound; columnIndex < columnUpperBound; columnIndex++) {
            for (int rowIndex = rowLowerBound; rowIndex < rowUpperBound; rowIndex++) {
                int value = this.cells[columnIndex][rowIndex].getValue();
                if (value != 0) gridValues.add(value);
            }
        }
        Set<Integer> uniqueValues = new HashSet<>(gridValues);
        return uniqueValues.size() < gridValues.size();

    }


    private int getGridCount(int num) {
        int multiplier = num / 3;
        return (multiplier) * 3;
    }

    private Cell[] trimColumnArray(int columnNum) {
        Cell[] column = new Cell[9];
        System.arraycopy(cells[columnNum], 0, column, 0, 9);
        return column;
    }

    private Cell[] trimRowArray(int rowNum) {
        Cell[] row = new Cell[9];
        for (int i = 0; i < cells.length; i++) {
            row[i] = cells[i][rowNum];
        }
        return row;
    }

    private boolean checkRowDuplicate(int row) {
        Cell[] rowCells = trimRowArray(row);
        ArrayList<Integer> rowValues = new ArrayList<>();

        for (Cell cell : rowCells) {
            if (cell.getValue() != 0) rowValues.add(cell.getValue());
        }

        Set<Integer> uniqueValues = new HashSet<>(rowValues);
        return uniqueValues.size() < rowValues.size();


    }

    private boolean checkColumnDuplicates(int column) {
        Cell[] allCells = trimColumnArray(column);
        ArrayList<Integer> allValues = new ArrayList<>();

        for (Cell cell : allCells) {
            if (cell.getValue() != 0) allValues.add(cell.getValue());
        }

        Set<Integer> uniqueValues = new HashSet<>(allValues);

        return uniqueValues.size() < allValues.size();


    }


    private void render() {
        drawBackGround();
        drawValues();

    }


    private void drawValues() {
        for (int i = 0; i < 9; i++) {//height
            for (int j = 0; j < 9; j++) { // width
                if (cells[i][j].getValue() != 0) {
                    gc.fillText(String.valueOf(cells[i][j].getValue()), i * 50 + 20, j * 50 + 25);
                }


            }

        }
    }

    private void drawBackGround() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (int i = 1; i < 9; i++) {
            gc.setLineWidth(1);
            if (i % 3 == 0) gc.setLineWidth(5);
            gc.strokeLine(i * 50, 0, i * 50, canvas.getHeight());
            gc.strokeLine(0, i * 50, canvas.getWidth(), i * 50);

        }
    }

}


