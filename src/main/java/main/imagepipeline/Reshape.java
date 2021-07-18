package main.imagepipeline;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import philosophers.arge.actor.Actor;
import philosophers.arge.actor.ActorMessage;
import philosophers.arge.actor.configs.ActorConfig;

/**
 * Objective : reshape image according to the consumer types
 * 
 * @author osmanyasal
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@FieldNameConstants
public class Reshape extends Actor<Image> {

	private ActorConfig<Image> config;
	private int delay;

	protected Reshape(ActorConfig<Image> config, int delay) {
		super(config);
		this.config = config;
		this.delay = delay;
	}

	@Override
	public void operate(ActorMessage<Image> msg) {
		//System.out.println("Reshape");
		Image img = reshapeImage(msg.getMessage());
		FilterY rootActor = (FilterY) getRootActor(FilterY.class.getSimpleName());
		rootActor.sendByLocking(new ActorMessage<Image>(img));
	}

	private Image reshapeImage(Image message) {
		delay(delay);
		return message;
	}

	@Override
	public Actor<Image> generateChildActor() {
		return new Reshape(config, delay);
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
