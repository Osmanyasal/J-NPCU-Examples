package main.wordcount;

import java.util.ArrayList;
import java.util.List;

import com.github.javafaker.Faker;

import main.Example;
import philosophers.arge.actor.ActorCluster;
import philosophers.arge.actor.ActorMessage;
import philosophers.arge.actor.ActorPriority;
import philosophers.arge.actor.ExecutorFactory.ThreadPoolTypes;
import philosophers.arge.actor.configs.ActorConfig;
import philosophers.arge.actor.configs.ClusterConfig;
import philosophers.arge.actor.Topic;
import philosophers.arge.actor.divisionstrategies.NumberBasedDivison;

public class WordCountExmpl extends Example {

	private final int LIMIT = 10_000_000;

	private List<String> namePool;
	private Faker faker;
	private ActorCluster cluster;
	private CounterNode counterNode;
	private String lookingFor;

	public WordCountExmpl() {
		init();
		filling();
		initNodes();
	}

	private void init() {
		namePool = new ArrayList<>();
		faker = new Faker();
		cluster = new ActorCluster(new ClusterConfig(ThreadPoolTypes.PRIORITIZED, false));
	}

	private void initNodes() {
		ActorConfig<List<String>> config = new ActorConfig<>(new Topic("nodeCounter"), cluster.getRouter(),
				new NumberBasedDivison<List<String>>(5l), ActorPriority.DEFAULT, null);
		this.counterNode = new CounterNode(config, lookingFor);
		cluster.addRootActor(counterNode);
	}

	private void filling() {
		for (int i = 0; i < LIMIT; i++)
			namePool.add(faker.shakespeare().hamletQuote());
		lookingFor = namePool.get(0).split(" ")[0]; // first word!!
	}

	public void runParallel() throws InterruptedException {

		// send message
		int range = LIMIT / 100;
		for (int i = 0; i < LIMIT; i += range)
			counterNode.sendByLocking(new ActorMessage<List<String>>(namePool.subList(i, i + range)));

		// wait duringe execution
		try {
			cluster.waitForTermination(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// collect result
		CounterNode temp = counterNode;
		long result = 0;
		while (temp != null) {
			result += temp.getCount();
			temp = (CounterNode) temp.getChildActor();
		}
		System.out.println("parallel res : " + result);
		// disable cluster
	}

	@Override
	public void runSerial() {
		long result = 0;
		for (int i = 0; i < LIMIT; i++) {
			if ((this.namePool.get(i)).contains(lookingFor))
				result++;
		}
		System.out.println("serial res :" + result);
	}

	@Override
	public void onEpochCompleted() {
		// TODO Auto-generated method stub

	}
}
