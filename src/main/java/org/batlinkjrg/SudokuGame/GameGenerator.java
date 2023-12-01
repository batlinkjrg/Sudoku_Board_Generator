package org.batlinkjrg.SudokuGame;

import java.util.HashSet;
import java.util.Random;

import static java.lang.Math.*;


// TODO: Convert board generator to use seeds (a-i instead of 1-9) as to allow for increased random boards with greater speed

public class GameGenerator {

    public static final int BOARD_SIZE = 9, BOX_SIZE = 9;

    // These numbers will be used to index the different rows and columns
    private int[] finalBoard;
    private int[] board;

    public GameGenerator() {
        createNewBoard();
    }

    public int[] getSolutionBoard() {
        return finalBoard;
    }

    public int[] getGameBoard(int diffucltyLevel) {
        HashSet<Integer> blankSpaces = new HashSet<>();
        int[] challengeBoard = finalBoard;
        int blankSpacesCount = 1;
        switch (diffucltyLevel) {
            case 1: // Easy Difficulty
                blankSpacesCount = finalBoard.length-30;
                break;

            case 2: // Medium Difficulty
                blankSpacesCount = finalBoard.length-20;
                break;

            case 3: // Hard Difficulty
                blankSpacesCount = finalBoard.length-10;
                break;

            default: // Default is super easy
                blankSpacesCount = finalBoard.length/40;
                break;
        }

        for(int i = 0; i < blankSpacesCount; i++) {
            int blankSpace = RandomIntGenerator(0, finalBoard.length-1);

            while(blankSpaces.contains(blankSpace)) {
                blankSpace = RandomIntGenerator(0, finalBoard.length-1);
            }

            blankSpaces.add(blankSpace);
        }

        for(int blankSpace : blankSpaces) {
            challengeBoard[blankSpace] = 0;
        }

        return finalBoard;
    }

    public void printSudokuBoard(int[] board) {
        // Guard Clause
        if(board.length != finalBoard.length) {
            System.out.println("Can't print board!");
            return;
        }

        System.out.println("****** BOARD ******");
        for(ROW row : ROW.values()) {
            for(COLUMN column : COLUMN.values()) {
                int index = row.numToAdd + column.numToAdd;
                System.out.print(" " + finalBoard[index] + " ");

                if(column == COLUMN.THREE || column == COLUMN.SIX) {
                    System.out.print("|");
                }
            }

            if(row == ROW.THREE || row == ROW.SIX) {
                System.out.println();
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            } else { System.out.println(); }

        }
    }

    public void createNewBoard() {
        //System.out.println("Searching for a board!");

        // Search for a Sudoku board
        finalBoard = findValidBoard();

        //System.out.println("A board has been found!!");
    }

    // Board Creation
    private int[] findValidBoard() {
        // Set up values
        int boardAttemptCount = 0;
        HashSet<Integer> resolvedPositions = new HashSet<>(); // Keep track of indexes visited

        // Search for boards
        boolean validBoard = false;
        while(!validBoard) {
            boardAttemptCount++;
            board = new int[BOARD_SIZE*BOARD_SIZE]; // 81 indexes for 9x9 board
            resolvedPositions.clear();

            // Generate Random boards
            for(int index = 0; index < board.length; index++) {
                // Get random number
                int randNum = pickInt(index);
                if(randNum == 0) {
                    if(!switchPreviousInt(index)) {  break; }
                }

                // Place random number
                board[index] = randNum;
                resolvedPositions.add(index);
            }

            // Double Check Board is Valid
            // System.out.println("Board Attempt Number: " + boardAttemptCount);
            validBoard = validateBoard();
        }

        // Return the found board!
        return board;
    }

    private boolean validateBoard() {
        boolean validBoard = true;

        for (int i = 0; i < board.length; i++) {
            validBoard = checkNumberAvaliable(i);
            if(!validBoard) { break; }
        }

        return validBoard;
    }

    private int pickInt(int boardPos) {
        int numToPlace = 0;
        HashSet<Integer> triedIntegers = new HashSet<>();

        do {
            int randNum = RandomIntGenerator(1, 9, triedIntegers);
            board[boardPos] = randNum;
            if(!checkNumberAvaliable(boardPos)) { triedIntegers.add(randNum); }
            else { numToPlace = randNum; break; }
        } while(triedIntegers.size() < BOX_SIZE);

        return numToPlace;
    }

