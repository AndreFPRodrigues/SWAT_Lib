package hcim.auric.recognition;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class Picture {

	private String name;
	private Bitmap img;

	public Picture(String name, Bitmap img) {
		this.img = img;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Bitmap getImage() {
		return img;
	}

	public static Bitmap[] getImages(Picture[] array) {
		Bitmap[] result = new Bitmap[array.length];

		for (int i = 0; i < result.length; i++) {
			result[i] = array[i].getImage();
		}
		return result;
	}

	public static String[] getNamesArray(Picture[] array) {
		String[] result = new String[array.length];

		for (int i = 0; i < result.length; i++) {
			result[i] = array[i].getName();
		}
		return result;
	}

	public static List<String> getNamesList(Picture[] array) {
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < array.length; i++) {
			result.add(array[i].getName());
		}
		return result;
	}

}
