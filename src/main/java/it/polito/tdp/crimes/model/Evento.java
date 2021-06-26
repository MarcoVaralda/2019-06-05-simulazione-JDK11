package it.polito.tdp.crimes.model;

import java.time.LocalDateTime;

public class Evento implements Comparable<Evento>{
	
	public enum Tipo {
		CRIMINE,
		ARRIVA_AGENTE,
		GESTITO
	}
	
	private Event crimine;
	private LocalDateTime data;
	private Tipo tipo;
	
	public Event getCrimine() {
		return crimine;
	}


	public LocalDateTime getData() {
		return data;
	}


	public Tipo getTipo() {
		return tipo;
	}


	public Evento(Event crimine, LocalDateTime data, Tipo tipo) {
		this.crimine = crimine;
		this.data = data;
		this.tipo = tipo;
	}


	@Override
	public int compareTo(Evento altro) {
		return this.data.compareTo(altro.getData());
	}

}
