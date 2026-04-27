package idfactory;

import java.util.concurrent.atomic.AtomicLong;

public class UniqueTimestampGenerator {

	private static AtomicLong ts = new AtomicLong(System.currentTimeMillis());
	
	// instrumentation used only for testing
	private static boolean verbose = false;

	
	/**
	 * Provides a unique value approximately equal to the current long milliseconds time value.
	 * Multiple calls made within the same millisecond will return a value artificially incremented 
	 * by 1 ms relative to the most recent value the utility returned. This ensures every call 
	 * gets a unique value.
	 * 
	 * A typical program's flow will not need to continuously pull unique timestamps at a high rate,
	 * but if one does it will cause the returned values to gradually climb away from the actual 
	 * clock time.
	 * 
	 * The typical case might include running a fraction of a second ahead for a short time, but then
	 * diverting to other processing allowing the wall clock to overtake the internal counter.
	 * 
	 * @return a unique long value based roughly on the current system clock milliseconds
	 */
	public static long get() {
		boolean success=false;
		long now, was;
		int tries=0;
		do {
			now = System.currentTimeMillis();
			was = ts.get();
			++tries;
			if (now <= was) {
				// timestamp collided on an already used millisecond. increment to avoid. this is the only special case.
				// why test < ? Because the faked millis could easily advance faster than the wall clock
				now = was+1;
			}
			success = ts.compareAndSet(was,now);
			
		// keep retrying with updated time until we manage to catch the atomic ts in a steady state 
		// where compareAndSet succeeds 
		} while (!success);
		
		// used to visualize collision correction behavior in the multi-threaded test (single-threaded code is not susceptible)
		if (verbose) {
			if (tries > 1) { System.out.println("tries:"+tries+"  was:"+was+"  now:"+now); }
		}
		
		// either the natural value or the artificially incremented value that was successfully set
		return now;
	}
}
