import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;


//Note about 2d arrays: [row][column]
public class SudokuSolver extends JFrame{
	//private int[][] numbers = new int[9][9];
	private Point[][] numbers = new Point[9][9];
	private JLabel[][] labels = new JLabel[9][9];
	private JButton button = new JButton("Solve");
	private JPanel panel = new JPanel();
	private int xLoc = -1, yLoc = -1;
	private boolean solving = false;
	public SudokuSolver()
	{
		setLayout(new BorderLayout());
		panel.setLayout(new GridLayout(9,9));
		Font font;
		for(int x=0; x<9; x++)
			for(int y=0; y<9;y++)
			{
				numbers[x][y] = new Point(x, y);
				//numbers[x][y] = 0;
				labels[x][y] = new JLabel("");
				//labels[x][y].setText(" sswfew");
				labels[x][y].setBorder(BorderFactory.createLineBorder(Color.black));
				font = labels[x][y].getFont();
				labels[x][y].setFont(new Font(font.getName(), Font.PLAIN, 100));
				panel.add(labels[x][y]);
			}
		add(panel, BorderLayout.CENTER);
		add(button, BorderLayout.SOUTH);
		panel.addKeyListener(new key());
		panel.addMouseListener(new mouse());
		panel.setFocusable(true);
		button.addActionListener(new buttonListener());
	}
	public static void main(String[]args)
	{
		SudokuSolver frame = new SudokuSolver();
		frame.setTitle("Sudoku Solver");
		frame.setSize(920, 970);
		frame.setLocationRelativeTo(null); // Center the frame   
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		//frame.addKeyListener(new key());
	}
	
	public void solve()
	{
		int[] g = {1, checkLines(1)};
		ArrayList<Integer> f = has(g);
		for(int x=0; x<f.size(); x++)
			System.out.print(f.get(x) + " ");
		solveSingleLines();
		if(solved())
			System.out.println("Yay!");
		/*while(!solved())
		{
			System.out.println();
			int[] g = {1, checkLines(1)};
			System.out.println(checkLines(1));
			ArrayList<Integer> f = needed(g);
			for(int x=0; x<f.size(); x++)
				System.out.print(f.get(x) + " ");
			System.out.println();
			
			g[0] = 2;
			g[1] = checkLines(2);
			System.out.println(checkLines(2));
			f = needed(g);
			for(int x=0; x<f.size(); x++)
				System.out.print(f.get(x) + " ");
			System.out.println();
			
			g[0] = 3;
			g[1] = checkLines(3);
			System.out.println(g[1]);
			f = needed(g);
			for(int x=0; x<f.size(); x++)
				System.out.print(f.get(x) + " ");
			
			solveSingleLines();
		}*/
	}
	
	//TO DO: Implement method to check which numbers can fit in a spot
	//TO DO: Implement method to solve a spot if only one number fits
	//TO DO: Implement method to solve if it is the only spot in its row, column, or box that can hold a specific number
	
	//IDEA: Solve method will run the method that solves single spaces and the method to solve a spot if only one number fits until neither of them fill in a
	//		spot (they'll return false if they don't fill a spot). And then it'll run the method to solve if it is the only spot in its row, column, or box 
	//		that can hold a specific number. It'll loop between these until it is solved
	//		Obviously this won't solve everything, but I'll keep it that way until I implement methods for testing guesses. Once I have that, then it'll loop
	//		until none of those 3 methods fill in a spot and then it'll start guessing until it gets the real answer
	
	public void updatePossible()
	{
		ArrayList<Integer> possible = new ArrayList();
		int[] has = new int[2];
		for(int x=0; x<9; x++)
		{
			for(int y=0; y<9; y++)
			{
				if(numbers[x][y].getValue()==0)
				{
					//go through that point's box, row, and column and remove all values found
					numbers[x][y].fillPossible();
					has[0]=1;
					has[1]=y;
					numbers[x][y].removePossible(has(has));
					has[0]=2;
					has[1]=x;
					numbers[x][y].removePossible(has(has));
					has[0]=3;
					has[1]=numbers[x][y].whichBox();
					numbers[x][y].removePossible(has(has));
				}
			}
		}
	}
	
