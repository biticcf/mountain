/**
 * 
 */
package com.beyonds.phoenix.mountain.generator;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年1月14日
 * @Time:   上午10:25:56
 *
 */
public class XMLValidation {
	/**
	 * 用xsd验证xml文件
	 * @param xmlPath
	 * @throws Exception
	 */
	public static void validateXMLSchema(String xmlPath) throws Exception {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Resource resource = new ClassPathResource("/META-INF/generator.xsd");
            Schema schema = factory.newSchema(resource.getURL());
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlPath)));
            
            System.out.println("xsd文件验证成功.");
        } catch (IOException | SAXException e) {
            throw new Exception(e.getMessage());
        }
    }
}
