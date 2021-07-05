package main.imagePipeline;

import main.Example;
import philosophers.arge.actor.ActorCluster;
import philosophers.arge.actor.ActorConfig;
import philosophers.arge.actor.ActorMessage;
import philosophers.arge.actor.ClusterConfig;
import philosophers.arge.actor.divisionstrategies.NumberBasedDivison;

public class ImageProcessing extends Example {
	private static final int LIMIT = 1000;

	// total : 15ms per image.
	private static final int READING_DELAY = 1; // ms
	private static final int FILTER_DELAY = 4; // ms
	private static final int RESHAPING_DELAY = 3; // ms
	private static final int FILTER2_DELAY = 3; // ms
	private static final int SERVING_DELAY = 2; // ms

	@Override
	public void runSerial() throws Exception {
		for (int i = 0; i < LIMIT; i++) {
			Image img = new Image(4, 4);

			// processing start
			Thread.sleep(READING_DELAY + FILTER2_DELAY + FILTER_DELAY + RESHAPING_DELAY + SERVING_DELAY);
			// processing end.
		}
	}

	@Override
	public void runParallel() throws Exception {
		ActorCluster cluster = new ActorCluster(new ClusterConfig());
		Reader startNode = importNodes(cluster);

		for (int i = 0; i < LIMIT; i++) {
			startNode.load(new ActorMessage<String>(String.format("Path[%s]", "cd../img" + i + ".jpg")));
		}
		startNode.executeNodeStack();
		cluster.waitForTermination(true);
	}

	private Reader importNodes(ActorCluster cluster) {

		Serve serveNode = new Serve(new ActorConfig<Image>(Serve.class.getSimpleName(), cluster.getRouter(),
				new NumberBasedDivison<Image>((long) 1)), SERVING_DELAY);
		cluster.addRootActor(serveNode);

		FilterY filterY = new FilterY(new ActorConfig<Image>(FilterY.class.getSimpleName(), cluster.getRouter(),
				new NumberBasedDivison<Image>((long) 1)), FILTER2_DELAY);
		cluster.addRootActor(filterY);

		Reshape reshape = new Reshape(new ActorConfig<Image>(Reshape.class.getSimpleName(), cluster.getRouter(),
				new NumberBasedDivison<Image>((long) 1)), RESHAPING_DELAY);
		cluster.addRootActor(reshape);

		FilterX filterX = new FilterX(new ActorConfig<Image>(FilterX.class.getSimpleName(), cluster.getRouter(),
				new NumberBasedDivison<Image>((long) 1)), FILTER_DELAY);
		cluster.addRootActor(filterX);

		Reader reader = new Reader(new ActorConfig<String>(Reader.class.getSimpleName(), cluster.getRouter(),
				new NumberBasedDivison<String>((long) 1)), READING_DELAY);
		cluster.addRootActor(reader);

		return reader;
	}

}
