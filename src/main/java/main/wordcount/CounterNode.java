package main.wordcount;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import philosophers.arge.actor.Actor;
import philosophers.arge.actor.ActorMessage;
import philosophers.arge.actor.configs.ActorConfig;

@Getter
@Setter
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
	public Actor<List<String>> generateChildActor() {
		return new CounterNode(this.config, getLookingFor());
	}

	@Override
	public void operate(ActorMessage<List<String>> message) {
		List<String> msg = message.getMessage();

		// process
		for (int i = 0; i < msg.size(); i++) {
			if (msg.get(i).contains(lookingFor))
				count++;
		}
	}

}
