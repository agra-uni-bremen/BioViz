package de.dfki.revlibReader;

import static org.junit.Assert.*;

import org.junit.Test;

public class RevlibFileReaderTest {

	@Test
	public void test_urf4_187() {
		ReversibleCircuit rc = RevlibFileReader.readRealFile("examples/urf4_187.real");
		ToffoliGate tg = rc.getGate(0);
		assertEquals(tg.getInputs().get(0), "k");
		assertEquals("First variable name of urf4_187 was supposed to be 'a', was instead " + rc.getVars().get(0), "a", rc.getVars().get(0));
		assertEquals("k", rc.getVars().get(rc.getVars().size() - 1));
		assertEquals("Expected amount of garbage lines of urf4_187 was 0, value retrieved was " + rc.getAmountOfGarbageLines(), 0, rc.getAmountOfGarbageLines());
		assertEquals("Expected amount of variables of urf4_187 was 11, value retrieved was " + rc.getAmountOfVars(), 11, rc.getAmountOfVars());
	}
}
