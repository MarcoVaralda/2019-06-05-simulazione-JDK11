package it.polito.tdp.crimes.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.crimes.db.EventsDao;
import it.polito.tdp.crimes.model.Evento.Tipo;

public class Simulatore {
	
	EventsDao dao ;
	
	// Eventi
	PriorityQueue<Evento> queue ;
	
	// Parametri
	Graph<Integer,DefaultWeightedEdge> grafo;
	int N;
	int anno;
	int mese;
	int giorno;
	Map<Integer,Integer> agenti;
	
	// Misure in uscita
	int malgestiti;
	
	
	public void init(int N, int anno, int mese, int giorno, Graph<Integer,DefaultWeightedEdge> grafo) {
		this.N=N;
		this.anno=anno;
		this.mese=mese;
		this.grafo=grafo;
		
		this.malgestiti=0;
		
		this.queue = new PriorityQueue<>();
		this.agenti = new HashMap<>();
		this.dao = new EventsDao();
		
		// Prendo il distretto a minor criminalità
		Integer distrettoMin = this.dao.getDistrettoMin(anno);
		
		// Riempio la mappa degli agenti
		for(Integer distretto : this.grafo.vertexSet())
			this.agenti.put(distretto, 0);
		
		// Inserisco tutti gli agenti nel distretto a minor criminalità
		this.agenti.put(distrettoMin, N);
		
		// Eventi iniziali --> Tutti i crimini a Denver del anno:mese:giorno
		for(Event e : this.dao.listAllEventsByDate(anno, mese, giorno))
			this.queue.add(new Evento(e,e.getReported_date(),Tipo.CRIMINE));
		
	}
	
	public int run() {		
		Evento e;
		while((e = queue.poll()) != null) {
			switch (e.getTipo()) {
			case CRIMINE: // --> Scelgo l'agente libero più vicino
				Integer distrettoPartenza = null;
				distrettoPartenza = cercaAgente(e.getCrimine().getDistrict_id());
				
				if(distrettoPartenza!=null) {
					// Calcolo il tempo che impiega l'agente a raggiungere il crimine
					this.agenti.put(distrettoPartenza, this.agenti.get(distrettoPartenza) - 1);
					Double distanza;
					if(distrettoPartenza.equals(e.getCrimine().getDistrict_id()))
						distanza = 0.0;
					else 
						distanza = this.grafo.getEdgeWeight(this.grafo.getEdge(distrettoPartenza, e.getCrimine().getDistrict_id()));
					
					Long seconds = (long) ((distanza * 1000)/(60/3.6));
					this.queue.add(new Evento(e.getCrimine(), e.getData().plusSeconds(seconds), Tipo.ARRIVA_AGENTE));
				}
				else { // Nessun agente libero --> crimine mal gestito
					this.malgestiti ++;
				}
				
				break;
				
			case ARRIVA_AGENTE:
				// Calcolo il tempo per gestire il crimine
				Long duration = getDurata(e.getCrimine().getOffense_category_id());
				this.queue.add(new Evento(e.getCrimine(),e.getData().plusSeconds(duration),Tipo.GESTITO));
				
				// Controllo se il crimine è stato mal gestito
				if(e.getData().isAfter(e.getCrimine().getReported_date().plusMinutes(15)))
					this.malgestiti ++;
				
				break;
				
			case GESTITO:
				this.agenti.put(e.getCrimine().getDistrict_id(), this.agenti.get(e.getCrimine().getDistrict_id())+1);
				break;
			}
		}
		return this.malgestiti;
	}

	private Long getDurata(String offense_category_id) {
		if(offense_category_id.equals("all_other_crimes")) {
			Random r = new Random();
			if(r.nextDouble() > 0.5)
				return Long.valueOf(2*60*60);
			else
				return Long.valueOf(1*60*60);
		} else {
			return Long.valueOf(2*60*60);
		}
	}

	private Integer cercaAgente(Integer district_id) {
		Double distanza = Double.MAX_VALUE;
		Integer distretto = null;
		
		for(Integer d : this.agenti.keySet()) {
			if(this.agenti.get(d) > 0) {
				if(district_id.equals(d)) {
					distanza = 0.0;
					distretto = d; 
				} else if(this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d)) < distanza) {
					distanza = this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d));
					distretto = d;
				}
			}
		}
		return distretto;
	}

}
