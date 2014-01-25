package fr.hnit.bac76;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;


public class BuildHoraires {
	File tsvFile;
	String encoding;

	/**
	 * 
	 * @param tsvFile Tabulation separator and no text separator
	 * @param encoding
	 */
	BuildHoraires(File tsvFile, String encoding) {
		this.tsvFile = tsvFile;
		this.encoding = encoding;
	}

	public Horaires createHoraires() throws Exception {

		Horaires horaires = new Horaires();

		FileInputStream freader = new FileInputStream(tsvFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(freader, encoding));

		// first line is header, skip it
		String line = reader.readLine();

		// read the TSV file
		line = reader.readLine();
		while (line != null) {
			String[] split = line.split("\t");
			int ncols = split.length;
			if (ncols >= 3) {
				if (!horaires.containsKey(split[0])) {
					Rive rive = Rive.valueOf(split[1]);
					horaires.put(split[0], new HoraireList(split[0], rive));
					if (rive.equals(Rive.LEFT)) {
						horaires.lefts.add(split[0]);
					} else {
						horaires.rights.add(split[0]);
					}
					
				} 
				
				HoraireList hl = horaires.get(split[0]);
				
				ArrayList<Horaire> times = new ArrayList<Horaire>();
				for (int c = 3 ; c < ncols ; c++) {
					Horaire h = new Horaire(split[c]);
					times.add(h);
				}
				
				String[] days = split[2].split(","); // get days for the column
				for (String day : days) {
					hl.put(Integer.parseInt(day), times);				
				}
			}
			
			line = reader.readLine();
		}
		reader.close();

		return horaires;
	}

	public static void main(String args[]) {
		File csvFile = new File("C:\\Documents and Settings\\H\\Bureau\\BAC76_2013.csv");
		File binFile = new File("E:\\workspaceAndroid\\BAC76\\res\\xml\\Horaires.bin");
		String encoding = "UTF-8";
		BuildHoraires bh = new BuildHoraires(csvFile, encoding);
		try {
			Horaires horaires = bh.createHoraires();
			//System.out.println(horaires);
			Horaires.write(binFile, horaires);
			System.out.println("Result saved in "+binFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
