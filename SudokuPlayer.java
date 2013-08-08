/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sudsolv;

import static sudsolv.SudokuGames.game;

/**
 *
 * @author Neil
 */
/**
 * The class SudokuPlayer is a sudoku game.  It will allow the player to fill
 * in a sudoku grid, and will check the validity of the solution if the player
 * wishes.  Checking can be performed either during 
 * game play, or only once the grid is filled.  A solver is also provided
 * for those times the player is feeling a little lazy ;-).  The solver can 
 * cope with all sudoku grids.
 * 
 * @author Neil Riste 
 * @version 8/10/8
 */
public class SudokuPlayer
{
    private int[][] game;
    private int[][][] possible;
    private int[][] origSetting;
    private int[][] gridState;
    private boolean check;
    
    private static final int isEMPTY = 0;
    private static final int isFIXED = 1;
    private static final int isSET = 2;
    private static final int SIZE = 9;
    private static final int[][] initial = {{5,3,0,0,7,0,0,0,0}, 
                                           {6,0,0,1,9,5,0,0,0},
                                           {0,9,8,0,0,0,0,6,0},
                                           {8,0,0,0,6,0,0,0,3},
                                           {4,0,0,8,0,3,0,0,1},
                                           {7,0,0,0,2,0,0,0,6},
                                           {0,6,0,0,0,0,2,8,0},
                                           {0,0,0,4,1,9,0,0,5},
                                           {0,0,0,0,8,0,0,7,9}};

/**
     * Constructor for objects of class SudokuPlayer.  Creates a game with a 
     * user defined 9x9 grid.  The grid supplied will be checked for being a
     * valid state of a sudoku. If it not a valid sudoku, construction will be
     * refused.  Whether the g.rid is solvable is not determined.
     */
    public SudokuPlayer(int[][] initial) {
        
        game = new int[SIZE][SIZE];
        origSetting = new int[SIZE][SIZE];
        gridState = new int[SIZE][SIZE];
        possible = new int[SIZE][SIZE][SIZE+1];
        
        game = initial;
        origSetting = clone(initial);
        for (int i=0; i<game.length; i++) {
            if (game[i].length != SIZE || game.length != SIZE) {
                IllegalArgumentException gridWrongSize;
                gridWrongSize = new IllegalArgumentException("The initial grid you have entered is the wrong size.  Please enter a 9x9 grid.");
                throw(gridWrongSize);
            }
        }

        for (int i=0; i<SIZE; i++) {
            for (int j=0; j<SIZE; j++) {
                if (game[i][j] == 0) {
                    gridState[i][j] = isEMPTY;
                }
                else {
                    gridState[i][j] = isFIXED;
                }
            }
        }
        this.updatePossibles();
        check = false;
        
        if (!this.isValid()) {
            IllegalArgumentException invalidSudoku;
            invalidSudoku = new IllegalArgumentException("The specified initial Sudoku game state is not valid.");
            throw(invalidSudoku);
        }
    }
    
    /**
     * Constructor for objects of class SudokuPlayer.  Creates a game using the 
     * default grid:
     *              {{5,3,0,0,7,0,0,0,0}, 
     *              {6,0,0,1,9,5,0,0,0},
     *              {0,9,8,0,0,0,0,6,0},
     *              {8,0,0,0,6,0,0,0,3},
     *              {4,0,0,8,0,3,0,0,1},
     *              {7,0,0,0,2,0,0,0,6},
     *              {0,6,0,0,0,0,2,8,0},
     *              {0,0,0,4,1,9,0,0,5},
     *              {0,0,0,0,8,0,0,7,9}} 
     * 
     */
    public SudokuPlayer() {

        this(initial);
    }

    /**
     * A helper method to make a copy of a two dimensional array of ints.
     * 
     * @param  grid     the grid to be duplicated 
     * @return          the reference to the duplicate grid
     */
    private static int[][] clone(int[][] grid) {
        
        int[][] clone = new int[grid.length][grid.length];
        
        for (int i=0; i<grid.length; i++) {
            for (int j=0; j<grid.length; j++) {
                clone[i][j] = grid[i][j];
            }
        }
                
        return clone;
    }
    
