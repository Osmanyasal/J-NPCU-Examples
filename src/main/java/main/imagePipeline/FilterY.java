package main.imagePipeline;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import philosophers.arge.actor.Actor;
import philosophers.arge.actor.ActorConfig;
import philosophers.arge.actor.ActorMessage;

/**
 * Objective : filter the image as defined and deliver to next node.
 * 
 * @author osmanyasal
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@FieldNameConstants
public class FilterY extends Actor<Image> {

	private ActorConfig<Image> config;
	private int delay;

	protected FilterY(ActorConfig<Image> config, int delay) {
		super(config);
		this.config = config;
		this.delay = delay;
	}

	@Override
	public void operate(ActorMessage<Image> msg) {
		Image img = filterImage(msg.getMessage());
		Serve rootActor = (Serve) getRootActor(Serve.class.getSimpleName());
		rootActor.sendByLocking(new ActorMessage<>(img));
	}

	private Image filterImage(Image message) {
		// System.out.println(String.format("[filterY] : filtering image (%s)",
		// message.getId().substring(0, 10)));
		delay(delay);
		return message;
	}

	@Override
	public Actor<Image> generateChildActor() {
		return new FilterY(config, delay);
	}

	private void delay(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			System.out.println("image reading interrupted!");
		}
	}
}
