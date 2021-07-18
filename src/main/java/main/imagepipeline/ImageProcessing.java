package main.imagepipeline;

import java.util.ArrayList;
import java.util.List;

import main.Example;
import philosophers.arge.actor.Actor;
import philosophers.arge.actor.ActorCluster;
import philosophers.arge.actor.ActorMessage;
import philosophers.arge.actor.ActorPriority;
import philosophers.arge.actor.ExecutorFactory.ThreadPoolTypes;
import philosophers.arge.actor.Topic;
import philosophers.arge.actor.configs.ActorConfig;
import philosophers.arge.actor.configs.ClusterConfig;
import philosophers.arge.actor.divisionstrategies.NumberBasedDivison;

public class ImageProcessing extends Example {
	private static int LIMIT = 10;

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
		ActorCluster cluster = new ActorCluster(new ClusterConfig(ThreadPoolTypes.FIXED_SIZED, false));
		Reader startNode = importNodes(cluster);
		for (int i = 0; i < LIMIT; i++) {
			startNode.load(new ActorMessage<String>(String.format("Path[%s]", "cd../img" + i + ".jpg")));
		}
		startNode.executeNodeStack();
		cluster.waitForTermination(false);
		cluster.terminateCluster(true, false);
		System.out.println("end of program");
	}

	private Reader importNodes(ActorCluster cluster) {
		List<Actor<?>> filterYWaitList = new ArrayList<>();

		Serve serveNode = new Serve(new ActorConfig<Image>(new Topic(Serve.class.getSimpleName()), cluster.getRouter(),
				new NumberBasedDivison<Image>(500l), ActorPriority.MAX, null), SERVING_DELAY);
		cluster.addRootActor(serveNode);

		FilterY filterY = new FilterY(new ActorConfig<Image>(new Topic(FilterY.class.getSimpleName()),
				cluster.getRouter(), new NumberBasedDivison<Image>(500l), ActorPriority.MEDIUM, filterYWaitList),
				FILTER2_DELAY);
		cluster.addRootActor(filterY);

		Reshape reshape = new Reshape(new ActorConfig<Image>(new Topic(Reshape.class.getSimpleName()),
				cluster.getRouter(), new NumberBasedDivison<Image>(500l), ActorPriority.LOW, null), RESHAPING_DELAY);
		cluster.addRootActor(reshape);

		FilterX filterX = new FilterX(new ActorConfig<Image>(new Topic(FilterX.class.getSimpleName()),
				cluster.getRouter(), new NumberBasedDivison<Image>(500l), ActorPriority.VERY_LOW, null), FILTER_DELAY);
		cluster.addRootActor(filterX);

		Reader reader = new Reader(new ActorConfig<String>(new Topic(Reader.class.getSimpleName()), cluster.getRouter(),
				new NumberBasedDivison<String>(500l), ActorPriority.DEFAULT, null), READING_DELAY);
		cluster.addRootActor(reader);

		return reader;
	}

	@Override
	public void onEpochCompleted() {
		LIMIT = LIMIT * 5;
	}

}
