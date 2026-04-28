package idfactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

class UniqueTimestampGeneratorTest {

	// This accumulates all IDs generated across all tests so id reuse (collisions) can be detected at the end
	static Map<Long, Integer> countMap = new ConcurrentHashMap<>(100000);
	static AtomicInteger invocations = new AtomicInteger(0);
	
	// this AfterAll method is actually a test itself, looking through the accumulated IDs
	// for any that were seen more than once.
	@AfterAll
	static void summarizeAndAssertOverallSuccessCriteria() {
		System.out.println("Looking for collisions");
		for ( Integer value : countMap.values() ) {
			assertEquals(1, value.intValue(), "no key should have been seen more than once");
		}
		assertEquals(invocations.get(), countMap.size(), "Map size should equal invocation counter");
		System.out.println("No collisions found");
	}

	@Test
	void testOne() {
		long ts = getTsAndAccumulate();
		assertTrue(ts > 0, "A value was generated");
	}
	
	@Test
	void testLinearHighSpeedAllocation() {
		long last, ts = 0;	
		for (int i=0; i<10000; i++) {
			last = ts;
			ts = getTsAndAccumulate();
			assertTrue(ts>last, "new ts should always be greater than previous ts");
		}
		System.out.println("Final ts served by single threaded test: "+ts);
	}

	@Test
	void testThreadedHighSpeedAllocation() throws InterruptedException, ExecutionException {
		System.out.println("Multi-threaded test started at: "+System.currentTimeMillis());

		// execute 5 copies of the linear test concurrently to encourage any thread race conditions / collisions.
		// note that all assertions for this test occur either in the runnable or in the AfterAll sweep
		ExecutorService executor = Executors.newFixedThreadPool(5);
        Future<?> future1 = executor.submit(()-> {testLinearHighSpeedAllocation();} );
        Future<?> future2 = executor.submit(()-> {testLinearHighSpeedAllocation();} );
        Future<?> future3 = executor.submit(()-> {testLinearHighSpeedAllocation();} );
        Future<?> future4 = executor.submit(()-> {testLinearHighSpeedAllocation();} );
        Future<?> future5 = executor.submit(()-> {testLinearHighSpeedAllocation();} );
        future1.get();
        future2.get();
        future3.get();
        future4.get();
        future5.get();

        System.out.println("Multi-threaded test completed at: "+System.currentTimeMillis());
        // assertions for this test will be checked in the AfterAll method
	}
	
	// This should be the ONLY place in the test class where the direct call to the utility is made
	// so that each call can be recorded for later analysis
	private long getTsAndAccumulate() {
		
		// generate next unique value
		// this one line is the entire calling convention for the unique timestamp/long/ID generator
		long ts = UniqueTimestampGenerator.get();
		
		// accumulate this invocation
		invocations.addAndGet(1);

		// accumulate the generated value in a Map for later collision detection
		countMap.merge(ts, 1, Integer::sum);

		return ts;
	}
}