    private boolean switchPreviousInt(int index) {
        if(index == 0) { return false; }

        boolean searching = true;
        int boardPos = index-1;
        int numToPlace = 0;
        HashSet<Integer> triedIntegers = new HashSet<>();
        triedIntegers.add(board[boardPos]);

        while(searching) {
            do {
                int randNum = RandomIntGenerator(1, 9, triedIntegers);
                board[boardPos] = randNum;
                if(!checkNumberAvaliable(boardPos)) { triedIntegers.add(randNum); }
                else { numToPlace = randNum; break; }
            } while(triedIntegers.size() < BOX_SIZE);

            if(numToPlace == 0) {
                if(!switchPreviousInt(boardPos)) { return false; }
            }

            searching = false;
        }

        board[boardPos] = numToPlace;
        return true;
    }


    // Utils
    private boolean checkNumberAvaliable(int boardPos) {
        if(board[boardPos] == 0) { return false; }

        int checkerPos = boardPos; // Set checker position
        Coordinate coords = findBoardCoords(boardPos); // Get current row and column

        // Check Box
        {
            // Set forward amount and backwards amount.
            int boxPos = boardPos % BOX_SIZE;
            checkerPos = boardPos - boxPos; // checker starts at 0 for that box

            // Loop through current box and check for matches
            for (int i = 0; i < BOX_SIZE; i++) {
                if (checkerPos == boardPos) {
                    checkerPos++;
                    continue;
                }

                if (board[checkerPos] == board[boardPos]) {
                    return false;
                }

                checkerPos++;
            }
        }

        // Check Horizontal
        for (COLUMN column : COLUMN.values()) {
            if(column == coords.column) { continue; }

            checkerPos = coords.row.numToAdd + column.numToAdd;
            if(board[checkerPos] == board[boardPos]) { return false; }
        }

        // Check Vertical
        for (ROW row : ROW.values()) {
            if(row == coords.row) { continue; }

            checkerPos = row.numToAdd + coords.column.numToAdd;
            if(board[checkerPos] == board[boardPos]) { return false; }
        }

        // If we made it here than the number is valid in that position
        return true;
    }

    private Coordinate findBoardCoords(int boardPos) {
       // Loop through coordinates
       // It isn't optimized, but it doesn't need to be
        for (ROW row : ROW.values()) {
            for(COLUMN column : COLUMN.values()) {
                    int positionGuess = row.numToAdd + column.numToAdd;
                    if(positionGuess == boardPos) {
                        Coordinate coords = new Coordinate();
                        coords.column = column;
                        coords.row = row;
                        return coords;
                    }
            }
        }

        // Something went wrong... oops, this point should never be reached
        return null;
    }


    // Random Number Generators
    public static int RandomIntGenerator(float min, float max, HashSet<Integer> notAllowed) {
        Random rand = new Random();
        int randNum = 0;

        // Search for allowed number
        boolean searching = true;
        while(searching) {
            randNum = (int) round(rand.nextFloat() * (max - min) + min);
            if(!notAllowed.contains(randNum)) { searching = false; }
        }

        return randNum;
    }

    public static int RandomIntGenerator(float min, float max) {
        Random rand = new Random();
        return (int) round(rand.nextFloat() * (max - min) + min);
    }
}

// "Structure" to hold x and y coordinates as row and column
class Coordinate {
    public COLUMN column;
    public ROW row;
}

// Values of rows and columns
enum ROW {
    ONE(0),
    TWO(3),
    THREE(6),
    FOUR(27),
    FIVE(30),
    SIX(33),
    SEVEN(54),
    EIGHT(57),
    NINE(60);

    public final int numToAdd;

    private ROW(int numToAdd) {
        this.numToAdd = numToAdd;
    }
}

enum COLUMN {
    ONE(0),
    TWO(1),
    THREE(2),
    FOUR(9),
    FIVE(10),
    SIX(11),
    SEVEN(18),
    EIGHT(19),
    NINE(20);

    public final int numToAdd;

    private COLUMN(int numToAdd) {
        this.numToAdd = numToAdd;
    }
}
