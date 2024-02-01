package model;

import java.util.List;

public class Constituency {
	
	private int id;
	private String name;
	private List<Candidate> candidates;
	
	public Constituency(int id, String name, List<Candidate> candidates) {
		this.id = id;
		this.name = name;
		this.candidates = candidates;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Candidate> getCandidates(){
		return candidates;
	}
	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}
}
