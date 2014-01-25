package fr.hnit.bac76;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;

public class Horaires extends HashMap<String, HoraireList> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1531102114201278916L;
	
	public ArrayList<String> rights = new ArrayList<String>();
	public ArrayList<String> lefts = new ArrayList<String>();
	
	public void add(HoraireList hl) {
		this.put(hl.from, hl);
		if (hl.rive.equals(Rive.LEFT)) {
			lefts.add(hl.from);
		} else {
			rights.add(hl.from);
		}
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("RIGHTS: \n");
		for (String right : rights) {
			buffer.append(" "+this.get(right)+"\n");
		}
		buffer.append("\n");
		buffer.append("LEFTS: \n");
		for (String left : lefts) {
			buffer.append(" "+this.get(left)+"\n");
		}
		
		return buffer.toString();
	}

	
	public static void write(File binFile, Serializable p) throws IOException {
		FileOutputStream fos = new FileOutputStream(binFile);
		ObjectOutputStream oos= new ObjectOutputStream(fos);
		try {
			oos.writeObject(p); 
			oos.flush();
		} finally {
			try {
				oos.close();
			} finally {
				fos.close();
			}
		}
	}

	public static Horaires read(File binFile) throws StreamCorruptedException, IOException, ClassNotFoundException {
		Horaires horaires = null;
		FileInputStream fis = new FileInputStream(binFile);
		ObjectInputStream ois= new ObjectInputStream(fis);
		try {	
			Object p = ois.readObject(); 
			if (p instanceof Horaires)
				horaires = (Horaires)p;
			
		} finally {
			try {
				ois.close();
			} finally {
				fis.close();
			}
		}
		return horaires;
	}
}
