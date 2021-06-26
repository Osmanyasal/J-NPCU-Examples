package main;

public abstract class Example {
	private long start, end;

	public final void run() {
		try {
			measureParallel();
			System.out.println("-------");
			measureSerial();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void measureSerial() throws Exception {
		start = System.currentTimeMillis();
		runSerial();
		end = System.currentTimeMillis();
		System.out.println("T(Serial) :" + (end - start));
	}

	private final void measureParallel() throws Exception {
		start = System.currentTimeMillis();
		runParallel();
		end = System.currentTimeMillis();
		System.out.println("T(Parallel) :" + (end - start));
	}

	public abstract void runSerial() throws Exception;

	public abstract void runParallel() throws Exception;
}
