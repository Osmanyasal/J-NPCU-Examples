package main.imagepipeline;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import philosophers.arge.actor.Actor;
import philosophers.arge.actor.ActorMessage;
import philosophers.arge.actor.configs.ActorConfig;

/**
 * Objective : read the image from dest address that's given as message and
 * deliver to first step of parocessing node.
 * 
 * @author osmanyasal
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@FieldNameConstants
public class Reader extends Actor<String> {
	private ActorConfig<String> config;
	private int delay;

	protected Reader(ActorConfig<String> config, int delay) {
		super(config);
		this.delay = delay;
		this.config = config;
	}

	@Override
	public void operate(ActorMessage<String> msg) {
		// reading
		Image img = readImg(msg.getMessage());

		// deliver
		FilterX nextActor = (FilterX) getRootActor(FilterX.class.getSimpleName());
		nextActor.sendByLocking(new ActorMessage<>(img));
	}

	@Override
	public Actor<String> generateChildActor() {
		return new Reader(config, delay);
	}

	private Image readImg(String from) {
		// System.out.println(String.format("reading image from(%s)", from));
		delay(delay);
		return new Image(4, 4);
	}

	private void delay(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			System.out.println("image reading interrupted!");
			e.printStackTrace();
		}
	}
}
