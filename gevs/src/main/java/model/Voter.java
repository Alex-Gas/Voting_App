package model;

public class Voter extends User 
{
	private String email;
	private String name;
	private String dob;
	private String uvc;
	private String constituency;
	
	public Voter(String email, String name, String dob, String password, String uvc,  String constituency){
		setEmail(email);
		setName(name);
		setDob(dob); 
		setPassword(password);
		setUvc(uvc);
		setConstituency(constituency);
	}
	

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	
	public String getUvc() {
		return uvc;
	}
	public void setUvc(String uvc) {
		this.uvc = uvc;
	}
	
	public String getConstituency() {
		return constituency;
	}
	public void setConstituency(String constituency) {
		this.constituency = constituency;
	}
	
	
	
}