	//This will check if the puzzle is solveable or not. For now it only checks if there are the same numbers in a row or column
	public boolean solveable()
	{
		int[] array = new int[2];
		int[] limits;
		ArrayList<Integer> num;
		
		for(int x=0; x<9; x++)
		{
			array[1] = x;
			//Check for the row
			array[0] = 1;
			num = has(array);
			if(num.size() > 0)
			{
				for(int y=1; y<num.size(); y++)
				{
					if(num.get(y)==num.get(y-1))
					{
						return false;
					}
				}
			}
			//check for the column
			array[0] = 2;
			num = has(array);
			if(num.size() > 0)
			{
				for(int y=1; y<num.size(); y++)
				{
					if(num.get(y)==num.get(y-1))
					{
						return false;
					}
				}
			}
			//Check for the box
			array[1] = x+1;
			array[0] = 3;
			num = has(array);
			if(num.size() > 0)
			{
				for(int y=1; y<num.size(); y++)
				{
					if(num.get(y)==num.get(y-1))
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	//This method checks if the puzzle has been completely solved or not
	public boolean solved(){
		for(int x=0; x<9; x++)
		{
			for(int y=0; y<9; y++)
			{
				if(numbers[x][y].getValue() == 0)
					return false;
			}
		}
		return true;
	}
	
	//The method checks if there are any rows, columns, or boxes with only one empty spot and if so, fills it in
	public void solveSingleLines(){
		int[] array = new int[2];
		int[] limits;
		ArrayList<Integer> num;
		array[0] = 1;
		for(int x=0; x<9; x++)
		{
			array[1] = x;
			num = needed(array);
			if(num.size() == 1)
			{
				for(int y=0; y<9; y++)
				{
					if(numbers[x][y].getValue() == 0)
					{
						numbers[x][y].setValue(num.get(0));
						labels[x][y].setText(num.get(0) + "");
					}
				}
			}
		}
		array[0] = 2;
		for(int x=0; x<9; x++)
		{
			array[1] = x;
			num = needed(array);
			if(num.size() == 1)
			{
				for(int y=0; y<9; y++)
				{
					if(numbers[y][x].getValue() == 0)
					{
						numbers[y][x].setValue(num.get(0));
						labels[y][x].setText(num.get(0) + "");
					}
				}
			}
		}
		array[0] = 3;
		for(int x=1; x<=9; x++)
		{
			array[1] = x;
			num = needed(array);
			if(num.size() == 1)
			{
				limits = boxLimits(x);
				for(int y=limits[0]; y<=limits[1]; y++)
				{
					for(int z=limits[2]; z<=limits[3]; z++)
					{
						if(numbers[y][z].getValue() == 0)
						{
							numbers[y][z].setValue(num.get(0));
							labels[y][z].setText(num.get(0) + "");
						}
							
					}
				}
			}
		}
	}
	
	//This method checks every row (1), column (2), or box (3) to see which has the least open spaces in it
	public int checkLines(int which)
	{
		int count = 9, count2 = 0, index =0;
		if(which==1)
		{
			for(int x=0; x<9; x++)
			{
				for(int y=0; y<9; y++)
				{
					if(numbers[x][y].getValue() == 0)
						count2++;
				}
				if(count2<count)
				{
					index = x;
					count = count2;
				}
				count2 = 0;
			}
		}
		else if(which==2)
		{
			for(int x=0; x<9; x++)
			{
				for(int y=0; y<9; y++)
				{
					if(numbers[y][x].getValue() == 0)
						count2++;
				}
				if(count2<count)
				{
					index = x;
					count = count2;
				}
				count2 = 0;
			}
		}
		else
		{
			int[] limits;
			for(int x=1; x<=9; x++)
			{
				limits = boxLimits(x);
				for(int y=limits[0]; y<=limits[1]; y++)
				{
					for(int z=limits[2]; z<=limits[3]; z++)
					{
						if(numbers[y][z].getValue() == 0)
							count2++;
					}
				}
				if(count2<count)
				{
					index = x;
					count = count2;
				}
				count2 = 0;
			}
		}
		return index;
	}
	
	//method figures out what numbers are missing in a given row (line[0] = 1), column (line[0] = 2), or box (line[0] = 3)
	public ArrayList<Integer> needed(int[] line)
	{
		ArrayList<Integer> num = new ArrayList();
		for(int i=1; i<=9; i++)
			num.add(i);
		if(line[0]==1)
		{
			for(int x=0; x<9; x++)
			{
				for(int y=0; y<num.size();y++)
				{
					if(numbers[line[1]][x].getValue()==num.get(y))
						num.remove(y);
				}
			}
		}
		else if(line[0]==2)
		{
			for(int x=0; x<9; x++)
			{
				for(int y=0; y<num.size();y++)
				{
					if(numbers[x][line[1]].getValue()==num.get(y))
						num.remove(y);
				}
			}
		}
		else
		{
			int[] limits = boxLimits(line[1]);
			for(int x=limits[0]; x<=limits[1]; x++)
			{
				for(int y=limits[2]; y<=limits[3]; y++)
				{
					for(int z=0; z<num.size(); z++)
					{
						if(numbers[x][y].getValue()==num.get(z))
							num.remove(z);
					}
				}
			}
		}
		
		//sorts the array. Could be helpful, who knows
		int j, first, temp;  
	    for (int i = num.size() - 1; i > 0; i--)  
	    {
	    	first = 0;
	    	for(j = 1; j <= i; j ++)
	    	{
	    		if(num.get(j) > num.get(first))         
	    			first = j;
	    	}
	    	temp = num.get(first);
	    	num.set(first, num.get(i));
	    	num.set(i, temp); 
	    }
		return num;
	}
	
	//Gets the numbers that are already in the given row (line[0] = 1), column (line[0] = 2), or box (line[0] = 3)
	public ArrayList<Integer> has(int[] line)
	{
		ArrayList<Integer> num = new ArrayList();
		if(line[0]==1)
		{
			for(int x=0; x<9; x++)
			{
				if(numbers[line[1]][x].getValue()!=0)
					num.add(numbers[line[1]][x].getValue());
			}
		}
		else if(line[0]==2)
		{
			for(int x=0; x<9; x++)
			{
				if(numbers[x][line[1]].getValue()!=0)
					num.add(numbers[x][line[1]].getValue());
			}
		}
		else
		{
			int[] limits = boxLimits(line[1]);
			for(int x=limits[0]; x<=limits[1]; x++)
			{
				for(int y=limits[2]; y<=limits[3]; y++)
				{
					if(numbers[x][y].getValue()!=0)
						num.add(numbers[x][y].getValue());
				}
			}
		}
		//sorts the array. Could be helpful, who knows
		int j, first, temp;  
		for (int i = num.size() - 1; i > 0; i--)  
		{
			first = 0;
			for(j = 1; j <= i; j ++)
			{
				if(num.get(j) > num.get(first))         
					first = j;
			}
			temp = num.get(first);
			num.set(first, num.get(i));
			num.set(i, temp);
		}
		return num;
	}
	
	public int[] boxLimits(int box)
	{
		int[] limits = new int[4];
		if(box<=3)
		{
			limits[0] = 0;
			limits[1] = 2;
		}
		else if(box <= 6)
		{
			limits[0] = 3;
			limits[1] = 5;
		}
		else
		{
			limits[0] = 6;
			limits[1] = 8;
		}
		
		if(box%3==1)
		{
			limits[2] = 0;
			limits[3] = 2;
		}
		else if(box%3==2)
		{
			limits[2] = 3;
			limits[3] = 5;
		}
		else
		{
			limits[2] = 6;
			limits[3] = 8;
		}
					
		return limits;
	}
	
	class mouse implements MouseListener{
		@Override
		public void mouseClicked(MouseEvent e) {
			
		}
		
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mousePressed(MouseEvent e) {
			if(!solving)
			{
				int x = e.getX();
				int y = e.getY();
				if(x<100)
					xLoc = 0;
				else if(x<200)
					xLoc = 1;
				else if(x<300)
					xLoc = 2;
				else if(x<400)
					xLoc = 3;
				else if(x<500)
					xLoc = 4;
				else if(x<600)
					xLoc = 5;
				else if(x<700)
					xLoc = 6;
				else if(x<800)
					xLoc = 7;
				else
					xLoc = 8;
				
				if(y<100)
					yLoc = 0;
				else if(y<200)
					yLoc = 1;
				else if(y<300)
					yLoc = 2;
				else if(y<400)
					yLoc = 3;
				else if(y<500)
					yLoc = 4;
				else if(y<600)
					yLoc = 5;
				else if(y<700)
					yLoc = 6;
				else if(y<800)
					yLoc = 7;
				else
					yLoc = 8;
				System.out.println("click " + xLoc + " " + yLoc);
				System.out.println("x: " + numbers[xLoc][yLoc].getX() + " y: " + numbers[xLoc][yLoc].getY() + " box: " + numbers[xLoc][yLoc].whichBox());
				updatePossible();
				for(int i=0; i<numbers[xLoc][yLoc].getPossible().size(); i++)
					System.out.print(numbers[xLoc][yLoc].getPossible(i) + " ");
				System.out.println();
			}
		}
		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
	class key implements KeyListener
	{
		@Override
		public void keyPressed(KeyEvent e) {
			
		}
		@Override
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void keyTyped(KeyEvent e) {
			if(!solving)
			{
				char c = e.getKeyChar();
				System.out.println(c);
				switch(c)
				{
					case (char)KeyEvent.VK_BACK_SPACE:
					case (char)KeyEvent.VK_DELETE:
						if(xLoc>-1 && yLoc>-1)
						{
							numbers[yLoc][xLoc].setValue(0);
							labels[yLoc][xLoc].setText("");
						}
						break;
					case '1':
						if(xLoc>-1 && yLoc>-1)
						{
							numbers[yLoc][xLoc].setValue(1);
							labels[yLoc][xLoc].setText("1");
						}
						break;
					case '2':
						if(xLoc>-1 && yLoc>-1)
						{
							numbers[yLoc][xLoc].setValue(2);
							labels[yLoc][xLoc].setText("2");
						}
						break;
					case '3':
						if(xLoc>-1 && yLoc>-1)
						{
							numbers[yLoc][xLoc].setValue(3);
							labels[yLoc][xLoc].setText("3");
						}
						break;
					case '4':
						if(xLoc>-1 && yLoc>-1)
						{
							numbers[yLoc][xLoc].setValue(4);
							labels[yLoc][xLoc].setText("4");
						}
						break;
					case '5':
						if(xLoc>-1 && yLoc>-1)
						{
							numbers[yLoc][xLoc].setValue(5);
							labels[yLoc][xLoc].setText("5");
						}
						break;
					case '6':
						if(xLoc>-1 && yLoc>-1)
						{
							numbers[yLoc][xLoc].setValue(6);
							labels[yLoc][xLoc].setText("6");
						}
						break;
					case '7':
						if(xLoc>-1 && yLoc>-1)
						{
							numbers[yLoc][xLoc].setValue(7);
							labels[yLoc][xLoc].setText("7");
						}
						break;
					case '8':
						if(xLoc>-1 && yLoc>-1)
						{
							numbers[yLoc][xLoc].setValue(8);
							labels[yLoc][xLoc].setText("8");
						}
						break;
					case '9':
						if(xLoc>-1 && yLoc>-1)
						{
							numbers[yLoc][xLoc].setValue(9);
							labels[yLoc][xLoc].setText("9");
						}
						break;
				}
			}
		}
	}
	
	class buttonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(solveable())
			{
				System.out.println("here");
				solving = true;
				solve();
			}
			// PROBLEM: Can't edit shit after this???
			else
			{
				JOptionPane.showMessageDialog(null, "This puzzle is unsolveable. try changing it");
				System.out.println("ERROR");
				panel.setFocusable(true);
			}
		}
		
	}
}
