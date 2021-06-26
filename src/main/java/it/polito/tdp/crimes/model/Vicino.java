package it.polito.tdp.crimes.model;

public class Vicino implements Comparable<Vicino>{
	
	private int distretto;
	private Double distanza;
	
	public Vicino(int distretto, Double distanza) {
		super();
		this.distretto = distretto;
		this.distanza = distanza;
	}

	public int getDistretto() {
		return distretto;
	}

	public void setDistretto(int distretto) {
		this.distretto = distretto;
	}

	public Double getDistanza() {
		return distanza;
	}

	public void setDistanza(Double distanza) {
		this.distanza = distanza;
	}

	@Override
	public int compareTo(Vicino altro) {
		return this.distanza.compareTo(altro.distanza);
	}

}
