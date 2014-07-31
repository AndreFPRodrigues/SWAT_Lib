package hcim.auric.recognition;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import android.util.Log;

public class Labels {

	ArrayList<Label> list = new ArrayList<Label>();
	String path;

	class Label {
		String label;
		int num;

		public Label(String s, int n) {
			label = s;
			num = n;
		}
	}

	public Labels(String path) {
		this.path = path;
	}

	public boolean isEmpty() {
		return !(list.size() > 0);
	}

	public void add(String s, int n) {
		list.add(new Label(s, n));
	}

	public String get(int i) {
		Iterator<Label> Ilabel = list.iterator();
		while (Ilabel.hasNext()) {
			Label l = Ilabel.next();
			if (l.num == i)
				return l.label;
		}
		return "";
	}

	public int get(String s) {
		Iterator<Label> iterator = list.iterator();

		while (iterator.hasNext()) {
			Label l = iterator.next();
			if (l.label.equalsIgnoreCase(s))
				return l.num;
		}
		return -1;
	}

//	public void save() {
//		try {
//			File f = new File(path + "faces.txt");
//			f.createNewFile();
//			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
//			Iterator<Label> iterator = list.iterator();
//			
//			while (iterator.hasNext()) {
//				Label l = iterator.next();
//				bw.write(l.label + "," + l.num);
//				bw.newLine();
//			}
//			bw.close();
//		} catch (IOException e) {
//			Log.e("error", e.getMessage() + " " + e.getCause());
//		}
//
//	}
	
	public void save() {
		try {
			File f = new File(path + "faces.txt");
			f.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			
			for(Label l : list) {
				bw.write(l.label + "," + l.num);
				bw.newLine();
			}
			
			bw.close();
			
		} catch (IOException e) {
			Log.e("error", e.getMessage() + " " + e.getCause());
		}

	}

	public void read() {
		try {
			FileInputStream fstream = new FileInputStream(path + "faces.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fstream));

			String strLine;
			list = new ArrayList<Label>();
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				StringTokenizer tokens = new StringTokenizer(strLine, ",");
				String s = tokens.nextToken();
				String sn = tokens.nextToken();

				list.add(new Label(s, Integer.parseInt(sn)));
			}
			br.close();
			fstream.close();
		} catch (IOException e) {
			Log.e("error", e.getMessage() + " " + e.getCause());
		}
	}

	public int max() {
		int m = 0;
		Iterator<Label> Ilabel = list.iterator();
		while (Ilabel.hasNext()) {
			Label l = Ilabel.next();
			if (l.num > m)
				m = l.num;
		}
		return m;
	}

}
