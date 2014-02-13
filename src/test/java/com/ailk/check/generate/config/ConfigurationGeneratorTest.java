package com.ailk.check.generate.config;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-21
 * Time: 下午3:20
 */
public class ConfigurationGeneratorTest {

    private ConfigurationGenerator generator;

    @Before
    public void setUp() throws ParserConfigurationException, IOException, SAXException {
        this.generator = new ConfigurationGenerator(getClass().getResource("/xmlMappingDb/smmafXmlMapper.xml").getFile());
    }

    @Test
    public void generateConfiguration ()
            throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        Configuration configuration = generator.generateConfiguration();
        assertEquals("SMMAF", configuration.getName());
        assertEquals("column:main_acct_id", configuration.getDb().getMapper().getElementMap().get("rolelist").getMethod().getParams().get(0).getValue());
    }
}
