package model;

public class Candidate {
	
	
	private int id;
	private String name;
	private int partyID;
	private int constituencyID;
	private int voteCount;
	
	public int getID() {
		return id;
	}
	public void setID(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getPartyID() {
		return partyID;
	}
	public void setPartyID(int partyID) {
		this.partyID = partyID;
	}
	
	public int getConstID() {
		return constituencyID;
	}
	public void setConstID(int constituencyID) {
		this.constituencyID = constituencyID;
	}
	
	public int getVoteCount() {
		return voteCount;
	}
	public void setVoteCount(int voteCount) {
		this.voteCount = voteCount;
	}
}
