package de.ronnyfriedland.shoppinglist.xml;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Ronny Friedland
 */
public class ReadXMLFile {

    private static final Logger LOG = Logger.getLogger(ReadXMLFile.class.getName());

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
                @Override
                public void characters(char ch[], int start, int length) throws SAXException {
                    result.add(new String(ch, start, length));
                }

            };
            saxParser.parse(Thread.currentThread().getContextClassLoader().getResourceAsStream(file), handler);
        } catch (ParserConfigurationException e) {
            LOG.log(Level.SEVERE, "Error configuring parser", e);
        } catch (SAXException e) {
            LOG.log(Level.SEVERE, "Error creating parser", e);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error parsing file " + file, e);
        }
        return result;
    }
}
