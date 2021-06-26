package main.wordCount;

import java.util.ArrayList;
import java.util.List;

import com.github.javafaker.Faker;

import main.Example;
import philosophers.arge.actor.ActorCluster;
import philosophers.arge.actor.ActorConfig;
import philosophers.arge.actor.ActorMessage;
import philosophers.arge.actor.ClusterConfig;
import philosophers.arge.actor.NumberBasedDivison;

public class WordCountExmpl extends Example {

	private final int LIMIT = 50_000_000;

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
		cluster = new ActorCluster(new ClusterConfig());
	}

	private void initNodes() {
		ActorConfig<List<String>> config = new ActorConfig<>("nodeCounter", cluster.getRouter(),
				new NumberBasedDivison<List<String>>(5l));
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
			counterNode.sendByLocking(new ActorMessage<List<String>>().setMessage(namePool.subList(i, i + range)));

		// wait duringe execution
		cluster.waitForTermination();

		// collect result
		CounterNode temp = counterNode;
		long result = 0;
		while (temp != null) {
			result += temp.getCount();
			temp = (CounterNode) temp.getChildActor();
		}
		System.out.println("parallel res : " + result);

		// disable cluster
		cluster.getPool().shutdown();
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
}
