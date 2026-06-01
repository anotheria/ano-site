package test;

import net.anotheria.asg.generator.Generator;
import net.anotheria.asg.generator.util.FileWriter;

public class GenerateANOSITE {
	public static void main(String[] a ) throws Exception{
		FileWriter.setBaseDir("generated");
		Generator.generate();
	}
}
