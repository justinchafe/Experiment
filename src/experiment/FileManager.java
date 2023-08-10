/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package experiment;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author jchafe
 */
public class FileManager {
   
    Document doc;
    private String xmlFilename;
    private static String csvFilename;
    
     public FileManager(String xmlFilename, String csvFilename, String rootElement) {
        this.xmlFilename = xmlFilename;
        this.csvFilename = csvFilename;
        
        try {
            /////////////////////////////
            //Creating an empty XML Document
            //We need a Document
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            doc = docBuilder.newDocument();
            /////////////////////////
            //Creating the XML tree
            //create the root element and add it to the document
            Element root = doc.createElement(rootElement);
            doc.appendChild(root);
            
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
       	}
          createFile();
          writeXmlFile();
        
    }
    
    
      public void addMultipleResponses(String[] resps, String parentNode, String title) {
         int i;
         
        try {
            for (i=0;i<resps.length;i++) {
                if (resps[i] != null) {
                    Element child = doc.createElement(title + i);
                    child.appendChild(doc.createTextNode(resps[i]));
                    //child.setAttribute("name", "value");
                    //Node node = doc.getDocumentElement();
                    NodeList list = doc.getElementsByTagName(parentNode);
                    if (list.getLength() == 1) {
                        Element ele = (Element) list.item(0);
                        ele.appendChild(child);
                    }else {
                        System.out.println("Parent Node: " + parentNode + " Not Found!!");
                    }
                }
            }
        }catch (Exception e) {
            System.out.println(e);
        }
        //writeXmlFile();
    }
     
      
    //Integer Value  
    public void addSingleResponse(int value, String title ) {
        try {
            Element child = doc.createElement(title);
            child.appendChild(doc.createTextNode(Integer.toString(value)));
            //child.setAttribute("name", "value");
            Element root = doc.getDocumentElement();
            root.appendChild(child);
        }catch (Exception e) {
            System.out.println(e);
        }
    }
     
    //String Value
     public void addSingleResponse(String value, String title) {
        try {
            Element child = doc.createElement(title);
            child.appendChild(doc.createTextNode(value));
            //child.setAttribute("name", "value");
            Element root = doc.getDocumentElement();
            root.appendChild(child);
        }catch (Exception e) {
            System.out.println(e);
        }
    } 
     
     
     public void addSingleResponse(String parentNode, int value, String title) {
        try {
            Element child = doc.createElement(title);
            child.appendChild(doc.createTextNode(Integer.toString(value)));
            //child.setAttribute("name", "value");
             NodeList list = doc.getElementsByTagName(parentNode);
                    if (list.getLength() == 1) {
                        Element ele = (Element) list.item(0);
                        ele.appendChild(child);
                    }else {
                        System.out.println("Parent Node: " + parentNode + " Not Found!!");
                    }
           // Element root = doc.getDocumentElement();
           // root.appendChild(child);
        }catch (Exception e) {
            System.out.println(e);
        }
    } 
     
      public void addSingleResponse(String parentNode, String value, String title) {
        try {
            Element child = doc.createElement(title);
            child.appendChild(doc.createTextNode(value));
            //child.setAttribute("name", "value");
             NodeList list = doc.getElementsByTagName(parentNode);
                    if (list.getLength() == 1) {
                        Element ele = (Element) list.item(0);
                        ele.appendChild(child);
                    }else {
                        System.out.println("Parent Node: " + parentNode + " Not Found!!");
                    }
           // Element root = doc.getDocumentElement();
           // root.appendChild(child);
        }catch (Exception e) {
            System.out.println(e);
        }
    } 
 
