package idfactory;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SequencedIdFactoryTest {

	@Test
	void testIdPatternGeneration() {
		checkPattern( new SequencedIdFactory("prefix-", "-suffix"), "prefix-[0-9]+-suffix");
		checkPattern( new SequencedIdFactory(null, "-suffix"), "[0-9]+-suffix");
		checkPattern( new SequencedIdFactory("prefix-", null), "prefix-[0-9]+");
	}
	
	private void checkPattern(SequencedIdFactory sif, String matchingRegex) {
		for (int i=0; i<5; i++) {
			String id = sif.newId();
			assertTrue(id.matches(matchingRegex));
		}
	}

}
