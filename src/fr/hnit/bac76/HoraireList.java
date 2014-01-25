package fr.hnit.bac76;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HoraireList extends HashMap<Integer, List<Horaire>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5872170171460904029L;
	String from;
	Rive rive;
	
	public HoraireList(String from, Rive rive) {
		this.from = from;
		this.rive = rive;
		for (int i = 1 ; i <= 8 ; i++)
			this.put(i, new ArrayList<Horaire>());
	}
	
	public String toString() {
		return from+"\t"+rive+"\t"+super.toString();
	}

	public void addAll(int day, String[] asList) {
		if (asList == null ) {
			//TODO: NextBac.show("HoraireList.addAll(day, list): error null list");
			return;
		}
		Horaire[] listToAdd = new Horaire[asList.length];
		for (int i = 0 ; i < asList.length ; i++) {
			String str = asList[i];
			listToAdd[i] = new Horaire(str);
		}
		this.put(day, Arrays.asList(listToAdd));
	}
	
	public void addAll(String[] asList) {
		if (asList == null ) {
			//TODO: NextBac.show("HoraireList.addAll(list): error null list");
			return;
		}
		for (int day = 1 ; day <=7 ; day++) {
			addAll(day, asList);
		}
	}
}