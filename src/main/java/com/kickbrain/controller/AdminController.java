package com.kickbrain.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kickbrain.beans.BaseResult;
import com.kickbrain.beans.GeneralLookup;
import com.kickbrain.beans.SubmitQuestionRequest;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@RequestMapping(value = "/submitQuestion", method = RequestMethod.POST, consumes="application/json")
	public BaseResult submitQuestion(@RequestBody SubmitQuestionRequest request) {
		
		GeneralLookup result = new GeneralLookup();
		
		String questionEn = request.getQuestionEn();
		String questionAr = request.getQuestionAr();
		String answers = request.getAnswers();
		
		if(StringUtils.isEmpty(questionAr) || StringUtils.isEmpty(questionEn) || StringUtils.isEmpty(answers))
		{
			result.setStatus(0);
		}
		else
		{
			boolean res = addQuestionToXML(questionEn, questionAr, answers);
			result.setStatus(res ? 1 : 0);
		}
		
		return result;
	}
	
	private static boolean addQuestionToXML(String questionEn, String questionAr, String answers)
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		boolean result = true;
		
		try
		{
			File sourceXmlFile = ResourceUtils.getFile("classpath:app.xml");
			File backupFile = ResourceUtils.getFile("classpath:app-backup.xml");
			
			// Take backup of current file
		    try (InputStream is = new FileInputStream(sourceXmlFile); OutputStream os = new FileOutputStream(backupFile)) {
		        byte[] buffer = new byte[1024];
		        int length;
		        while ((length = is.read(buffer)) > 0) {
		            os.write(buffer, 0, length);
		        }
		    }
		    catch(Exception ex)
		    {
		    	result = false;
		    	ex.printStackTrace();
		    }

	        try (InputStream inputStream = new FileInputStream(sourceXmlFile)) {
	        	
	        	DocumentBuilder db = dbf.newDocumentBuilder();
	        	Document doc = db.parse(inputStream);
	        	NodeList questionsElementLst = doc.getElementsByTagName("questions");
	        	org.w3c.dom.Node questionElement = questionsElementLst.item(0);
	        	
	        	Element question = doc.createElement("question");
	        	Element questionEnElement = doc.createElement("prompt-en");
	        	questionEnElement.appendChild(doc.createTextNode(questionEn));
	        	question.appendChild(questionEnElement);
	        	
	        	Element questionArElement = doc.createElement("prompt-ar");
	        	questionArElement.appendChild(doc.createTextNode(questionAr));
	        	question.appendChild(questionArElement);
	        	
	        	Element answersElement = doc.createElement("answers");
	        	answersElement.appendChild(doc.createTextNode(answers));
	        	question.appendChild(answersElement);
	        	
	        	questionElement.appendChild(question);
	        	
	        	try (FileOutputStream output = new FileOutputStream(sourceXmlFile)) {
	        		TransformerFactory transformerFactory = TransformerFactory.newInstance();

	        		// add a xslt to remove the extra newlines
	        		Transformer transformer = transformerFactory.newTransformer();

	        		// pretty print
	        		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");

	        		DOMSource source = new DOMSource(doc);
	        		StreamResult streamResult = new StreamResult(output);

	        		transformer.transform(source, streamResult);
			       }
	        } catch (Exception ex) {
	        	result = false;
	            ex.printStackTrace();
	        }
		}
		catch(Exception ex)
		{
			result = false;
			ex.printStackTrace();
		}
		
		return result;
	}
	
}
