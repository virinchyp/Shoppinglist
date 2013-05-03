package de.ronnyfriedland.shoppinglist.xml;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

/**
 * @author Ronny Friedland
 */
public class ReadXMLFile {

	/**
	 * Parse file and add entries to the result list.
	 * 
	 * @param file
	 *            the source file
	 * @return the content
	 */
	public static Collection<String> parseFile(final String file) {
		final Set<String> result = new HashSet<String>();
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			DefaultHandler handler = new DefaultHandler() {
				/**
				 * {@inheritDoc}
				 * 
				 * @see org.xml.sax.helpers.DefaultHandler#characters(char[],
				 *      int, int)
				 */
				public void characters(char ch[], int start, int length) throws SAXException {
					result.add(new String(ch, start, length));
				}

			};
			saxParser.parse(Thread.currentThread().getContextClassLoader().getResourceAsStream(file), handler);
		} catch (ParserConfigurationException e) {
			Log.e(ReadXMLFile.class.getCanonicalName(), "Error configuring parser", e);
		} catch (SAXException e) {
			Log.e(ReadXMLFile.class.getCanonicalName(), "Error creating parser", e);
		} catch (IOException e) {
			Log.e(ReadXMLFile.class.getCanonicalName(), "Error parsing file " + file, e);
		}
		return result;
	}
}
