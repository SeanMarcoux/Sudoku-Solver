import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

public class SudokuSolver extends JFrame{
	private int[][] numbers = new int[9][9];
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
				numbers[x][y] = 0;
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
	}
	
	public int checkLines(int which)
	{
		int count = 9, count2 = 0, index =0;
		if(which==1)
		{
			for(int x=0; x<9; x++)
			{
				for(int y=0; y<9; y++)
				{
					if(numbers[x][y] == 0)
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
					if(numbers[y][x] == 0)
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
			
		}
		return index;
	}
	
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
					if(numbers[line[1]][x]==num.get(y))
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
					if(numbers[x][line[1]]==num.get(y))
						num.remove(y);
				}
			}
		}
		else
		{
			
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
			}
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
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
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
			char c = e.getKeyChar();
			System.out.println(c);
			if(!solving)
			switch(c)
			{
			case '1':
				if(xLoc>-1 && yLoc>-1)
				{
					numbers[yLoc][xLoc] = 1;
					labels[yLoc][xLoc].setText("1");
				}
				break;
			case '2':
				if(xLoc>-1 && yLoc>-1)
				{
					numbers[yLoc][xLoc] = 2;
					labels[yLoc][xLoc].setText("2");
				}
				break;
			case '3':
				if(xLoc>-1 && yLoc>-1)
				{
					numbers[yLoc][xLoc] = 3;
					labels[yLoc][xLoc].setText("3");
				}
				break;
			case '4':
				if(xLoc>-1 && yLoc>-1)
				{
					numbers[yLoc][xLoc] = 4;
					labels[yLoc][xLoc].setText("4");
				}
				break;
			case '5':
				if(xLoc>-1 && yLoc>-1)
				{
					numbers[yLoc][xLoc] = 5;
					labels[yLoc][xLoc].setText("5");
				}
				break;
			case '6':
				if(xLoc>-1 && yLoc>-1)
				{
					numbers[yLoc][xLoc] = 6;
					labels[yLoc][xLoc].setText("6");
				}
				break;
			case '7':
				if(xLoc>-1 && yLoc>-1)
				{
					numbers[yLoc][xLoc] = 7;
					labels[yLoc][xLoc].setText("7");
				}
				break;
			case '8':
				if(xLoc>-1 && yLoc>-1)
				{
					numbers[yLoc][xLoc] = 8;
					labels[yLoc][xLoc].setText("8");
				}
				break;
			case '9':
				if(xLoc>-1 && yLoc>-1)
				{
					numbers[yLoc][xLoc] = 9;
					labels[yLoc][xLoc].setText("9");
				}
				break;
			}
			
		}
	}
	
	class buttonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			solving = true;
			solve();
		}
		
	}
}
