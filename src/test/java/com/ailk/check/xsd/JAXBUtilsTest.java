package com.ailk.check.xsd;

import com.ailk.check.xsd.smjkr.ObjectFactory;
import com.ailk.check.xsd.smjkr.Smp;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-5-22
 * Time: 上午10:43
 */
public class JAXBUtilsTest {

    @Test
    public void unMarshalAndMarshalTest() throws JAXBException, IOException {
        File smjkrXml = new File(getClass().getResource("/testXml/SMJKR_23.xml").getFile());
        Smp smjkr01 = JAXBUtils.unMarshal(Smp.class, FileUtils.readFileToString(smjkrXml));
        Assert.assertEquals(9, smjkr01.getData().getRcd().size());

        ObjectFactory objectFactory = new ObjectFactory();
        Smp.Data resetData = objectFactory.createSmpData();
        int i = 1;
        for (Smp.Data.Rcd rcd : smjkr01.getData().getRcd()) {
            if (i == 1 || i == 6) {
                resetData.getRcd().add(rcd);
            }
            i++;
        }
        smjkr01.setData(resetData);

        File resetXml = new File(getClass().getResource("/").getFile() + "/generateXml/SMJKR_01_reset.xml");
        FileUtils.touch(resetXml);
        String xml = JAXBUtils.marshal(Smp.class, smjkr01);
        FileUtils.writeStringToFile(resetXml, xml);
        Assert.assertTrue(resetXml.exists());
    }
}