    /**
     * A helper method to make a copy of a one dimensional array of ints.
     * 
     * @param   int[] arrayToCopy   the array to be duplicated.
     * @return  the reference to the duplicate array.
     */
    private static int[] clone1D(int[] arrayToCopy) {
        int[] clone1D = new int[arrayToCopy.length];
        
        for (int i=0; i<arrayToCopy.length; i++) {
            clone1D[i] = arrayToCopy[i];
        }
        
        return clone1D;
    }
        
    
    /**
     * This method returns a copy of the two dimensional integer array of 
     * the current cell values of the Sudoku game.
     * 
     * @return int[][] A copy of the game grid.
     */
    public int[][] getGame() {

        int[][] dummy = new int[SIZE][SIZE];
        
        dummy = clone(game);
        return dummy;
    }
    
    /**
     * Returns an array of int[10] of values that a currently empty cell (i,j) 
     * could possibly be. The zeroth element is the number of possibilities for
     * cell (i,j), the n-th entry is 1 if n is a possible value, and 0 otherwise.
     * If cell (i,j) is already set, an array int[10] of zeros is returned.
     * 
     * @param   int i   Row address of cell to examine.
     * @param   int j   Column address of cell to examine.
     * 
     * @return  int[]   An array indicating how many values are possible 
     *                  for game[i][j] and which values they are.
     */
    public int[] getPossible(int i, int j) {
        
        int[] poss = new int[SIZE+1];
        
        poss = clone1D(possible[i][j]);
        
        return poss;
    }
    
    /**
     * Updates game[i][j] with value if allowed, and returns true, makes no
     * changes and returns false, otherwise.  Checks that are performed are:
     * 1.   Whether value is in the allowed range (1 to SIZE).
     * 
     * 2.   Whether the array indices i and j are in the allowed range (0 to
     *      SIZE-1).
     * 
     * 3.   Whether the cell game[i][j] was one of the orginally fixed cells.
     * 
     * 4.   If checking is enabled, whether the proposed new value is a 
     *      valid value for that cell.
     * 
     * If the proposed change passes all these tests, the cell game[i][j] is
     * updated to value, the gridState array is updated, and the possible
     * array is updated.
     * 
     * @param int i The row of the cell to be updated.
     * @param int j The column of the cell to be updated.
     * @param int value The value to be entered in game[i][j], if allowed.
     * 
     * @return boolean True if change allowed, false otherwise.
     */
    public boolean setCell(int i, int j, int value) {
        
        if (value < 1 || value > SIZE) {
            return false;
        }
        
        if (i < 0 || i > SIZE-1 || j < 0 || j > SIZE-1) {
            return false;
        }
        
        if (gridState[i][j] == isFIXED) {
            return false;
        }
        
        if (check && gridState[i][j] == isEMPTY && possible[i][j][value] != 1) {
            return false;
        }
        
        if (check && gridState[i][j] == isSET) {
            int hold = game[i][j];
            game[i][j] = 0;
            gridState[i][j] = isEMPTY;
            this.updatePossibles();
            if (possible[i][j][value] != 1) {
                game[i][j] = hold;
                gridState[i][j] = isSET;
                this.updatePossibles();
                return false;
            }
        }
            
        game[i][j] = value;
        gridState[i][j] = isSET;
        this.updatePossibles();
        
        return true;
    }
    
