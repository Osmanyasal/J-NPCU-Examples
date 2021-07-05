package main.imagePipeline;

import philosophers.arge.actor.Actor;
import philosophers.arge.actor.ActorConfig;
import philosophers.arge.actor.ActorMessage;

public class Serve extends Actor<Image> {
	private ActorConfig<Image> config;
	private int delay;

	protected Serve(ActorConfig<Image> config, int delay) {
		super(config);
		this.config = config;
		this.delay = delay;
	}

	@Override
	public void operate(ActorMessage<Image> msg) {
		deliver(msg.getMessage());
	}

	private void deliver(Image message) {
		delay(delay);
		// System.out.println(String.format("[Deliver] : image (%s) delivered",
		// message.getId().substring(0, 10)));
	}

	@Override
	public Actor<Image> generateChildActor() {
		return new Serve(config, delay);
	}

	private void delay(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			System.out.println("serve interrupted!");
		}
	}
}
