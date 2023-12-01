package org.batlinkjrg;

import org.batlinkjrg.SudokuGame.GameGenerator;

public class Main {
    public static void main(String[] args) {

        GameGenerator gameGen = new GameGenerator();
        gameGen.printSudokuBoard(gameGen.getSolutionBoard());
        gameGen.printSudokuBoard(gameGen.getGameBoard(3));

    }
}