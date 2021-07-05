package main.imagePipeline;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import philosophers.arge.actor.Actor;
import philosophers.arge.actor.ActorConfig;
import philosophers.arge.actor.ActorMessage;

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
		Image img = reshapeImage(msg.getMessage());
		FilterY rootActor = (FilterY) getRootActor(FilterY.class.getSimpleName());
		rootActor.sendByLocking(new ActorMessage<Image>(img));
	}

	private Image reshapeImage(Image message) {
		//System.out.println(String.format("[Reshape] : reshaping image (%s)", message.getId().substring(0, 10)));
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
