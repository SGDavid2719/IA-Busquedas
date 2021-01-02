package ia.agafam;

public enum Direccio {
    N(0, -1, -1),    
    NE(1, 0, -1),
    SE(1, 1, 0),
    S(0, 1, 1),
    SW(-1, 1, 0),
    NW(-1, 0, -1),
    C(0, 0, 0);
    private final int incColumna;
    private final int incFilaParell;
    private final int incFilaSenar;

    // incX:       increment de X per anar a la seguent posició en aquesta direcció
    // incYParell: increment de Y si la columna és parell
    // incYSenar:  increment de Y si la columna és senar
    
    private Direccio(final int incX, final int incYParell, final int incYSenar) {
        this.incColumna = incX;
        this.incFilaParell = incYParell;
        this.incFilaSenar = incYSenar;
    }

    public int incColumna()    { return incColumna;}
    public int incFilaParell() { return incFilaParell;}
    public int incFilaSenar()  { return incFilaSenar;}

    // A quines coordenades es troba el punt en la direcció donada ?
    
    public Punt coordenadesVeinat(Punt coord) {
        int columna = coord.x + incColumna();
        int fila = coord.y + (Util.isEven(coord.x) ? incFilaParell() : incFilaSenar());
        return new Punt(columna, fila, coord.posicioX, coord.posicioY);
    }
}
