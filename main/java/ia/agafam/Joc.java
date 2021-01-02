package ia.agafam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

public class Joc extends SurfaceView {
	
	// Descriptor per la graella hexagonal

    private int amplaGraella; 		// # de columnes
    private int altGraella; 		// # de files
    private int costatHexagon; 		// costat de l'hexagon
    private int alturaHexagon; 		// altura
    private int radiHexagon; 		// Radi
    private int totHexagon;  		// Altura i costat 
    private int amplaRectHexagon; 	// Amplada del rectangle mínim que conté l'hexagon
    private int altRectHexagon; 	// Altura del rectangle mínim que conté l'hexagon
    private int offsetX;			// auxiliars pel desplaçament de la graella
    private int offsetY;
    
    private Bitmap bmp, terraBmp, pedraBmp, bitxoBmp, bitxoTristBmp;   // imatges
    
    private SurfaceHolder holder;   // superficie de dibuix
    private LoopJoc jocLoopThread;  // Fil de pintat

    final int CASELLES_PER_FILA 	= 8;
    final int CASELLES_PER_COLUMNA 	= 8;
    
    private boolean graellaNecessitaPintar = false;
    private int orientacio;
    
    private Punt bitxo;
    private Punt bitxoOrigen;
    private Punt bitxoV;
    private Punt bitxoDesti;
    private Punt casellaPitjada = new Punt();
    
    private Context context;
    private boolean missatgeGuanyador, missatgePerdedor;
    private boolean jocAturat;
    private int indexAnimacio;
    private boolean mostraCami;
    private int corraletBitxo;
    private long recordActual;
    boolean DEBUG = false;

    private int ampladaText;
    private int maxX, maxY;
    private Button botoNou, botoCami, botoAjuda;

    Random ran = new Random();
   
    List<Punt> obstacles   = Collections.synchronizedList(new ArrayList<Punt>(CASELLES_PER_FILA*CASELLES_PER_COLUMNA));
    List<Punt> graella     = Collections.synchronizedList(new ArrayList<Punt>(CASELLES_PER_FILA*CASELLES_PER_COLUMNA));
    List<Punt> camiSolucio = Collections.synchronizedList(new ArrayList<Punt>(CASELLES_PER_FILA*CASELLES_PER_COLUMNA));
    List<Punt> corralet    = Collections.synchronizedList(new ArrayList<Punt>(CASELLES_PER_FILA*CASELLES_PER_COLUMNA));

