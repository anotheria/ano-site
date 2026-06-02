package net.anotheria.anosite.content.servlet;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BrowsingHistoryTest {
	@Test public void testAddSimilar(){
		BrowsingHistory history = new BrowsingHistory();
		history.addHistoryItem("1");
		history.addHistoryItem("1");
		assertEquals("1", history.toString());
		
	}
	
	@Test public void testToString(){
		BrowsingHistory history = new BrowsingHistory();
		history.addHistoryItem("1");
		history.addHistoryItem("2");
		history.addHistoryItem("3");
		assertEquals("1->2->3", history.toString());
	}
	
	@Test public void testOverflow(){
		BrowsingHistory h1 = new BrowsingHistory();
		
		for (int i=0; i<200; i++){
			h1.addHistoryItem(""+i);
			assertTrue(h1.getHistoryItems().size()<=10);
		}
	}
}
