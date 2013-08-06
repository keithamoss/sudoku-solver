package sudoku;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import java.awt.Font;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import java.awt.event.*;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class SudokuGUI extends JFrame {
    // Dimensions config
    final int rows = 9;
    final int columns = 9;
    final int width = 500;
    final int height = 500;
    
    // Buttons initialisation
    JButton jbtnSolve = new JButton("Solve");
    JButton jbtnClear = new JButton("Clear");
    
    final JTextField[][] subPanels = new JTextField[rows][columns];
    final JPanel GuiPanel = new JPanel(new GridLayout(rows, columns));
    
    public SudokuGUI() {
        super("Sudoku Solver");
        
        // Init a panel to hold the 9x9 grid
//        final JPanel GuiPanel = new JPanel(new GridLayout(rows, columns));
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
        
        // Init the textfield container and associated fonts
//        final JTextField[][] subPanels = new JTextField[rows][columns];
        Font font = new Font("SansSerif", Font.BOLD, 20);

        // Initialise the grid
        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < columns; c++) {
                subPanels[r][c] = new JTextField("");
                subPanels[r][c].setForeground(Color.BLACK);
                subPanels[r][c].setFont(font);
                subPanels[r][c].setHorizontalAlignment(JTextField.CENTER);

                // Crossword-style colouring of the cells
//                if((r/1 + c/1) % 2 == 0) {
//                    subPanels[r][c].setBackground(Color.lightGray);
//                }
//				
                /* There's probably a better way to do this */
                // Corner border (left + top) for the four cells at corners of the thick border
                if((r == 3 && c == 3) || r == 6 && c == 6 || r == 3 && c == 6 || r == 6 && c == 3) {
                    subPanels[r][c].setBorder(thickCornerBorder);
                } else if(c == 3 || c == 6) {
                // Vertical thick border
                    subPanels[r][c].setBorder(thickVerticalBorder);
                } else if(r == 3 || r == 6) {
                // Horizontal thick border
                    subPanels[r][c].setBorder(thickHorizontalBorder);
                } else {
                // Standard border
                    subPanels[r][c].setBorder(thinBorder);
                }

                GuiPanel.add(subPanels[r][c]);
            }
        } 
	
        
        // Events for control bar buttons
        jbtnSolve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                solve();
            }
        });
        jbtnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        
        // Init a panel to hold the control bar
        final JPanel ButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ButtonPanel.setLayout(new BorderLayout());
        ButtonPanel.add(jbtnSolve);
        ButtonPanel.add(jbtnClear);
        ButtonPanel.setBorder(new LineBorder(Color.lightGray));
        ButtonPanel.setLayout(new BoxLayout(ButtonPanel, BoxLayout.X_AXIS));
        ButtonPanel.setLayout(new FlowLayout());
        add(ButtonPanel, BorderLayout.SOUTH);
        
        // We're done, display the whole GUI!
        setVisible(true);
    }
    
    public void solve() {
        int[][] numbers = get();
        
        // Magic goes here...
//        int[][] solved_numbers = get();
        
        set(numbers);
    }
    
    public void clear() {
        for (int r = 0; r < rows; r++)  {
            for (int c = 0; c < columns; c++) {
                subPanels[r][c].setText("");
            }
        }
    }
    
    private int[][] get() {
        String debug = "";
        int[][] numbers = new int[rows][columns];
        for (int r = 0; r < rows; r++)  {
            for (int c = 0; c < columns; c++) {
                debug += subPanels[r][c].getText() + ", ";
                if(!subPanels[r][c].getText().isEmpty()) {
                    numbers[r][c] = Integer.parseInt(subPanels[r][c].getText());
                }
            }
            debug += "\r\n";
        }
        JOptionPane.showMessageDialog(GuiPanel, "Hai!\r\n" + debug);
        System.out.println(numbers);
        return numbers;
    }
    
    private void set(int[][] numbers) {
        for (int r = 0; r < rows; r++)  {
            for (int c = 0; c < columns; c++) {
                subPanels[r][c].setText(Integer.toString(numbers[r][c]));
            }
        }
    }
    
    public static void main(String[] args) {
        SudokuGUI JPanel = new SudokuGUI();
    }
}
