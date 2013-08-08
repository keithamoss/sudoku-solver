package sudsolv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import java.awt.Font;
import java.awt.event.*;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JFormattedTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.text.NumberFormatter;

public class SudokuGUI extends JFrame {
    final int rows = 9;
    final int columns = 9;
    final int width = 600;
    final int height = 600;
    
    JButton jbtnStart = new JButton("Start");
    JButton jbtnSolve = new JButton("Solve");
    JButton jbtnNewGame = new JButton("New Game");
    JButton jbtnReset = new JButton("Reset");
    
    final JFormattedTextField[][] textfields = new JFormattedTextField[rows][columns];
    final JPanel GuiPanel = new JPanel(new GridLayout(rows, columns));
    SudokuPlayer player = null;
    
    public SudokuGUI() {
        super("Sudoku Solver");
        
        // Init a panel to hold the 9x9 grid
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(GuiPanel);
        setLocationRelativeTo(null);

        // Init borders for styling the grid
        Border outerBorder = BorderFactory.createLineBorder(Color.black, 2);
        Border thinBorder   = BorderFactory.createLineBorder(Color.BLACK, 1);
        Border thickVerticalBorder = BorderFactory.createMatteBorder(1, 3, 1, 1, Color.BLACK);
        Border thickHorizontalBorder = BorderFactory.createMatteBorder(3, 1, 1, 1, Color.BLACK);
        Border thickCornerBorder = BorderFactory.createMatteBorder(3, 3, 1, 1, Color.BLACK);
        GuiPanel.setBorder(outerBorder);
        
        // Initialise the NumberFormatter to limit textinputs to 1 - 9
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(1);
        formatter.setMaximum(9);
        formatter.setCommitsOnValidEdit(true); // commits on each keystroke instead of when focus is lost

        // Initialise the grid
        Font font = new Font("SansSerif", Font.BOLD, 20);
        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < columns; c++) {
                textfields[r][c] = new JFormattedTextField(formatter);
                textfields[r][c].setForeground(Color.BLACK);
                textfields[r][c].setFont(font);
                textfields[r][c].setHorizontalAlignment(JFormattedTextField.CENTER);
                /*textfields[r][c].getDocument().addDocumentListener(new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                      
                    }
                    public void removeUpdate(DocumentEvent e) {
                      
                    }
                    public void insertUpdate(DocumentEvent e) {
                      
                    }
                });*/
	
                /* There's a better way to do this */
                // Corner border (left + top) for the four cells at corners of the thick border
                if((r == 3 && c == 3) || r == 6 && c == 6 || r == 3 && c == 6 || r == 6 && c == 3) {
                    textfields[r][c].setBorder(thickCornerBorder);
                } else if(c == 3 || c == 6) {
                // Vertical thick border
                    textfields[r][c].setBorder(thickVerticalBorder);
                } else if(r == 3 || r == 6) {
                // Horizontal thick border
                    textfields[r][c].setBorder(thickHorizontalBorder);
                } else {
                // Standard border
                    textfields[r][c].setBorder(thinBorder);
                }

                GuiPanel.add(textfields[r][c]);
            }
        } 

        // Events for control bar buttons
        jbtnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        
        jbtnSolve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                solveGame();
            }
        });
        jbtnSolve.setEnabled(false);
        
        jbtnNewGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newGame();
            }
        });
        
        jbtnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
        jbtnReset.setEnabled(false);
        
        // Finally, init a panel to hold the control bar
        final JPanel ButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ButtonPanel.add(jbtnStart);
        ButtonPanel.add(jbtnNewGame);
        ButtonPanel.add(jbtnSolve);
        ButtonPanel.add(jbtnReset);
        ButtonPanel.setBorder(new LineBorder(Color.lightGray));
        ButtonPanel.setLayout(new FlowLayout());
        add(ButtonPanel, BorderLayout.SOUTH);
        
        // We're done, display the whole GUI!
        setVisible(true);
    }
    
    private void startGame() {
        for (int r = 0; r < rows; r++)  {
            for (int c = 0; c < columns; c++) {
                if(!textfields[r][c].getText().isEmpty()) {
                    textfields[r][c].setEditable(false);
                }
            }
        }
        
        player = new SudokuPlayer(getGrid());
        
        jbtnStart.setEnabled(false);
        jbtnSolve.setEnabled(true);
        jbtnReset.setEnabled(true);
    }
    
    private void solveGame() {
        int[][] numbers = getGrid();
        
        for (int r = 0; r < rows; r++)  {
            for (int c = 0; c < columns; c++) {
                if(!textfields[r][c].getText().isEmpty()) {
                    player.setCell(r, c, Integer.parseInt(textfields[r][c].getText()));
                }
            }
        }
        
        player.solveGame();
        setGrid(player.getGame());
    }
    
    private void newGame() {
        for (int r = 0; r < rows; r++)  {
            for (int c = 0; c < columns; c++) {
                textfields[r][c].setText("");
                textfields[r][c].setEditable(true);
            }
        }
        
        player = null;
        
        jbtnStart.setEnabled(true);
        jbtnSolve.setEnabled(false);
        jbtnReset.setEnabled(false);
    }
    
    private void resetGame() {
        if(player instanceof SudokuPlayer) {
            setGrid(player.getGame());
            player.reset();
        }
    }
    
    private int[][] getGrid() {
        int[][] numbers = new int[rows][columns];
        for (int r = 0; r < rows; r++)  {
            for (int c = 0; c < columns; c++) {
                if(!textfields[r][c].getText().isEmpty()) {
                    numbers[r][c] = Integer.parseInt(textfields[r][c].getText());
                }
            }
        }
        
//        String debug = "";
//        for (int r = 0; r < rows; r++)  {
//            for (int c = 0; c < columns; c++) {
//                debug += numbers[r][c] + ", ";
//            }
//            debug += "\r\n";
//        }
//        JOptionPane.showMessageDialog(GuiPanel, "Hai!\r\n" + debug);
//        System.out.println(numbers);
        
        return numbers;
    }
    
    private void setGrid(int[][] numbers) {
        NumberFormatter formatter = (NumberFormatter) textfields[0][0].getFormatter();
        int min_value = (int)formatter.getMinimum();
        int max_value = (int)formatter.getMaximum();
        
        for (int r = 0; r < rows; r++)  {
            for (int c = 0; c < columns; c++) {
                if(numbers[r][c] >= min_value && numbers[r][c] <= max_value) {
                    textfields[r][c].setText(Integer.toString(numbers[r][c]));
                } else {
                    textfields[r][c].setText("");
                }
            }
        }
    }
    
    public static void main(String[] args) {
        SudokuGUI JPanel = new SudokuGUI();
    }
}
