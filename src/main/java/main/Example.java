package main;

import java.util.HashMap;
import java.util.Map;

import philosophers.arge.actor.visual.LineChart;
import philosophers.arge.actor.visual.Utils;

public abstract class Example {
	private long start, end;

	public final void run(int times) {
		if (!isValidArgument(times))
			throw new IllegalArgumentException("The argument is not valid");
		Map<Double, Double> parallelMetrics = new HashMap<>();
		Map<Double, Double> serialMetrics = new HashMap<>();
		try {
			double serial, parallel;
			for (int i = 0; i < times; i++) {
				parallel = (double) measureParallel();
				serial = (double) measureSerial();
				System.out.println("\tSpeedUp : " + (serial / parallel));
				System.out.println("---------");
				System.gc();

				parallelMetrics.put((double) i, parallel);
				serialMetrics.put((double) i, serial);

				onEpochCompleted();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			LineChart lineChart = new LineChart("iterition", "completation time",
					Utils.toXYDataSet("serial", serialMetrics), Utils.toXYDataSet("parallel", parallelMetrics)).draw();
		}
	}

	private boolean isValidArgument(int times) {
		return times >= 0;
	}

	private final long measureSerial() throws Exception {
		start = System.currentTimeMillis();
		runSerial();
		end = System.currentTimeMillis();
		System.out.println("T(Serial) :" + (end - start));
		return (end - start);
	}

	private final long measureParallel() throws Exception {
		start = System.currentTimeMillis();
		runParallel();
		end = System.currentTimeMillis();
		System.out.println("T(Parallel) :" + (end - start));
		return (end - start);
	}

	public abstract void runSerial() throws Exception;

	public abstract void runParallel() throws Exception;

	public abstract void onEpochCompleted();

}
