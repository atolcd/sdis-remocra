package fr.sdis83.remocra.xml;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.security.DbUnitBaseTest;
import fr.sdis83.remocra.util.XmlValidationEventHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/spring/testContextInMemory.xml" })
public class XmlTest {

    @Test
    public void testXML() throws BusinessException, XMLStreamException, FactoryConfigurationError, SAXException {

        // Test avec les communes
        ArrayList<fr.sdis83.remocra.xml.Commune> lstXmlCommune = new ArrayList<fr.sdis83.remocra.xml.Commune>();

        lstXmlCommune.add(new fr.sdis83.remocra.xml.Commune("21000", "DIJON", "21000"));
        lstXmlCommune.add(new fr.sdis83.remocra.xml.Commune("21220", "GEVREY CHAMBERTIN", "21220"));
        lstXmlCommune.add(new fr.sdis83.remocra.xml.Commune("21240", "TALANT", "21240"));

        JAXBContext jaxbContext;

        LstCommunes lstComm = new LstCommunes();
        try {
            // Sérialisation
            lstComm.setCommunes(lstXmlCommune);

            jaxbContext = JAXBContext.newInstance(fr.sdis83.remocra.xml.LstCommunes.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            StringWriter sw = new StringWriter();
            XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
            jaxbMarshaller.marshal(lstComm, xmlStreamWriter);

            // Désérialisation
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // ClassLoader classLoader =
            // Thread.currentThread().getContextClassLoader();
            // URL urlFile = classLoader.getResource("xml/communes.xml");
            // /LstCommunes communes = (LstCommunes)
            // unmarshaller.unmarshal(urlFile);

            LstCommunes communes = (LstCommunes) unmarshaller.unmarshal(new StreamSource(new StringReader(sw.getBuffer().toString())));

            assertTrue(communes.getCommunes().size() == lstXmlCommune.size());

        } catch (JAXBException e) {
            fail("error " + e);
        }
        // Test avec les Modeles
        ArrayList<fr.sdis83.remocra.xml.Modele> lstXmlModele = new ArrayList<fr.sdis83.remocra.xml.Modele>();

        lstXmlModele.add(new fr.sdis83.remocra.xml.Modele("X1", "X1"));
        lstXmlModele.add(new fr.sdis83.remocra.xml.Modele("X5", "X1"));
        lstXmlModele.add(new fr.sdis83.remocra.xml.Modele("X6", "X6"));

        LstModeles lstModeles = new LstModeles();
        try {
            // Sérialisation
            lstModeles.setModeles(lstXmlModele);

            jaxbContext = JAXBContext.newInstance(fr.sdis83.remocra.xml.LstModeles.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            StringWriter sw = new StringWriter();
            XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
            jaxbMarshaller.marshal(lstModeles, xmlStreamWriter);

            // Désérialisation
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // ClassLoader classLoader =
            // Thread.currentThread().getContextClassLoader();
            // URL urlFile = classLoader.getResource("xml/communes.xml");
            // /LstCommunes communes = (LstCommunes)
            // unmarshaller.unmarshal(urlFile);

            LstModeles modeles = (LstModeles) unmarshaller.unmarshal(new StreamSource(new StringReader(sw.getBuffer().toString())));

            assertTrue(modeles.getModeles().size() == lstXmlModele.size());

        } catch (JAXBException e) {
            fail("error " + e);
        }

        // Désérialisation
        // Tests tournée
        try {
            jaxbContext = JAXBContext.newInstance(fr.sdis83.remocra.xml.LstTournees.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL urlFile = classLoader.getResource("xml/tournees.xml");
            LstTournees tournees = (LstTournees) unmarshaller.unmarshal(urlFile);

            assertTrue(tournees.getTournees().size() == 2);
        } catch (JAXBException e) {
            fail("error " + e);
        }
        // Tests Hydrants
        try {

            jaxbContext = JAXBContext.newInstance(fr.sdis83.remocra.xml.LstHydrants.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL urlFile = classLoader.getResource("xml/hydrants.xml");
            URL urlFile2 = classLoader.getResource("xml/Hydrants.xsd");

            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new File(urlFile2.getFile()));

            unmarshaller.setSchema(schema);
            unmarshaller.setEventHandler(new XmlValidationEventHandler());
            LstHydrants hydrants = (LstHydrants) unmarshaller.unmarshal(urlFile);

            assertTrue((hydrants.getHydrantsPena().size()+hydrants.getHydrantsPibi().size()) == 13);
        } catch (JAXBException e) {
            fail("error " + e);
        }
    }
}
