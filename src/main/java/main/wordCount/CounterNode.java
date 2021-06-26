package main.wordCount;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import philosophers.arge.actor.Actor;
import philosophers.arge.actor.ActorConfig;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CounterNode extends Actor<List<String>> {
	private ActorConfig<List<String>> config;
	private String lookingFor;
	private long count;

	protected CounterNode(ActorConfig<List<String>> config, String lookingFor) {
		super(config);
		this.config = config;
		this.lookingFor = lookingFor;
		this.count = 0;
	}

	@Override
	public void operate() {
		while (!getQueue().isEmpty()) {

			// get next message
			List<String> msg = deq().getMessage();

			// process
			for (int i = 0; i < msg.size(); i++) {
				if (msg.get(i).contains(lookingFor))
					count++;
			}
		}
	}

	@Override
	public Actor<List<String>> generateChildActor() {
		return new CounterNode(this.config, getLookingFor());
	}

}
