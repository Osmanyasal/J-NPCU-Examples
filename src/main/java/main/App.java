package main;

import main.imagePipeline.ImageProcessing;
import main.wordCount.WordCountExmpl;

/**
 * Hello world!
 *
 */

public class App {

	public static void main(String[] args) {
		// new WordCountExmpl().run(0);
		new ImageProcessing().run(1);
	}
}
