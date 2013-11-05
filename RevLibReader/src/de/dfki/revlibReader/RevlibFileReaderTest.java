package de.dfki.revlibReader;

import static org.junit.Assert.*;

import org.junit.Test;

public class RevlibFileReaderTest {

	@Test
	public void test_urf4_187() {
		ReversibleCircuit rc = RevlibFileReader.readRealFile("examples/urf4_187.real");
		ToffoliGate tg = rc.getGate(0);
		assertEquals(tg.inputA, "k");
	}

}
