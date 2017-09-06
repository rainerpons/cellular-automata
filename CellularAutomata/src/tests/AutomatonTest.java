
package tests;

import automata.Automaton;
import automata.Vector;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/*
 * AutomatonTest.java
 */

public class AutomatonTest {
	Map<Integer, Vector> map;

	@Before public void setUp() {
		map = new HashMap<Integer, Vector>();
		map.put(new Integer(0), new Vector("10101010"));
		map.put(new Integer(1), new Vector("10101011"));
		map.put(new Integer(2), new Vector("10101010"));
		map.put(new Integer(3), new Vector("10101011"));
		map.put(new Integer(4), new Vector("10101010"));
		map.put(new Integer(5), new Vector("10101011"));
		map.put(new Integer(6), new Vector("10101010"));
		map.put(new Integer(7), new Vector("10101011"));
	}

	// assert that map values are generated correctly
	@Test public void testInitializeVectorMap() {
		Map<Integer, Vector> expectedMap = map;
		Map<Integer, Vector> actualMap = Automaton.initializeVectorMap(30, new Vector("10101010"));
		for (Map.Entry<Integer, Vector> entry : expectedMap.entrySet()) {
			String expected = entry.getValue().getVector();
			String actual = actualMap.get(entry.getKey()).getVector();
			Assert.assertEquals(expected, actual);
		}
	}
}