    /**
     * Returns true if the current game values form a legitimate Sudoku game.
     * 
     * @return  boolean Truth value of the statement "the grid forms a valid partial sudoku".
     */
    public boolean isValid() {
        
        if (!this.isRowValid(game) || !this.isRowValid(transpose(game)) || !this.isRowValid(subGridToRow(game))) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Helper method to determine if the array passed has rows that are valid
     * partially filled Latin arrays.
     */
    private boolean isRowValid(int[][] a) {

        int n = a.length;    
        int[] test = new int[n + 1];
        int[] loseTheZeroCounter = new int[n];

//        System.out.println("\n" + a[0][0] + "  " + a[0][1] + "  " + a[0][2] + "  " + a[0][3] + "  " + a[0][4] + "  " + a[0][5] + "  " + a[0][6] + "  " + a[0][7] + "  " + a[0][8] + "  ");
        for (int i=0; i<n; i++) {
            
            for (int j=0; j<n; j++) {
                test[a[i][j]]++;
//                System.out.println("\n" + test[0] + "  " + test[1] + "  " + test[2] + "  " + test[3] + "  " + test[4] + "  " + test[5] + "  " + test[6] + "  " + test[7] + "  " + test[8] + "  " + test[9] + "  ");
            }
            for (int j=0; j<n; j++) {
                loseTheZeroCounter[j] = test[j+1];
            }
            if (this.max(loseTheZeroCounter) > 1) {
                return false;
            }
            for (int j=0; j<=n; j++) {
                test[j] = 0;
            }
        }
        return true;
    }
    
    /**
     * Helper method to find the maximum value of the array passed.
     */
    private int max(int[] row) {
        int max = row[0];
        for (int i=0; i<row.length; i++) {
            if (row[i]>max) {
                max = row[i];
            }
        }
        return max;
    }

    /**
     * Helper method to return the transpose of the 2-D array passed.
     */
    private int[][] transpose(int[][] a) {
        //This must be used on a rectangular array.
        int[][] transA;
        transA = new int[a.length][a[0].length];
        int rows=transA.length;
        int cols=transA[0].length;
        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                transA[i][j]=a[j][i];
            }
        }
        return transA;
    }
    
    /**
     * Helper method to unwrap the Sudoku sub-grids into the rows of a 2-D array.
     */
    private int[][] subGridToRow(int[][] a) {
        
        int[][] sgToRow = new int[SIZE][SIZE];
        int m=0;
        
        for (int i=0; i<9; i+=3) {
            for (int j=0; j<9; j+=3) {
                sgToRow[m] = this.unwrapGrid(a, i, j);    
                m++;
            }        
        }        
        return sgToRow;
    }
    
    /**
     * Helper method to unwrap a sub-grid into a 1-D array.
     */
    private int[] unwrapGrid(int[][] a, int i, int j) {

        int[] unwrapped = new int[9];
        int m = 0;

        for (int k=0; k<3; k++) {
            for (int l=0; l<3; l++) {
                unwrapped[m] = a[i+k][j+l];
                m++;
            }
        }
        return unwrapped;
    }
    
    /**
     * Method which will return true if the game is completed.
     * 
     * @return  boolean Truth of the statement "the game is finished".
     */
    public boolean isFinished() {
        
        for (int i=0; i<SIZE; i++) {
            for (int j=0; j<SIZE; j++) {
                if (gridState[i][j] == isEMPTY) {
                    return false;
                }
            }
        }
        
        if (!this.isValid()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Method to toggle the value of the check variable to enable 
     * or disable checking of values as they are entered into the 
     * game grid.
     */
    public void setCheck() {
        if (check == true) {
            check = false;
        }
        else {
            check = true;
        }
    }
    
    /**
     * Method to return the value of the check variable.
     * 
     * @return  boolean The truth value of the statement "checking will be performed".
     */
    public boolean getCheck() {
        return check;
    }
    
    /**
     * This method resets the current working grid to its original 
     * game settings. This will allow the player to start afresh 
     * with the same game.
     */
    public void reset() {
        
        game = clone(origSetting);
        
        for (int i=0; i<SIZE; i++) {
            for (int j=0; j<SIZE; j++) {
                if (game[i][j] == 0) {
                    gridState[i][j] = isEMPTY;
                }
                else {
                    gridState[i][j] = isFIXED;
                }
            }
        }
        this.updatePossibles();
        check = false;
    }
    
    /**
     * This method solves the given sudoku grid if it is possible.  If multiple 
     * solutions are possible, only one is returned.  No indication of the number
     * of solutions possible is given.
     */
    public void solveGame() {
        
        int[] address = new int[3];
        
        address = this.getSingleton();
        
        while (this.setCell(address[0], address[1], address[2])) {
            address = this.getSingleton();
        }
        if (!this.isFinished()) {
            this.advancedSolver();
        }
    }
    
    /**
     * Helper method to search for the first cell in the game grid that
     * has only one possible entry.
     */
    private int[] getSingleton() {
        
        int[] here = {11,11,11};
//        System.out.println(here[0] + " " + here[1] + " " + here[2]);
        
        for (int i=0; i<SIZE; i++) {
            for (int j=0; j<SIZE; j++) {
                if (possible[i][j][0] == 1) {
                    here[0] = i;
                    here[1] = j;
                    for (int k=1; k<SIZE+1; k++) {
                        if (possible[i][j][k] != 0) {
                            here[2] = k;
                        }
                    }
                    return here;
                }
            }
        }
        return here;
    }
    
    /**
     * This method updates the possible[][][] array for every position 
     * in the current game configuration.
     */
    public void updatePossibles() {
                
        for (int i=0; i<SIZE; i++) {
            for (int j=0; j<SIZE; j++) {
                for (int k=0; k<SIZE+1; k++) {
                    possible[i][j][k] = 0;
                }
                if (gridState[i][j] == isEMPTY) {
                    for (int val=1; val<=SIZE; val++) {
                        game[i][j] = val;
                        if (this.isValid()) {
                            possible[i][j][val]++;
                        }
                    }
                    game[i][j] = 0;
                    possible[i][j][0] = rowSum(possible[i][j]);  // At this point, possible[i][j][0] = 0 always.
                }
            }
        }
    }

    /**
     * Helper method to return the sum of the entries in a 1-D array.
     */
    private int rowSum(int[] a) {
        
        int rowSum = 0;
        
        for (int i=0; i<a.length; i++) {
            rowSum = rowSum + a[i];
        }
        return rowSum;
    }
    
    /**
     * Helper method to solve the hard parts of sudoku grids.  This method
     * is used in the cases where there is not a cell with only one option
     * available.  The method will store a copy of the game grid in the state
     * where the method is called, then search for the cell with the least
     * options, insert one of the options and try to solve the grid from there.
     * If successful, the game grid will be left in the completed state, if not
     * it will be reset to the state where the method was called, and the next
     * possible value for the minimally free cell will be tried.  The method
     * is recursive.
     */
    private void advancedSolver() {
        
        int[][] storeGame = new int[SIZE][SIZE];
        int[] minPossible = new int[2];
        int numberPoss;
        
        storeGame = this.clone(game);
        
        minPossible = this.minPoss();
        numberPoss = possible[minPossible[0]][minPossible[1]][0];
        
        int[] listMinPoss = new int[numberPoss];
        int n = 0;
        for (int k=1; k<SIZE+1; k++) {
            if (possible[minPossible[0]][minPossible[1]][k] != 0) {
                listMinPoss[n] = k;
                n++;
            }
        }
        
        for (int i=0; i<numberPoss; i++) {
            this.setCell(minPossible[0], minPossible[1], listMinPoss[i]);
            this.solveGame();
            if (this.isFinished()) {
                return;
            }
            else {
                game = clone(storeGame);
                for (int k=0; k<SIZE; k++) {
                    for (int l=0; l<SIZE; l++) {
                        if (game[k][l] == 0) {
                            gridState[k][l] = isEMPTY;
                        }
                    }
                }
                this.updatePossibles();
            }
        }
    }
    
    /**
     * Helper method to find the cell with the minimum number of possible
     * values.  The address of the cell is returned.
     */
    private int[] minPoss() {
        
    int min;
    int[] minLocation = new int[2];
    
    int x = 0;
    int y = 0;
    for (int i=0; i<SIZE; i++) {
        for (int j=0; j<SIZE; j++) {
            if (gridState[i][j] == isEMPTY) {
                x = i;
                y = j;
                break;
            }
        }
    }
    
    min = possible[x][y][0];
    minLocation[0] = x;
    minLocation[1] = y;
    
    for (int i=0; i<SIZE; i++) {
        for (int j=0; j<SIZE; j++) {
            if (gridState[i][j] == isEMPTY && possible[i][j][0] < min) {
                minLocation[0] = i;
                minLocation[1] = j;
            }
        }
    }
    return minLocation;
}
        
    
    /**
     * This method prints the current game configuration to the console.
     * It is adapted from the code given in the Tic Tac Toe program outline.
     */
    public void printGrid() {
        System.out.print('\u000C');    // clear the console window

        for(int x=0; x<SIZE-1; x++) {    // loop over 8 rows. Separate cells 
                                       // by |, and print ----- as a bottom edge.
            System.out.print(game[x][0] + " | " + game[x][1] + " | " + game[x][2] + " || " + game[x][3] + " | " + game[x][4] + " | " + game[x][5] + " || " + game[x][6] + " | " + game[x][7] + " | " + game[x][8]);
            if (x == 2 || x == 5) {
                System.out.println("\n===================================");
            }
            else {
                System.out.println("\n-----------------------------------"); 
            }
        }
       // Now print last row (with no bottom edge)
       System.out.print(game[8][0] + " | " + game[8][1] + " | " + game[8][2] + " || " + game[8][3] + " | " + game[8][4] + " | " + game[8][5] + " || " + game[8][6] + " | " + game[8][7] + " | " + game[8][8]);
            
    }

}
