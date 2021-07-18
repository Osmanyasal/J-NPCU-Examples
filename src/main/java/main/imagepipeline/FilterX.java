package main.imagepipeline;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import philosophers.arge.actor.Actor;
import philosophers.arge.actor.ActorMessage;
import philosophers.arge.actor.configs.ActorConfig;

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
		//System.out.println("X[filter]");
		Image img = filterImage(msg.getMessage());
		Reshape rootActor = (Reshape) getRootActor(Reshape.class.getSimpleName());
		rootActor.sendByLocking(new ActorMessage<>(img));
	}

	private Image filterImage(Image message) {
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
