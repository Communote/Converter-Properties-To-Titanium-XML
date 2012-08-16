package com.communote.tools.properties;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * This is a simple converter, which reads java property files and writes them
 * back as Titanium XML files.
 * 
 * Execute this script in the Titanium's project root directory!
 * 
 * @author Communote GmbH - <a
 *         href="http://www.communote.de/">http://www.communote.com/</a>
 */
public class TitaniumConverter {

	private static Logger LOGGER = Logger.getLogger(TitaniumConverter.class
			.getName());

	/**
	 * Starter
	 */
	public static void main(String[] args) {
		try {
			File folder = new File(System.getProperty("user.dir"), "i18n");
			File[] langFolders = folder.listFiles(new FileFilter() {

				@Override
				public boolean accept(File arg0) {
					return arg0.isDirectory() && !arg0.getName().equals(".")
							&& !arg0.getName().equals("..");
				}
			});
			for (File lang : langFolders) {
				LOGGER.info("Translating " + lang.getName());
				transform(
						new File(lang, "strings.properties").getAbsolutePath(),
						new File(lang, "strings.xml").getAbsolutePath());
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		LOGGER.info("Done. Thanks You!");
	}

	/**
	 * Does the transformation.
	 * 
	 * @param input
	 *            Path to the input file.
	 * @param output
	 *            Path to the output file.
	 * @throws IOException
	 *             Exception.
	 * @throws FileNotFoundException
	 *             Exception.
	 */
	private static void transform(String input, String output)
			throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.load(new BufferedReader(new InputStreamReader(
				new FileInputStream(input), "UTF-8")));
		LOGGER.info("Read " + properties.size() + " properties from " + input);
		Map<String, String> orderedProperties = new TreeMap<String, String>();
		for (Entry<Object, Object> property : properties.entrySet()) {
			orderedProperties.put(property.getKey().toString(), property
					.getValue() == null ? "" : property.getValue().toString());
		}
		LOGGER.info("Writing Titanium XML to" + output);
		Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(output), "UTF-8"));
		try {
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			writer.write("<resources>\n");
			for (Entry<String, String> property : orderedProperties.entrySet()) {
				writer.write("    <string name=\"" + property.getKey() + "\"");
				if (property.getValue().contains("%")) {
					writer.write(" formatted=\"false\"");
				}
				writer.write(">" + property.getValue() + "</string>\n");
			}
			writer.write("</resources>");
		} finally {
			writer.close();
		}
	}
}
