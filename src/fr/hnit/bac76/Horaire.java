package fr.hnit.bac76;
import java.io.Serializable;



public class Horaire implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3846302829257086450L;
	
	String str;
	int time=0;
	int h=0,m=0;
	public Horaire(String str) {
		this.str = str;
		String[] split = str.split(":", 2);
		//if (split == null || split.length != 2) split = str.split("h", 2);
		if (split == null) {
			//TODO: NextBac.show("error creating horaire "+str);
			return;
		}
		try {
			this.h = (60 * Integer.parseInt(split[0]));
			this.m = Integer.parseInt(split[1]);
		} catch(Exception e) {
			//TODO: NextBac.show("Integer error: '"+str+"'");
		}
		this.time = h + m;
	}
		
	public int compareTo(Horaire h) {
		return this.time = h.time;
	}
	
	public int hashcode() {
		return time;
	}
	
	public String toString() {
		return str;
	}
}
