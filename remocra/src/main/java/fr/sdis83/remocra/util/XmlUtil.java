package fr.sdis83.remocra.util;

import fr.sdis83.remocra.exception.BusinessException;
import java.io.File;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public class XmlUtil {

  private static final Logger logger = Logger.getLogger(XmlUtil.class.getClass());

  public static <T> void serializeXml(
      Class<?> classe, Object lst, String referentiel, OutputStream out) throws BusinessException {
    try {
      JAXBContext jaxbContext;
      jaxbContext = JAXBContext.newInstance(classe);
      Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
      jaxbMarshaller.marshal(lst, out);
    } catch (JAXBException e) {
      logger.error("Problème avec la sérialisation des " + referentiel + " : " + e.getMessage(), e);
      throw new BusinessException(
          "Problème avec la sérialisation des " + referentiel + " : " + e.getMessage());
    }
  }

  public static Object unSerializeXml(String xml, Class<?> classe, String xsdResource)
      throws JAXBException, SAXException {
    JAXBContext jaxbContext;

    jaxbContext = JAXBContext.newInstance(classe);

    URL urlFile = XmlUtil.class.getClassLoader().getResource(xsdResource);

    SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = sf.newSchema(new File(urlFile.getFile()));

    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    unmarshaller.setSchema(schema);
    unmarshaller.setEventHandler(new XmlValidationEventHandler());

    return unmarshaller.unmarshal(new StringReader(xml));
  }
}
