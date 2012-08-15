package com.communote.tools.properties;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * This is a simple converter, which reads java property files and writes them back as Titanium XML
 * files.
 * 
 * @author Communote GmbH - <a href="http://www.communote.de/">http://www.communote.com/</a>
 */
public class TitaniumConverter {

    private static Logger LOGGER = Logger.getLogger(TitaniumConverter.class.getName());

    /**
     * Starter
     * 
     * @param args
     *            First argument must be the input file, second the output.
     */
    public static void main(String[] args) {
        if (args == null || args.length != 2) {
            System.out
                    .print("First argument must be the path to input file, second the path to the output file.");
            System.exit(1);
        }
        try {
            transform(args[0], args[1]);
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
    private static void transform(String input, String output) throws FileNotFoundException,
            IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(input));
        LOGGER.info("Read " + properties.size() + " properties from " + input);
        Map<String, String> orderedProperties = new TreeMap<String, String>();
        for (Entry<Object, Object> property : properties.entrySet()) {
            orderedProperties.put(property.getKey().toString(), property.getValue() == null ? ""
                    : property.getValue().toString());
        }
        LOGGER.info("Writing Titanium XML to" + output);
        Writer writer = new BufferedWriter(new FileWriter(output));
        try {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<resources>\n");
            for (Entry<String, String> property : orderedProperties.entrySet()) {
                writer.write("    <string name=\"" + property.getKey() + "\"");
                if (property.getValue().contains("%")) {
                    writer.write(" format=\"false\"");
                }
                writer.write(">" + property.getValue() + "</string>\n");
            }
            writer.write("</resources>");
        } finally {
            writer.close();
        }
    }
}
