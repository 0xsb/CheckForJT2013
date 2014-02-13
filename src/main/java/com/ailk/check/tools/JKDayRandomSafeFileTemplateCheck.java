package com.ailk.check.tools;

import com.ailk.check.xsd.JAXBUtils;
import com.ailk.check.xsd.smjkr.Smp;
import org.apache.commons.io.FileUtils;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-24
 * Time: 上午11:13
 */
public class JKDayRandomSafeFileTemplateCheck {

    public static void main(String[] args) throws IOException, JAXBException {
        for (int i = 1; i <= 31; i++) {
            String day = String.valueOf(i);
            String path = "D:\\java\\work\\ailk\\projects\\AI4A2\\src\\CheckForJT\\doc\\2013safe\\day\\SMJKR\\SMJKR_" + day + ".xml";
            System.out.println("day : " + i);
            printSameRequestIdFromSMJKR(path);
        }
    }

    public static void printSameRequestIdFromSMJKR(String safeFileTemplatePath) throws IOException, JAXBException {
        // 获取安全模板文件
        File jkrTemplateFile = new File(safeFileTemplatePath);
        // 从安全模板XML获取对象
        com.ailk.check.xsd.smjkr.Smp jkrTemplateObject = JAXBUtils.unMarshal(com.ailk.check.xsd.smjkr.Smp.class, FileUtils.readFileToString(jkrTemplateFile, "UTF-8"));

        Set<String> requestSet = new HashSet<String>();
        Set<String> sameSet = new HashSet<String>();
        for (Smp.Data.Rcd rcd : jkrTemplateObject.getData().getRcd()) {
            if (requestSet.contains(rcd.getRequestid())) {
                sameSet.add(rcd.getRequestid());
                continue;
            }
            requestSet.add(rcd.getRequestid());
        }
        System.out.println("same count : " + sameSet.size());
        for (String s : sameSet) {
            System.out.println(s);
        }
    }
}
