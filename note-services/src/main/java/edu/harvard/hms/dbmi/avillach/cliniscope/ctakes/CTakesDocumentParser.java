package edu.harvard.hms.dbmi.avillach.cliniscope.ctakes;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.CTakesGroup;
import edu.harvard.hms.dbmi.avillach.cliniscope.entities.CTakesHit;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CTakesDocumentParser {

	Logger logger = LogManager.getLogger(getClass());

	private Document convertToHtml(InputStream input) throws IOException, SAXException, ParserConfigurationException{
		BufferedReader rawText = new BufferedReader(new InputStreamReader(input));
		StringBuilder outputText = new StringBuilder();
		String line = rawText.readLine();
		int length = 0;
		int lineCount = 0;
		outputText.append("<div>");
		while(line != null){
			outputText.append("<p "
					+ "id='line_" + (lineCount++) + "' " 
					+ "data-start-offset='" + (length) + "' "
					+ "data-end-offset='"+(length+line.length()+1)+"'>");
			outputText.append(StringEscapeUtils.escapeHtml4(line));
			outputText.append("</p>\n");
			length+=line.length()+1;
			line = rawText.readLine();
		}
		outputText.append("</div>");
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
				new ByteArrayInputStream(outputText.toString().getBytes("UTF-8")));
	}

	public CTakesDocument parseDocument(Map<CTakesGroup, List<CTakesHit>> groups, InputStream text, String noteId) throws IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError{
		Document document = convertToHtml(text);

		Element documentElement = document.getDocumentElement();
		ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
		TransformerFactory.newInstance().newTransformer().transform(
				new DOMSource(documentElement), new StreamResult(outputStream2));

		NodeList pTags = documentElement.getElementsByTagName("p");

		HashMap<Integer, List<CTakesHit>> lineHits = new HashMap<Integer, List<CTakesHit>>();

		for(CTakesGroup group : groups.keySet()){
			logger.debug("processing group" + group);
			List<CTakesHit> hits = groups.get(group);
			for(int x = 0;x<pTags.getLength();x++){
				Node n = pTags.item(x);
				int lastChar = Integer.parseInt(n.getAttributes().getNamedItem("data-end-offset").getNodeValue());
				int firstChar = Integer.parseInt(n.getAttributes().getNamedItem("data-start-offset").getNodeValue());
				String id = n.getAttributes().getNamedItem("id").getNodeValue();
				ArrayList<CTakesHit> foundHits = new ArrayList<CTakesHit>();
				for(CTakesHit hit : hits){
					if(lastChar > hit.getStart_index() && firstChar <= hit.getEnd_index()){
						foundHits.add(hit);
					}
				}

				String textContent = n.getTextContent();
				if(textContent != null){
					if(foundHits.size()>0){
						logger.debug("processing node: " + x + " : " + ((Element)n).getAttribute("id"));
						logger.debug("processing hits: " + foundHits);
						logger.debug(id + " : " + firstChar + " : " + lastChar);
						List<CTakesHit> hitList = lineHits.get(x);
						if(hitList == null){
							hitList = new ArrayList<CTakesHit>();
							lineHits.put(x, hitList);
						}
						hitList.addAll(foundHits);
					}
				}
			}		
		}
		for(Integer lineNumber : lineHits.keySet()){
			Element n = (Element)pTags.item(lineNumber);
			List<CTakesHit> hitsForLine = lineHits.get(lineNumber);
			logger.debug("hitsForLine for : " + lineNumber + " - " + hitsForLine.size());
			Collections.sort(hitsForLine);
			String textContent = n.getTextContent();
			int currentCharacter = 0;
			int firstChar = Integer.parseInt(n.getAttributes().getNamedItem("data-start-offset").getNodeValue());

			TreeMap<Integer, List<CTakesHit>> wrapperMap = new TreeMap<Integer, List<CTakesHit>>();
			for(CTakesHit hit : hitsForLine){
				List<CTakesHit> hits = wrapperMap.get(hit.getStart_index());
				if(hits == null){
					hits = new ArrayList<CTakesHit>();
					wrapperMap.put(hit.getStart_index(), hits);
				}
				hits.add(hit);
			}
			Element newNode = document.createElement("p");
			newNode.setAttribute("data-end-offset", n.getAttribute("data-end-offset"));
			newNode.setAttribute("data-start-offset", n.getAttribute("data-start-offset"));
			for(List<CTakesHit> wrapper : wrapperMap.values()){
				try{
					CTakesHit firstHit = wrapper.get(0);
					logger.debug(firstHit);
					int hitStart = firstHit.getStart_index() - firstChar;
					int hitEnd = firstHit.getEnd_index() - firstChar;
					logger.debug("|" + textContent + "|" + currentCharacter + "|" + hitStart + "|" + textContent.length());
					String pre = textContent.substring(currentCharacter, hitStart);
					String groupList = "";
					for(CTakesHit hit : wrapper){
						groupList += hit.getGroupId() + " ";
					}
					Element newSpan = document.createElement("span");
					newSpan.setAttribute("class", firstHit.getCui() +" " + groupList);
					newSpan.setTextContent(textContent.substring(hitStart, hitEnd));
					currentCharacter = hitEnd;
					newNode.appendChild(document.createTextNode(pre));
					newNode.appendChild(newSpan);		
				} catch(StringIndexOutOfBoundsException e) {
					logger.error("Error parsing CTakesOutput for note : " + noteId);
					logger.error("At Line : " + lineNumber);
					logger.error("Processing hits : " + wrapper);
					logger.error("Message : " + e.getMessage());
				}
			}
			if(currentCharacter < textContent.length()){
				newNode.appendChild(document.createTextNode(textContent.substring(currentCharacter)));
			}
			documentElement.replaceChild(newNode, n);
		}

		//		System.out.println(documentElement.getTextContent());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		TransformerFactory.newInstance().newTransformer().transform(
				new DOMSource(documentElement), new StreamResult(outputStream));

		logger.trace("Output : " + outputStream.toString());

		String noteMarkup = Base64.getEncoder().encodeToString(outputStream.toString().getBytes("UTF-8"));

		return new CTakesDocument()
				.setGroups(new ArrayList<CTakesGroup>(groups.keySet()))
				.setMarkup(noteMarkup);
	}
}
