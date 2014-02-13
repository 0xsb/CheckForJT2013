package com.ailk.check.generate;

import com.ailk.check.generate.config.ConfigurationFactory;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-20
 * Time: 下午5:30
 */
public class XmlGeneratorTest {
    private XmlGenerator generator;

    @Before
    public void setUp() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.generator = new XmlGenerator(ConfigurationFactory.getInstance().getConfiguration("smmaf"), 2013, 4, 1, 0);
    }

    @Test
    public void generateXml() throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException,
            SQLException, JAXBException, IllegalAccessException, DatatypeConfigurationException, ClassNotFoundException {
        File smmafXml = generator.generateXml();
        assertTrue(smmafXml.exists());
    }
}
