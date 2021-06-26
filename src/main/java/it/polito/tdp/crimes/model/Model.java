package it.polito.tdp.crimes.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;


import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	EventsDao dao = new EventsDao();
	Graph<Integer,DefaultWeightedEdge> grafo;
	List<Integer> vertici;
	
	// PUNTO 2
	Simulatore s;
	
	public String creaGrafo(int anno) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class); 
		
		// Aggiungo i vertici
		this.vertici = this.dao.getVertici();
		Graphs.addAllVertices(this.grafo,vertici);
		
		// Aggiungo gli archi
		for(Integer v1 : this.vertici)
			for(Integer v2 : this.vertici)
				if(!v1.equals(v2))
					if(!this.grafo.containsEdge(v1, v2)) {
						Double latV1 = this.dao.getLatMedia(anno, v1);
						Double longV1 = this.dao.getLongMedia(anno, v1);
						
						Double latV2 = this.dao.getLatMedia(anno, v2);
						Double longV2 = this.dao.getLongMedia(anno, v2);
						
						Double distanza = LatLngTool.distance(new LatLng(latV1,longV1),new LatLng(latV2,longV2),LengthUnit.KILOMETER);
						
						Graphs.addEdge(this.grafo,v1,v2,distanza);
					}
		
		
		return "Grfao creato!\n\nNumero vertici: "+this.grafo.vertexSet().size()+"\nNumeroArchi: "+this.grafo.edgeSet().size();
	}
	
	public String getAdiacenti() {
		List<Vicino> vicini ;
		String result= "\n\n";
		for(Integer v : this.vertici) {
			result += "Adiacenti a "+v+":\n";
			vicini = new LinkedList<>(); 
			for(DefaultWeightedEdge arco : this.grafo.edgesOf(v)) {
				Integer vicino = Graphs.getOppositeVertex(this.grafo,arco,v);
				double distanza = this.grafo.getEdgeWeight(arco);
				vicini.add(new Vicino(vicino,distanza));
			}
			Collections.sort(vicini);
			for(Vicino vv : vicini)
				result += vv.getDistretto()+"\n";
			result+="\n";
		}
		return result;
	}
	
	public List<Integer> getMesi() {
		return this.dao.getMesi();
	}
	
	public List<Integer> getGiorni() {
		return this.dao.getGiorni();
	}
	
	
	// PUNTO 2
	public String simula(int N, int anno, int mese, int giorno) {
		this.s = new Simulatore();
		
		this.s.init(N, anno, mese, giorno, this.grafo);
		
		return "Numero di crimini mal gestiti il "+giorno+"/"+mese+"/"+anno+": "+this.s.run()+"\n";
	}
	
}
