package main;

public abstract class Example {
	private long start, end;

	public final void run(int times) {
		try {
			double serial, parallel;
			for (int i = 0; i < times; i++) {
				parallel = (double) measureParallel();
				serial = (double) measureSerial();
				System.out.println("\tSpeedUp : " + (serial / parallel));
				System.out.println("---------");
				System.gc();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

}
