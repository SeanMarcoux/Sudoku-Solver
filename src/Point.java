import java.util.ArrayList;


public class Point {
	private int value, x, y;
	private ArrayList<Integer> possible;
	
	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
		value = 0;
		possible = new ArrayList();
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public int numPossible()
	{
		return possible.size();
	}
	
	public int getPossible(int index)
	{
		return possible.get(index);
	}
	
	public ArrayList getPossible()
	{
		return possible;
	}
	
	public void setValue(int value)
	{
		this.value = value;
	}
	
	public void addPossible(int possible)
	{
		this.possible.add(possible);
	}
	
	public void removePossible(int num)
	{
		for(int i=0; i<possible.size(); i++)
		{
			if(possible.get(i)==num)
				possible.remove(i);
		}
	}
	
	public void removePossible(ArrayList<Integer> has)
	{
		for(int i=0; i<possible.size(); i++)
		{
			for(int j=0; j<has.size(); j++)
			{
				if(possible.get(i)==has.get(j))
					possible.remove(i);
			}
		}
	}
	
	public void fillPossible()
	{
		possible.clear();
		if(value==0)
		{
			for(int i=1; i<=9; i++)
				possible.add(i);
		}
	}
	
	public void setPossible(ArrayList<Integer> possible)
	{
		this.possible = possible;
	}
	
	public int whichBox()
	{
		if(x<3)
		{
			if(y<3)
				return 1;
			else if(y<6)
				return 4;
			else
				return 7;
		}
		else if(x<6)
		{
			if(y<3)
				return 2;
			else if(y<6)
				return 5;
			else
				return 8;
		}
		else
		{
			if(y<3)
				return 3;
			else if(y<6)
				return 6;
			else
				return 9;
		}
	}
}
