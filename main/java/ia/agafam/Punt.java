package ia.agafam;

public class Punt {
	// x and y: coordenades a la graella (row, column)
	// posicioX, posicioY: coordenades a la pantalla (pixels)
	
	int x;
	int y;
	int posicioX;
	int posicioY;
	
	Punt()
	{
		super();
	}
	
	Punt(Punt p1)
	{
		this.x =p1.x;
		this.y =p1.y;
		posicioX = 0;
		posicioY = 0;
	}
	
	Punt(Punt p1, Punt p2)
	{
		this(p1);
		posicioX = p2.x;
		posicioY = p2.y;
	}
	
	Punt(int x, int y)
	{
		this.x = x;
		this.y = y;
		posicioX = 0;
		posicioY = 0;
	}
	
	Punt(int x, int y, int p1, int p2)
	{
		this(x,y);
		posicioX = p1;
		posicioY = p2;
	}
	
    @Override
    public boolean equals(Object other)  // Dos punts ón iguals si les seves components són iguals
    {
        if (other == null) return false;
        if (((Punt) other).x == this.x && ((Punt) other).y == this.y) return true;
        else return false;
    } 
	
}
