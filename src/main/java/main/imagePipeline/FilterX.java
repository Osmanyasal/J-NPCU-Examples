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
public class FilterX extends Actor<Image> {

	private ActorConfig<Image> config;
	private int delay;

	protected FilterX(ActorConfig<Image> config, int delay) {
		super(config);
		this.config = config;
		this.delay = delay;
	}

	@Override
	public void operate(ActorMessage<Image> msg) {
		Image img = filterImage(msg.getMessage());
		Reshape rootActor = (Reshape) getRootActor(Reshape.class.getSimpleName());
		rootActor.sendByLocking(new ActorMessage<>(img));
	}

	private Image filterImage(Image message) {
		// System.out.println(String.format("[filterX] : filtering image (%s)",
		// message.getId().substring(0, 10)));
		delay(delay);
		return message;
	}

	@Override
	public Actor<Image> generateChildActor() {
		return new FilterX(config, delay);
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
