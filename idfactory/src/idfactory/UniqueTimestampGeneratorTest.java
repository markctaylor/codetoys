package idfactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UniqueTimestampGeneratorTest {

	static Map<Long, Integer> countMap;
	
	@BeforeAll
	static void setupOnce() {
	    countMap = new HashMap<>(100000);
	}
	
	@AfterAll
	static void summarizeAndAssertOverallSuccessCriteria() {
		System.out.println("Looking for collisions");
		for ( Integer value : countMap.values() ) {
			assertEquals(1, value.intValue(), "no key should have appeared more than once");
		}
		System.out.println("No collisions found");
	}

	@Test
	void testLinearHighSpeedAllocation() {
		long last, ts = 0;
		
		for (int i=0; i<10000; i++) {
			last = ts;
			ts = UniqueTimestampGenerator.get();
			countMap.merge(ts, 1, Integer::sum);

			assertTrue(ts>last, "new ts should always be greater than previous ts");
		}
		System.out.println("Final ts served by single threaded test: "+ts);
	}

	@Test
	void testThreadedHighSpeedAllocation() throws InterruptedException, ExecutionException {
		System.out.println("Multi-threaded test started at: "+System.currentTimeMillis());

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
	}
}
