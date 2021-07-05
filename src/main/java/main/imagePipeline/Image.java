package main.imagePipeline;

import java.util.UUID;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Image {
	private String id;
	private int[][] matris;

	public Image(int x, int y) {
		id = UUID.randomUUID().toString();
		matris = new int[x][y];
	}
}
