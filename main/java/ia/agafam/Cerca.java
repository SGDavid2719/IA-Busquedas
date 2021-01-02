package ia.agafam;

import java.util.ArrayList;

public class Cerca {

	private ArrayList<Node> llista;
	private Joc graella;

	Cerca(Joc graella)
	{
		this.graella = graella;
		llista = new ArrayList<Node>();
	}

	/*
	Return node si hay camino, return null si pierde

	Búsqueda en profundidad iterativa, recorre en profundidad con las ventajas de la búsqueda en
	amplitud.
	*/
	public Node calculaCasella(Punt inici)
	{
		// Define la profundidad a mirar
		int profundidad = 0;
		// Define la profundidad total a la que estamos
		int profundidadActual = 0;
		// Define el tope en profundidad a mirar
		int profundidadMaxima = 50;

		// Lista de cerrados
		ArrayList<Node> cerrados = new ArrayList<Node>();
		// Lista temporal de abiertos
		ArrayList<Node> tmp = new ArrayList<Node>();

		// Repetir
		while(profundidadActual<profundidadMaxima)
		{
			// Reiniciamos abiertos cada vez que modificamos la profundidad
			llista.clear();
			graella.camiSolucio.clear();
			cerrados.clear();
			// Volvemos al inicio
			llista.add(new Node(inici.x, inici.y));

			// Jugamos mientras se pueda mover
			if(graella.noPucMoure(inici))
			{
				return null;
			}

			// Mientras no esté vacía la lista
			while(!llista.isEmpty())
			{
				// Obtenemos el primer nodo de la lista de abiertos
				Node actual = llista.get(0);
				llista.remove(0);

				// Miramos si es una solución, debe ser profundidad 0 porque sino esa solución ya la
				// hemos mirado
				if(!graella.casellaDinsGraella(new Punt(actual.getX(), actual.getY())) && profundidad == 0)
				{
					graella.afegirNodeSolucio(new Punt(actual.getX(),actual.getY()));
					Node previ = actual.getNodePrevi();
					while(!(new Node(inici.x, inici.y).equals(previ)))
					{
						graella.afegirNodeSolucio(new Punt(previ.getX(),previ.getY()));
						previ = previ.getNodePrevi();
					}
					previ = new Node(graella.camiSolucio.get(graella.camiSolucio.size()-1).x, graella.camiSolucio.get(graella.camiSolucio.size()-1).y);
					return previ;
				}
				else if (profundidad > 0) // Si profundidad mayor que 0 debemos mirar los hijos
				{
					// Cerramos el nodo
					cerrados.add(actual);
					for(Direccio d: Direccio.values())
					{
						if(d != Direccio.C)
						{
							Punt p = d.coordenadesVeinat(new Punt(actual.getX(), actual.getY()));
							Node n = new Node(p.x, p.y);
							// Tratamiento de repetidos
							if(!graella.hiHaObstacle(p) && !llista.contains(n) && !tmp.contains(n) && !cerrados.contains(n))
							{
								tmp.add(n);
								n.setNodePrevi(actual);
							}
						}
					}
					if(llista.isEmpty())
					{
						// Decrementamos la profundidad para volver a los nodos superiores
						profundidad--;

						// Si hemos terminado con los nodos superiores y la lista no está vacía copiamos en
						// abiertos los nodos a mirar como posibles soluciones
						llista.addAll(tmp);
						tmp.clear();
					}
				}
				else // Si no es solución cerramos el nodo
				{
					cerrados.add(actual);
				}
			}
			// Si la lista está vacía porque profundidad es 0 y la lista de nuevos nodos también está
			// vacía, se procede a incrementar la profundidad

			// La profundidad a mirar será la actual más 1
			profundidad = profundidadActual+1;

			// Se actualiza la nueva profundidad actual
			profundidadActual = profundidad;
		}
		return null;
	}
}