    public Joc(Context cont)
    {
        super(cont);

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        maxX = metrics.widthPixels;
        maxY = metrics.heightPixels;

        ampladaText = maxX/15;
        int amplaBoto = maxX/5;
        int posicioBoto = maxX/4;
        botoNou   = new Button(this, R.drawable.noujocoff, R.drawable.noujocon, posicioBoto-amplaBoto/2, maxY-200, amplaBoto,amplaBoto);
        botoCami  = new Button(this, R.drawable.camioff, R.drawable.camion, posicioBoto*2-amplaBoto/2, maxY-200, amplaBoto,amplaBoto);
        botoAjuda = new Button(this, R.drawable.ajudaoff, R.drawable.ajudaon, posicioBoto*3-amplaBoto/2, maxY-200, amplaBoto,amplaBoto);


        mostraCami = false;
        context = cont;
        jocLoopThread = new LoopJoc(this);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder arg0) 
            {
            	initGame();
            	jocLoopThread.setRunning(true);
            	jocLoopThread.start();
            }
            public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) { }
            public void surfaceDestroyed(SurfaceHolder arg0) 
            { 
            	jocLoopThread.setRunning(false);
            }
        });
    }
    
    public boolean mostraCami()
    {
    	mostraCami = !mostraCami;
    	return mostraCami;
    }
    
    private int minim(int x, int y)
    {
    	if (x<y) return x;
    	else return y;
    }
    
    private int maxim(int x, int y)
    {
    	if (x>y) return x;
    	else return y;
    }
    
    public boolean noPucMoure(Punt start)
    {
    	Punt next;
    	
    	for (Direccio d: Direccio.values())
    	{
    		next = d.coordenadesVeinat(start);
    		if (!(obstacles.contains(next)) && casellaDinsGraella(next)) return false;
    	}
    	return true;
    }
    
    protected void computeMap()
    {
    	graellaNecessitaPintar = true; 
    }
    
    protected void setOrientation(int o)
    {
    	orientacio = o;
    }
 
    protected int  getOrientation()
    {
    	return(orientacio);
    }
    
    protected void updateMap()
    {
       	double apotema;
       	int windowWidth, windowHeight;

        int w = maxX;
        int h = maxY;

        amplaGraella = CASELLES_PER_FILA;
        altGraella   = CASELLES_PER_COLUMNA;
        
        if (DEBUG)
        {
        	System.out.println("Mapa actualitzat per Valors:"+getWidth()+" "+getHeight());
        	System.out.println("Mapa:"+orientacio);
        }
        

   		apotema = (int)(0.5*(minim(w,h)/(CASELLES_PER_FILA+1)));
    	costatHexagon = (int)(0.5+apotema/Math.cos(Math.PI/6));
    	if (DEBUG) System.out.println("Valors Horitzontal"+costatHexagon);
    	windowWidth  = maxim(w,h);
    	windowHeight = minim(w,h);
    	if (DEBUG) System.out.println("Valors finestra:"+windowWidth+"x"+windowHeight);


        radiHexagon = (int) (costatHexagon * Math.cos(Math.PI / 6));
        alturaHexagon = (int) (costatHexagon * Math.sin(Math.PI / 6));
        totHexagon = alturaHexagon + costatHexagon;
        amplaRectHexagon = 2 * alturaHexagon + costatHexagon;
        altRectHexagon = 2 * radiHexagon;    	
    	
        // igual per vertical i horitzontal, es pot canviar
        
        if (orientacio == Configuration.ORIENTATION_LANDSCAPE)
        {
            offsetX = (int)((windowWidth-(costatHexagon*1.5)*CASELLES_PER_FILA)/2);
            offsetY = (int)((windowHeight-altRectHexagon*CASELLES_PER_COLUMNA)/2);         	
        }
        else
        {
            offsetX = (int)((windowHeight-(costatHexagon*1.5)*CASELLES_PER_FILA)/2);
            offsetY = (int)((windowWidth-altRectHexagon*CASELLES_PER_COLUMNA)/2);         	
        }
        	    
        graella.clear();
    	for (int i = 0; i < amplaGraella; i++) 
            for (int j = 0; j< altGraella; j++) 
            	graella.add(new Punt(i,j));
        
    	graellaNecessitaPintar = false;
    }
    
    public void initGame()
    {
    	jocAturat = false;
    	missatgeGuanyador = false;
    	missatgePerdedor  = false;
    	
        obstacles.clear();
        camiSolucio.clear();
        corralet.clear();
        
       	recordActual = ((MainActivity) context).getPunts();

    	indexAnimacio = 0; // sequence image to be shown
    	bitxo = new Punt(CASELLES_PER_FILA/2,CASELLES_PER_COLUMNA/2);
        bitxoV = new Punt(0,0);
    	
    	if (getWidth()>getHeight())
            orientacio = Configuration.ORIENTATION_LANDSCAPE;
    	else
            orientacio = Configuration.ORIENTATION_PORTRAIT;
    	
    	updateMap();
    	bitxoOrigen = calculaCentre(bitxo.x, bitxo.y);
    	bitxoDesti  = calculaCentre(bitxo.x, bitxo.y);
       
    	bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.verd);

        // De 5 a 16 caselles aleatòries amb pedra

        int numCells = 5+ran.nextInt(12);

        //for (int i=0; i<numCells; i++)
        int i = 0;
        while (i<numCells)
        {
        	Punt p = new Punt(ran.nextInt(CASELLES_PER_FILA), ran.nextInt(CASELLES_PER_COLUMNA));
        	if (p.x != bitxo.x && p.y != bitxo.y) {
                obstacles.add(p);
                i++;
            }

        }
        
    	// resize images as needed
    	   
    	terraBmp= carregaImatgeEscalada(R.drawable.terra,  amplaRectHexagon, altRectHexagon);
        pedraBmp= carregaImatgeEscalada(R.drawable.pedra,  amplaRectHexagon, altRectHexagon);
    	bitxoBmp= carregaImatgeEscalada(R.drawable.riuverd, amplaRectHexagon*12, altRectHexagon);
    	bitxoTristBmp= carregaImatgeEscalada(R.drawable.ploraverd, amplaRectHexagon*12, altRectHexagon);    	
        partir();
    }
    
    protected void pintaBitxo(Canvas canvas, Punt pos, Punt p)
    {
    	if (missatgeGuanyador)
    	{
    		indexAnimacio = (indexAnimacio+1)%12;
    		pintaHexagon(canvas, bitxoTristBmp, p.x, p.y, 12);
    	}
    	else
    	if (missatgePerdedor)
    	{
    		indexAnimacio = (indexAnimacio+1)%12;
    		pintaHexagon(canvas, bitxoBmp, p.x, p.y, 12);
    	}
    	else    	
    	{
    		indexAnimacio= 0;
    		pintaHexagon(canvas, bitxoBmp, p.x, p.y, 12);
    	}
    }
    
    public void pintaHexagon(Canvas canvas, Bitmap bitmap, int x, int y, int numImatges) 
    {
    	Rect origen;
    	
    	int ampla = bitmap.getWidth()/numImatges;
    	int alt   = bitmap.getHeight();
    	
    	if (numImatges != 1)
    	{
            origen = new Rect(ampla*indexAnimacio, 0, ampla*indexAnimacio+ampla, alt);
    	}
    	else
            origen = new Rect(0, 0, ampla, alt);
    	
        double migAmpla = ampla*0.5;
        double migAlt   = alt*0.5;
        Rect   desti = new Rect((int)(x-migAmpla), (int)(y-migAlt), (int)(x-migAmpla + ampla), (int)(y-migAlt + alt));
        Paint paint = new Paint();    
        canvas.drawBitmap(bitmap, origen, desti, paint);
    }
    
    protected void pintaPantalla(Canvas canvas)
    {        
    	if (canvas == null) return; // don't do anything if canvas not ready

    	// Pinta tot el fons
    	Rect rorigen = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        Rect rdesti  = new Rect(0, 0, this.getWidth(), this.getHeight());
        canvas.drawBitmap(bmp, rorigen, rdesti, null);

        // Pinta els hexagons, obstacles i camins
        pintaGraella(canvas);
        
    	// Mira si encara s'ha de moure el bitxo o si ja ha arribat al seu desti (distància mínima = 5)
        if (distancia(bitxoOrigen, bitxoDesti) > 5)
        {
        	bitxoOrigen.x = bitxoOrigen.x+(int)(0.25*bitxoV.x);
        	bitxoOrigen.y = bitxoOrigen.y+(int)(0.25*bitxoV.y);
        }

        pintaBitxo(canvas, bitxo, bitxoOrigen);
        pintaBotons(canvas);

        // Escriu, si escau, els missatges de guanyador o perdedor

        int pixels = maxX/15;
        int color = Color.WHITE;
        int posY = maxY / 2;

        if (missatgeGuanyador) 
        {
        	escriuCentrat(canvas, "Has Guanyat !", pixels, color, posY-100);
        	escriuCentrat(canvas, "Punts: "+corraletBitxo, (int)(pixels*0.8), color, posY);
        	if (corraletBitxo > recordActual) recordActual = posaRecord(corraletBitxo);
        	escriuCentrat(canvas, "Record: "+recordActual+" per "+((MainActivity) context).getNom(), (int)(pixels*0.7), color, posY+100);
        }
        else
        if (missatgePerdedor) 
        {
        	escriuCentrat(canvas, "Has Perdut !", pixels, color, posY);
        }
    }
    
    public long posaRecord(long p)
    {
    	((MainActivity) context).posaNom();  					// Demana i guarda el nom de l'usuari
		recordActual = ((MainActivity) context).setPunts(p);  	// Guarda els punts del record
 		return p;
    }
    
    public int distancia(Punt p1, Punt p2)
    {
    	double difx, dify;
    	
    	difx = p2.x-p1.x;
    	dify = p2.y-p1.y;
    	
    	return (int) Math.sqrt(difx*difx+dify*dify);
    }
    
    public void escriuCentrat(Canvas canvas, String missatge, int size, int color, int posicio)
    {
        Paint paint = new Paint();
        paint.setTypeface(((GeneralActivity) context).fontJoc);
     	paint.setTextSize(size);
     	paint.setTextAlign(Align.CENTER);  
        paint.setColor(color); 
        
        // Centrat horitzontal	
        int xPos = (canvas.getWidth() / 2);

        // Centrat vertical de les lletres
        int yPos = (int) (posicio - ((paint.descent() + paint.ascent()))/4) ; 
       		        
        canvas.drawText(missatge, xPos, yPos, paint);	  	    	
    }
    
    public Bitmap carregaImatgeEscalada(int dibuix, int ampla, int alt)
    {

    	Bitmap tmpBitmap;
    	
    	tmpBitmap = BitmapFactory.decodeResource(getResources(), dibuix);

        return Bitmap.createScaledBitmap(tmpBitmap, ampla, alt, true);
    }

    public boolean onTouchEvent(MotionEvent event) 
    {
    	float posX, posY;
    	
        posX = event.getX();
        posY = event.getY();

    	switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (botoNou.contains(posX, posY))
                    botoNou.select();
                else if (botoCami.contains(posX, posY))
                    botoCami.select();
                else if (botoAjuda.contains(posX, posY))
                    botoAjuda.select();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                botoNou.unSelect();
                botoCami.unSelect();
                botoAjuda.unSelect();
                // Primer mira si hi ha algun botó pitjat
                if (botoNou.contains(posX, posY)) {
                    initGame();
                } else if (botoCami.contains(posX, posY)) {
                    mostraCami();
                } else if (botoAjuda.contains(posX, posY)) {
                    Intent intent = new Intent(context, HelpActivity.class);
                    context.startActivity(intent);
                } else { // està jugant
                    // Si tocas una casella, posa-hi una pedra !
                    if (jocAturat) return false;


                    Punt casella = dePixelACasella((int) (posX - offsetX), (int) (posY - offsetY));  // Quina casella he tocat ?

                    if (casellaDinsGraella(casella) && !obstacles.contains(casella) && !(casella.x == bitxo.x && casella.y == bitxo.y)) {
                        casellaPitjada.x = casella.x;
                        casellaPitjada.y = casella.y;

                        obstacles.add(new Punt(casella.x, casella.y));
                    } else return super.onTouchEvent(event);

                    camiSolucio.clear();  // inicialitzam el camí de la solució

                    // on vaig ? diu el bitxo
                    Node desti = new Cerca(this).calculaCasella(bitxo);  // Calcula la solució

                    if (desti != null)  // Es pot moure, continua el joc
                    {
                        Punt novaPosicio = calculaCentre(desti.x, desti.y);
                        bitxoV.x = novaPosicio.x - bitxoOrigen.x;
                        bitxoV.y = novaPosicio.y - bitxoOrigen.y;

                        bitxo.x = desti.x;
                        bitxo.y = desti.y;
                        bitxoDesti.x = novaPosicio.x;
                        bitxoDesti.y = novaPosicio.y;

                        if (!casellaDinsGraella(bitxo))
                            setMissatgePerdedor();
                    } else // aillat, el jugador guanya, calcula la puntuació amb les caselles buides
                    {
                        corraletBitxo = calculaArea(bitxo);
                        setMissatgeGuanyador();
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }
    
    public Punt casellaPitjada()
    {
    	return casellaPitjada;
    }

    public void setMissatgeGuanyador()
    {
    	missatgeGuanyador = true;
    	jocAturat = true;
    }
    
    public void setMissatgePerdedor()
    {
    	missatgePerdedor = true;
    	jocAturat = true;
    }
    
    public boolean hiHaObstacle(Punt casella)
    {
    	return obstacles.contains(casella);
    }

    private int calculaArea(Punt position) // calcula les caselles buides on s'ha aïllat el bitxo
    {
    	int area = 0;

    	if (!casellaDinsGraella(position)) return 0;   // Fora de límits, no hauria de passar mai, ja que el corralet està dins la graella
    	if (corralet.contains(position))   return 0;   // je l'hem visitat ?
        if (obstacles.contains(position))   return 0;  // frontera ?

        corralet.add(position);  // marca com a ja visitat
        area++;

    	// Compta recursivament els veinats no visitats
    	for (Direccio d: Direccio.values())
	        {
	        	if (d != Direccio.C) // don't take my position into account
	        	{
	        		Punt p=d.coordenadesVeinat(position);
	        		area+=calculaArea(p);
	        	}
	        }
    	return area;
    }

    public void aturar() 
    {
    	jocLoopThread.setRunning(false);
    }
    public void partir()
    {
    	jocLoopThread.setRunning(true);
    }

    protected void pintaBotons(Canvas g)
    {
        botoNou.draw(g);
        botoCami.draw(g);
        botoAjuda.draw(g);
    }

	protected void pintaGraella(Canvas g) 
	{
		if (graellaNecessitaPintar) 
		{
			int w = maxX;
			int h = maxY;

			offsetX = (int) ((w - (costatHexagon * 1.5) * CASELLES_PER_FILA) / 2);
			offsetY = (int) ((h - altRectHexagon * CASELLES_PER_COLUMNA) / 2);

			bitxoV = new Punt(0, 0);
			bitxoOrigen = calculaCentre(bitxo.x, bitxo.y);
			bitxoDesti = calculaCentre(bitxo.x, bitxo.y);
		}

		// Caselles del fons
		for (int i = 0; i < amplaGraella; i++) 
		{
			for (int j = 0; j < altGraella; j++) 
			{
				Punt p = calculaCentre(i, j);
				pintaHexagon(g, terraBmp, p.x, p.y, 1);
			}
		}

		
		// Caselles on hi ha obstacles
		synchronized (obstacles) 
		{
			for (Punt punt : obstacles) 
			{
				Punt p = calculaCentre(punt.x, punt.y);
				pintaHexagon(g, pedraBmp, p.x, p.y, 1);
			}
		}

		// La solució, si es vol
		if (mostraCami)
			synchronized (camiSolucio) 
			{
				for (Punt punt : camiSolucio)
					pintaPolygon(g, Style.STROKE, Color.RED,
							ferHexagon(punt.x, punt.y));
			}

		// Zona on es pot moure el bitxo una vegada bloquejat (per calcular la puntuació)
		synchronized (corralet) 
		{
			for (Punt punt : corralet)
				pintaPolygon(g, Style.STROKE, Color.RED,
						ferHexagon(punt.x, punt.y));
		}
	}
    
    public boolean esUnaSolucio(Punt p)
    {
    	return !graella.contains(p);
    }
    
    public void afegirNodeSolucio(Punt p)
    {
    	camiSolucio.add(p);
    }
    
    private void pintaPolygon(Canvas canvas, Paint.Style style,  int color, Punt[] punts) 
    {
        if (punts.length < 2)  // com a mínim una línia
        {
            return;
        }

        Paint polyPaint = new Paint();
        polyPaint.setColor(color);
        polyPaint.setStyle(style);
        polyPaint.setStrokeWidth(3);

        // path
        Path polyPath = new Path();
        polyPath.moveTo(punts[0].x, punts[0].y);
        
        int longitud;
        longitud = punts.length;
        
        for (int i = 0; i < longitud; i++) 
        {
            polyPath.lineTo(punts[i].x, punts[i].y);
        }
        polyPath.lineTo(punts[0].x, punts[0].y); // tanca el polígon i pinta'l
        canvas.drawPath(polyPath, polyPaint);
    }    

   Punt[] ferHexagon(int column, int row) 
   {
        Punt origin = deCasellaAPixel(column, row);
        return new Punt[]
        {
	        new Punt(offsetX+origin.x + alturaHexagon, offsetY+origin.y),
	        new Punt(offsetX+origin.x + totHexagon, offsetY+origin.y),
	        new Punt(offsetX+origin.x + amplaRectHexagon, offsetY+origin.y + radiHexagon),
	        new Punt(offsetX+origin.x + totHexagon, offsetY+origin.y + altRectHexagon),
	        new Punt(offsetX+origin.x + alturaHexagon, offsetY+origin.y + altRectHexagon),
	        new Punt(offsetX+origin.x, offsetY+origin.y + radiHexagon)
   		};
    }
   
    Punt calculaCentre(int column, int row) 
    {
	   int x, y;
       Punt origin = deCasellaAPixel(column, row);
        
       x = (int) (offsetX+origin.x+amplaRectHexagon*0.5);
       y = (int) (offsetY+origin.y+altRectHexagon*0.5);
       
       return new Punt(x,y);
    }
    
    Punt deCasellaAPixel(int column, int row) {
        Punt pixel = new Punt();
        pixel.x = totHexagon * column;
        if (Util.isOdd(column)) pixel.y = altRectHexagon * row;
        else pixel.y = altRectHexagon * row + radiHexagon;
        return pixel;
    }

    Punt dePixelACasella(int x, int y) {
        double divisio = (double) radiHexagon / (double) alturaHexagon;
        Punt p = new Punt(x / totHexagon, y / altRectHexagon);
        Punt r = new Punt(x % totHexagon, y % altRectHexagon);
        Direccio direccio;
        if (Util.isOdd(p.x)) { //odd column
            if (r.y < -divisio * r.x + radiHexagon) {
            	direccio = Direccio.NW;
            } else if (r.y > divisio * r.x + radiHexagon) {
            	direccio = Direccio.SW;
            } else {
            	direccio = Direccio.C;
            }
        } else { //even column
            if (r.y > divisio * r.x && r.y < -divisio * r.x + altRectHexagon) {
            	direccio = Direccio.NW;
            } else if (r.y < radiHexagon) {
            	direccio = Direccio.N;
            } else direccio = Direccio.C;
        }
        return new Punt(direccio.coordenadesVeinat(p));
    }

    public boolean casellaDinsGraella(Punt coordinates) {
         return graella.contains(coordinates);
    }
    public boolean casellaDinsGraella(int x, int y) {
        return graella.contains(new Punt(x,y));
    }
    public float getEstimatedDistanceToGoal(int startX, int startY)
    {
        // minimum distance to X=0, X=NUM_ROW_CELLS, Y=0 or Y=NUM_COL_CELLS
        int v1, v2, v3, v4;

        v1 = startX;
        v2 = startY;
        v3 = CASELLES_PER_FILA - startX;
        v4 = CASELLES_PER_COLUMNA - startY;

        return minim(v1, minim(v2, minim(v3, v4)));
    }
}
