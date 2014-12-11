import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

//To do: improve solveOnlySingle method
//To do: clean up GUI (some thicker lines on the edges of the boxes would be nice)
//To do: implement guess and test system?? Is that necessary??? It probably is for the insanely hard puzzles, sigh
//Error: solveOnlyPossible isn't working right, apparently. But why (it's giving possibilities to things with values???) (pretty damn sure I fixed it)

//IDEA: Solve method will run the method that solves single spaces and the method to solve a spot if only one number fits until neither of them fill in a
	//		spot (they'll return false if they don't fill a spot). And then it'll run the method to solve if it is the only spot in its row, column, or box 
	//		that can hold a specific number. It'll loop between these until it is solved
	//		Obviously this won't solve everything, but I'll keep it that way until I implement methods for testing guesses. Once I have that, then it'll loop
	//		until none of those 3 methods fill in a spot and then it'll start guessing until it gets the real answer

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
				numbers[x][y] = new Point(y, x);
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
		/*Point point=new Point(1,1);
		point.addPossible(1);
		point.addPossible(2);
		point.addPossible(3);
		point.addPossible(4);
		ArrayList<Integer> array = new ArrayList();
		array.add(1);
		array.add(2);
		
		point.printPossible();
		if(point.contains(array))
			System.out.println("woo");
		else
			System.out.println("nope");*/
	}
	
	public void solve()
	{
		/*int[] g = {1, checkLines(1)};
		ArrayList<Integer> f = has(g);
		for(int x=0; x<f.size(); x++)
			System.out.print(f.get(x) + " ");*/
		//solveSingleLines();
		do
		{
			updatePossible();
		}while(solveOnlyPossible()||solvePossible());
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
	
	public ArrayList removeFromArrayList(ArrayList<Integer> array1, ArrayList<Integer> array2){
		for(int i=0; i<array1.size(); i++)
		{
			for(int j=0; j<array2.size(); j++)
			{
				if(array1.get(i)==array2.get(j))
					array1.remove(i);
				if(i>=array1.size())
					break;
			}
		}
		return array1;
	}
	
	//For each point this will update what numbers are possible there
	public void updatePossible()
	{
		ArrayList<Integer> shared = new ArrayList();
		int[] has = new int[2];
		int[] limits;
		for(int x=0; x<9; x++)
		{
			for(int y=0; y<9; y++)
			{
				if(numbers[x][y].getValue()==0)
				{
					//go through that point's box, row, and column and remove all values found
					numbers[x][y].fillPossible();
					has[0]=1;
					has[1]=x;
					numbers[x][y].removePossible(has(has));
					has[0]=2;
					has[1]=y;
					numbers[x][y].removePossible(has(has));
					has[0]=3;
					has[1]=numbers[x][y].whichBox();
					numbers[x][y].removePossible(has(has));
				}
			}
		}
		
		//PROBLEM: so yeah, this is a fundamentally flawed thing I did here. This only updates the stuff if EVERYTHING that is shared is not anywhere else. But
		//		   say they share 1, 2, 7, 8, 9 and the 8 is the only thing that necessarily has to go in that column, it won't properly update things
		//Update all the possibilities to remove stuff that has to necessarily appear somewhere else in the row (because it can't fit anywhere else in that box)
		boolean updated;
		//This loop makes sure all the possibilities are sufficiently updated, since who knows. Maybe by updating them like this it'll make there be some more
		//spots that need to be updated in the same way
		do{
			updated=false;
			for(int x=1; x<=9; x++)
			{
				limits = boxLimits(x);
				//First row of box
				shared = numbers[limits[0]][limits[2]].getShared(numbers[limits[0]][limits[2]+1].getShared(numbers[limits[0]][limits[2]+2].getPossible()));
				for(int y=limits[0]; y<=limits[1]; y++)
				{
					for(int z=limits[2]; z<=limits[3]; z++)
					{
						if(y!=limits[0]||(z!=limits[2]&&z!=limits[2]+1&&z!=limits[2]+2))
							shared = removeFromArrayList(shared, numbers[y][z].getPossible());
					}
				}
				for(int y=0; y<9; y++)
				{
					if(y!=limits[2]&&y!=limits[2]+1&&y!=limits[2]+2)
					{
						if(numbers[limits[0]][y].removePossible(shared))
							updated=true;
					}
				}
				//Second row
				shared = numbers[limits[0]+1][limits[2]].getShared(numbers[limits[0]+1][limits[2]+1].getShared(numbers[limits[0]+1][limits[2]+2].getPossible()));
				for(int y=limits[0]; y<=limits[1]; y++)
				{
					for(int z=limits[2]; z<=limits[3]; z++)
					{
						if(y!=limits[0]+1||(z!=limits[2]&&z!=limits[2]+1&&z!=limits[2]+2))
							shared = removeFromArrayList(shared, numbers[y][z].getPossible());
					}
				}
				for(int y=0; y<9; y++)
				{
					if(y!=limits[2]&&y!=limits[2]+1&&y!=limits[2]+2)
					{
						if(numbers[limits[0]+1][y].removePossible(shared))
							updated=true;
					}
				}
				//Third row
				shared = numbers[limits[0]+2][limits[2]].getShared(numbers[limits[0]+2][limits[2]+1].getShared(numbers[limits[0]+2][limits[2]+2].getPossible()));
				for(int y=limits[0]; y<=limits[1]; y++)
				{
					for(int z=limits[2]; z<=limits[3]; z++)
					{
						if(y!=limits[0]+2||(z!=limits[2]&&z!=limits[2]+1&&z!=limits[2]+2))
							shared = removeFromArrayList(shared, numbers[y][z].getPossible());
					}
				}
				for(int y=0; y<9; y++)
				{
					if(y!=limits[2]&&y!=limits[2]+1&&y!=limits[2]+2)
					{
						if(numbers[limits[0]+2][y].removePossible(shared))
							updated=true;
					}
				}
				//First column
				shared = numbers[limits[0]][limits[2]].getShared(numbers[limits[0]+1][limits[2]].getShared(numbers[limits[0]+2][limits[2]].getPossible()));
				for(int y=limits[0]; y<=limits[1]; y++)
				{
					for(int z=limits[2]; z<=limits[3]; z++)
					{
						if(z!=limits[2]||(y!=limits[0]&&y!=limits[0]+1&&y!=limits[0]+2))
							shared = removeFromArrayList(shared, numbers[y][z].getPossible());
					}
				}
				for(int y=0; y<9; y++)
				{
					if(y!=limits[0]&&y!=limits[0]+1&&y!=limits[0]+2)
					{
						if(numbers[y][limits[2]].removePossible(shared))
							updated=true;
					}
				}
				//Second column
				shared = numbers[limits[0]][limits[2]+1].getShared(numbers[limits[0]+1][limits[2]+1].getShared(numbers[limits[0]+2][limits[2]+1].getPossible()));
				for(int y=limits[0]; y<=limits[1]; y++)
				{
					for(int z=limits[2]; z<=limits[3]; z++)
					{
						if(z!=limits[2]+1||(y!=limits[0]&&y!=limits[0]+1&&y!=limits[0]+2))
							shared = removeFromArrayList(shared, numbers[y][z].getPossible());
					}
				}
				for(int y=0; y<9; y++)
				{
					if(y!=limits[0]&&y!=limits[0]+1&&y!=limits[0]+2)
					{
						if(numbers[y][limits[2]+1].removePossible(shared))
							updated=true;
					}
				}
				//Third column
				shared = numbers[limits[0]][limits[2]+2].getShared(numbers[limits[0]+1][limits[2]+2].getShared(numbers[limits[0]+2][limits[2]+2].getPossible()));
				for(int y=limits[0]; y<=limits[1]; y++)
				{
					for(int z=limits[2]; z<=limits[3]; z++)
					{
						if(z!=limits[2]+2||(y!=limits[0]&&y!=limits[0]+1&&y!=limits[0]+2))
							shared = removeFromArrayList(shared, numbers[y][z].getPossible());
					}
				}
				for(int y=0; y<9; y++)
				{
					if(y!=limits[0]&&y!=limits[0]+1&&y!=limits[0]+2)
					{
						if(numbers[y][limits[2]+2].removePossible(shared))
							updated=true;
					}
				}
			}
		}while(updated);
		
		//now detect any twins and update possibilities accordingly (for an explanation of this, check http://www.sudokudragon.com/sudokustrategy.htm)
		ArrayList<Integer> twins = new ArrayList();
		twins.add(0);
		twins.add(1);
		int[] location = new int[2];
		int count=0;
		boolean notTwins=false;
		//iterates through the boxes
		for(int x=1; x<=9; x++){
			limits = boxLimits(x);
			//iterates through the current box
			for(int y=limits[0]; y<=limits[1]; y++)
			{
				for(int z=limits[2]; z<=limits[3]; z++)
				{
					//iterates for every possible pair
					if(numbers[y][z].getPossible().size()>1){
						for(int a=0; a<numbers[y][z].getPossible().size()-1; a++){
							twins.set(0, numbers[y][z].getPossible(a));
							for(int b=a+1; b<numbers[y][z].getPossible().size(); b++){
								twins.set(1, numbers[y][z].getPossible(b));
								//iterates through every other point in the box
								for(int c=limits[0]; c<=limits[1]; c++)
								{
									for(int d=limits[2]; d<=limits[3]; d++)
									{
										if((c!=y||d!=z)&&numbers[c][d].contains(twins)){
											count++;
											location[0]=c;
											location[1]=d;
										}
									}
								}
								//check again through the box to make sure that nothing in the box contains either element of the twins
								for(int g=limits[0]; g<=limits[1]; g++)
								{
									for(int h=limits[2]; h<=limits[3]; h++)
									{
										if((!(g==y&&h==z))&&(!(g==location[0]&&h==location[1]))&&numbers[g][h].containsOne(twins))
											notTwins=true;
									}
								}
								//what to do if twins are found
								if(count==1&&(!notTwins))
								{
									System.out.println("twins stuff");
									System.out.println(y + " " + z);
									numbers[y][z].printPossible();
									System.out.println(location[0] + " " + location[1]);
									numbers[location[0]][location[1]].printPossible();
									for (Integer number : twins) {
								        System.out.print(number + " ");
								      } 
									System.out.println();System.out.println();System.out.println();
									
									
									
									/*a=numbers[y][z].getPossible().size()-1;
									b=numbers[y][z].getPossible().size();*/
									numbers[y][z].setPossible(twins);
									numbers[location[0]][location[1]].setPossible(twins);
									if(y==location[0]){
										for(int e=0; e<9; e++){
											if(e!=z&&e!=location[1]){
												numbers[y][e].removePossible(twins);
											}
										}
									}
									else if(z==location[1]){
										for(int f=0; f<9; f++){
											if(f!=y&&f!=location[0]){
												numbers[f][z].removePossible(twins);
											}
										}
									}
								}
								count=0;
								notTwins=false;
							}
						}
					}
				}
			}
		}
	}
	
	//If there is only one possibility at the site, it will fill it in
	public boolean solvePossible()
	{
		for(int x=0; x<9; x++)
		{
			for(int y=0; y<9; y++)
			{
				if(numbers[x][y].getPossible().size()==1)
				{
					numbers[x][y].setValue(numbers[x][y].getPossible(0));
					labels[x][y].setText("" + numbers[x][y].getPossible(0));
					numbers[x][y].fillPossible();
					return true;
				}
			}
		}
		return false;
	}
	
	//If this is the only space in a row, column, or box that something is possible, then it will fill that spot
	//This could be done more efficiently so that I don't have to call the update possible method so often.
	public boolean solveOnlyPossible()
	{
		int[] limits;
		for(int x=0; x<9; x++)
		{
			for(int y=0; y<9; y++)
			{
				if(numbers[x][y].getValue()==0)
				{
					for(int i=0; i<9; i++)
					{
						if(i!=x)
							numbers[x][y].removePossible(numbers[i][y].getPossible());
					}
					if(numbers[x][y].getPossible().size()==1)
					{
						numbers[x][y].setValue(numbers[x][y].getPossible(0));
						labels[x][y].setText("" + numbers[x][y].getPossible(0));
						numbers[x][y].fillPossible();
						return true;
					}
					
					updatePossible();
					for(int j=0; j<9; j++)
					{
						if(j!=y)
							numbers[x][y].removePossible(numbers[x][j].getPossible());
					}
					if(numbers[x][y].getPossible().size()==1)
					{
						numbers[x][y].setValue(numbers[x][y].getPossible(0));
						labels[x][y].setText("" + numbers[x][y].getPossible(0));
						numbers[x][y].fillPossible();
						return true;
					}
					updatePossible();
					
					limits=boxLimits(numbers[x][y].whichBox());
					for(int a=limits[0]; a<=limits[1]; a++)
					{
						for(int z=limits[2]; z<=limits[3]; z++)
						{
							if(a!=x||z!=y)
								numbers[x][y].removePossible(numbers[a][z].getPossible());
						}
					}
					if(numbers[x][y].getPossible().size()==1)
					{
						numbers[x][y].setValue(numbers[x][y].getPossible(0));
						labels[x][y].setText("" + numbers[x][y].getPossible(0));
						numbers[x][y].fillPossible();
						return true;
					}
					updatePossible();
				}
			}
		}
		return false;
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
	
	//Returns the boundary points of the box. [0] and [1] are the row limits and [2] and [3] are the column limits
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
			//if(!solving)
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
				/*System.out.println("click " + xLoc + " " + yLoc);
				System.out.println("value: " + numbers[yLoc][xLoc].getValue() + " x: " + numbers[yLoc][xLoc].getX() + " y: " + numbers[yLoc][xLoc].getY() + " box: " + numbers[yLoc][xLoc].whichBox());
				*/updatePossible();
				System.out.println("possible stuff");
				for(int i=0; i<numbers[yLoc][xLoc].getPossible().size(); i++)
					System.out.print(numbers[yLoc][xLoc].getPossible(i) + " ");
				System.out.println();System.out.println();System.out.println();
				/*for(int i=0; i<numbers[yLoc][xLoc].getShared(numbers[yLoc][xLoc+1].getPossible()).size(); i++)
					System.out.print(numbers[yLoc][xLoc].getShared(numbers[yLoc][xLoc+1].getPossible()).get(i) + " ");
				System.out.println();*/
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
				//System.out.println(c);
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
							numbers[yLoc][xLoc].fillPossible();
						}
						break;
					case '2':
						if(xLoc>-1 && yLoc>-1)
						{
							numbers[yLoc][xLoc].setValue(2);
							labels[yLoc][xLoc].setText("2");
							numbers[yLoc][xLoc].fillPossible();
						}
						break;
					case '3':
						if(xLoc>-1 && yLoc>-1)
						{
							numbers[yLoc][xLoc].setValue(3);
							labels[yLoc][xLoc].setText("3");
							numbers[yLoc][xLoc].fillPossible();
						}
						break;
					case '4':
						if(xLoc>-1 && yLoc>-1)
						{
							numbers[yLoc][xLoc].setValue(4);
							labels[yLoc][xLoc].setText("4");
							numbers[yLoc][xLoc].fillPossible();
						}
						break;
					case '5':
						if(xLoc>-1 && yLoc>-1)
						{
							numbers[yLoc][xLoc].setValue(5);
							labels[yLoc][xLoc].setText("5");
							numbers[yLoc][xLoc].fillPossible();
						}
						break;
					case '6':
						if(xLoc>-1 && yLoc>-1)
						{
							numbers[yLoc][xLoc].setValue(6);
							labels[yLoc][xLoc].setText("6");
							numbers[yLoc][xLoc].fillPossible();
						}
						break;
					case '7':
						if(xLoc>-1 && yLoc>-1)
						{
							numbers[yLoc][xLoc].setValue(7);
							labels[yLoc][xLoc].setText("7");
							numbers[yLoc][xLoc].fillPossible();
						}
						break;
					case '8':
						if(xLoc>-1 && yLoc>-1)
						{
							numbers[yLoc][xLoc].setValue(8);
							labels[yLoc][xLoc].setText("8");
							numbers[yLoc][xLoc].fillPossible();
						}
						break;
					case '9':
						if(xLoc>-1 && yLoc>-1)
						{
							numbers[yLoc][xLoc].setValue(9);
							labels[yLoc][xLoc].setText("9");
							numbers[yLoc][xLoc].fillPossible();
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
				for(int x=0; x<9; x++)
				{
					for(int y=0; y<9;y++)
					{
						if(numbers[x][y].getValue()==0)
							labels[x][y].setForeground(Color.RED);
					}
				}
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