    public void addElement(String title) {
        try {
            Element child = doc.createElement(title);
            //child.setAttribute("name", "value");
            Element root = doc.getDocumentElement();
            root.appendChild(child);
        }catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void addSubElement(String parentNode, String title) {
          try {
            Element child = doc.createElement(title);
             //child.setAttribute("name", "value");
             NodeList list = doc.getElementsByTagName(parentNode);
                    if (list.getLength() == 1) {
                        Element ele = (Element) list.item(0);
                        ele.appendChild(child);
                    }else {
                        System.out.println("Parent Node: " + parentNode + " Not Found!!");
                    }
           // Element root = doc.getDocumentElement();
           // root.appendChild(child);
        }catch (Exception e) {
            System.out.println(e);
        }
    }
    
     /*
    public void addParticipantNum(int pNum) {
        try {
            Element child = doc.createElement("subjectNum");
            child.appendChild(doc.createTextNode(Integer.toString(pNum)));
            //child.setAttribute("name", "value");
            Element root = doc.getDocumentElement();
            root.appendChild(child);
        }catch (Exception e) {
            System.out.println(e);
        }
    }
    * 
    * previously we had addRWords as well (like above)
    */

    public void writeXmlFile() {
        try {
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no"); //from class OutputKeys!
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            //create string from xml tree
            PrintWriter out  = new PrintWriter(new BufferedWriter(new FileWriter(xmlFilename)));
	    StreamResult result = new StreamResult(out); //holder for transform
	    DOMSource source = new DOMSource(doc); //create our DOMSource using our Document (Passed as NODE)
            trans.transform(source, result); //pass our DOMSource and our holder for transform (Result interface implemented by StreamResult which is constructed from our StringWriter(Buffer))
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void printXml() {
	try {

		TransformerFactory transfac = TransformerFactory.newInstance();
        	Transformer trans = transfac.newTransformer();
        	trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        	trans.setOutputProperty(OutputKeys.INDENT, "yes");

            	//create string from xml tree
        	StringWriter sw = new StringWriter();
	 	StreamResult result = new StreamResult(sw); //holder for transform
            	DOMSource source = new DOMSource(doc); //create our DOMSource using our Document (Passed as NODE)
            	trans.transform(source, result); //pass our DOMSource and our holder for transform (Result interface implemented by StreamResult which is constructed from our StringWriter(Buffer))
          	String xmlString = sw.toString();

            	//print xml
            	System.out.println("Here's the xml:\n\n" + xmlString);
 	} catch (Exception e) {
           	 System.out.println(e);
        }
  
    }
    
    
    private boolean createFile() {
        File f = new File(csvFilename);
	JFrame frame = new JFrame("dialog");

        try {
            if (f.exists()) {
                int n = JOptionPane.showConfirmDialog(frame,"Warning! file already exists, would you like to overwrite previous data?",
    		"Overwrite",JOptionPane.YES_NO_OPTION);
        	//int k = JOptionPane.showConfirmDialog(frame,"Directory: " + System.getProperty("user.dir"),
               // "Overwrite", JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
                    f.delete();
                    f.createNewFile();
		}else if (n == JOptionPane.NO_OPTION) {
                   System.exit(0);
		}else if (n == JOptionPane.CLOSED_OPTION) {
                   System.exit(0);
		}
		}else {
                    if (f.createNewFile()) {
                        ;
		}else
                    ; //exists, handle append or overwrite
		}

	} catch (IOException e) {
            JOptionPane.showMessageDialog(frame,
            "File IO Error",
            "File Error",
            JOptionPane.WARNING_MESSAGE);
        }	
	//writeHeadersCSV();
	return true;
}
    

     public void writeCSVInfo(String info) {
        BufferedWriter outputStream = null;
      	
	try {
            outputStream = new BufferedWriter(new FileWriter(csvFilename, false));
            System.out.println(info);
            outputStream.write(info);
            //outputStream.flush();
            outputStream.close();
        }catch (IOException e) {
                System.out.println("File IO Error");
                e.printStackTrace();
            }
         
     }
    
    public static void writeHeaders(Participant p) {
        int i, j;	
        BufferedWriter stream = null;
        try {
            stream = new BufferedWriter(new FileWriter("CSVKey", false)); 
            stream.write("PNUM");
            stream.write(",");
            stream.write("firstList");
            stream.write(",");
            for (i=0; i<6; i++) {
                stream.write("vas1_" + (i+1));
                stream.write(", ");                
            }
            for (i=0; i<Experiment.NUM_WORDS; i++) {
                stream.write("DFWord" + (i+1));
                stream.write(", ");
                stream.write("DFRating" + (i+1));
                stream.write(", ");
            }
            
            stream.write("countDownAns");
            stream.write(", ");
            
            stream.write("exactMatch");
            stream.write(", ");
            stream.write("metaMatch");
            stream.write(", ");
           
            j = 1;
            for (i=0; i<p.getRecalledWords().length; i++) {
                if (p.getRecalledWords()[i] != null) {
                    stream.write("recalledWord" + j);
                    stream.write(", ");
                    j++;
                }
            }
            
             for (i=0; i<6; i++) {
                stream.write("vas2_" + (i+1));
                stream.write(", ");                
            }
            
            stream.write("movieOneYN1");
            stream.write(", ");
            
            stream.write("movieOneYN2");
            stream.write(", ");
            
            stream.write("movieOneScale1");
            stream.write(", ");
            
            stream.write("movieOneScale2");
            stream.write(", ");
            
            stream.write("movieTwoScale3");
            stream.write(", ");
            
            stream.write("TSList1");
            stream.write(", ");
            
            stream.write("TSNumSpacePressed");
            stream.write(", ");
            
            for (i=0; i<6; i++) {
                stream.write("vas3_" + (i+1));
                stream.write(", ");                
            }
            
            stream.write("tsYN1");
            stream.write(", ");
            stream.write("tsYN2");
            stream.write(", ");
            stream.write("tsYN3");
            stream.write(", ");
            
            stream.write("tsScale1");
            stream.write(", ");
            stream.write("tsScale2");
            stream.write(", ");
           // stream.write("tsScale3");
           // stream.write(", ");
            
            for (i=0; i<6; i++) {
                stream.write("vas4_" + (i+1));
                stream.write(", ");                
            }
            
                  
            stream.write("movieTwoYN1");
            stream.write(", ");
            
            stream.write("movieTwoYN2");
            stream.write(", ");
            
            stream.write("movieTwoScale1");
            stream.write(", ");
            
            stream.write("movieTwoScale2");
            stream.write(", ");      
                    
            stream.write("movieTwoScale3");
            stream.write(", ");
            
            
            stream.write("openEndedQuestion");
            stream.write(", ");
            
            stream.flush();
            stream.close();
          			
            }catch (IOException e) {
                System.out.println("File IO Error");
                e.printStackTrace();
		
            }

            
     }   
    
    
      public static void writeHeaders() {
        int i;	
        BufferedWriter stream = null;
        try {
            stream = new BufferedWriter(new FileWriter("CSVKey.csv", false)); 
            stream.write("PNUM");
            stream.write(",");
            stream.write("firstList");
            stream.write(",");
            for (i=0; i<6; i++) {
                stream.write("vas1_" + (i+1));
                stream.write(", ");                
            }
            for (i=0; i<Experiment.NUM_WORDS; i++) {
                stream.write("DFWord" + (i+1));
                stream.write(", ");
                stream.write("DFRating" + (i+1));
                stream.write(", ");
            }
            
            stream.write("countDownAns");
            stream.write(", ");
            
            stream.write("exactMatch");
            stream.write(", ");
            stream.write("metaMatch");
            stream.write(", ");
           
           
            for (i=0; i<Experiment.NUM_WORDS; i++) {
                    stream.write("recalledWord" + (i+1));
                    stream.write(", ");
                    
                }
           
            
             for (i=0; i<6; i++) {
                stream.write("vas2_" + (i+1));
                stream.write(", ");                
            }
            
            stream.write("movieOneYN1");
            stream.write(", ");
            
            stream.write("movieOneYN2");
            stream.write(", ");
            
            stream.write("movieOneScale1");
            stream.write(", ");
            
            stream.write("movieOneScale2");
            stream.write(", ");
            
            stream.write("movieTwoScale3");
            stream.write(", ");
            
            stream.write("TSList1");
            stream.write(", ");
            
            stream.write("TSNumSpacePressed1");
            stream.write(", ");
            
            stream.write("TSList2");
            stream.write(", ");
            
            stream.write("TSNumSpacePressed2");
            stream.write(", ");
            
            for (i=0; i<6; i++) {
                stream.write("vas3_" + (i+1));
                stream.write(", ");                
            }
            
            stream.write("tsYN1");
            stream.write(", ");
            stream.write("tsYN2");
            stream.write(", ");
            stream.write("tsYN3");
            stream.write(", ");
            
            stream.write("tsScale1");
            stream.write(", ");
            stream.write("tsScale2");
            stream.write(", ");
           // stream.write("tsScale3");
           // stream.write(", ");
            
            for (i=0; i<6; i++) {
                stream.write("vas4_" + (i+1));
                stream.write(", ");                
            }
            
                  
            stream.write("movieTwoYN1");
            stream.write(", ");
            
            stream.write("movieTwoYN2");
            stream.write(", ");
            
            stream.write("movieTwoScale1");
            stream.write(", ");
            
            stream.write("movieTwoScale2");
            stream.write(", ");      
                    
            stream.write("movieTwoScale3");
            stream.write(", ");
            
            
            stream.write("openEndedQuestion");
            stream.write(", ");
            
            stream.flush();
            stream.close();
          			
            }catch (IOException e) {
                System.out.println("File IO Error");
                e.printStackTrace();
		
            }
      }  
     
       
}
