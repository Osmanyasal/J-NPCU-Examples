package main.imagepipeline;

import philosophers.arge.actor.Actor;
import philosophers.arge.actor.ActorMessage;
import philosophers.arge.actor.configs.ActorConfig;

public class Serve extends Actor<Image> {
	private ActorConfig<Image> config;
	private int delay;
	private static Integer count = 0;

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
//		System.out.println(
//				String.format("%s - delivering image [%s]", IncrementAndGetCount(), message.getId().subSequence(0, 6)));
	}

	private static final int IncrementAndGetCount() {
		synchronized (count) {
			count += 1;
		}
		return count;
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
