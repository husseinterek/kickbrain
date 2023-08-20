package com.kickbrain.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.kickbrain.beans.configuration.AppConfiguration;
import com.kickbrain.beans.configuration.Question;

@Service
public class XMLConfigurationManager {
	
	private AppConfiguration appConfiguration;
	private File xmlFile;

	public XMLConfigurationManager(){
		try
		{
			xmlFile = ResourceUtils.getFile("classpath:app.xml");
			loadConfiguration();
			fileChangedListener(xmlFile);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void loadConfiguration()
	{
		try
		{
			appConfiguration = new AppConfiguration();
			
			XMLConfiguration xmlConfiguration = new XMLConfiguration();
			xmlConfiguration.setDelimiterParsingDisabled(true);
			xmlConfiguration.load(xmlFile);
			
			List<HierarchicalConfiguration> questionsConfig = (List<HierarchicalConfiguration>)xmlConfiguration.configurationsAt("question");
			int i=1;
			Map<Integer, Question> questionsMap = new HashMap<Integer, Question>();
			List<Question> questions = new ArrayList<Question>();
			for(HierarchicalConfiguration questionConfig : questionsConfig)
			{
				Question question = new Question();
				
				String promptEn = questionConfig.getString("prompt-en").trim();
				String promptAr = questionConfig.getString("prompt-ar").trim();
				List<String> answers= Stream.of(questionConfig.getString("answers").split(","))
					     .map(String::trim)
					     .collect(Collectors.toList());
				
				question.setPromptAr(promptAr);
				question.setPromptEn(promptEn);
				question.setAnswers(answers);
				question.setId(i++);
				
				questionsMap.put(question.getId(), question);
				
				questions.add(question);
			}
			
			appConfiguration.setQuestionsMap(questionsMap);
			appConfiguration.setQuestions(questions);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * This method is called to send a listener on the file being modified or
	 * changed.
	 * 
	 * @param file
	 * @throws FileSystemException
	 */
	private void fileChangedListener(File file) throws FileSystemException {

		FileSystemManager fsManager = VFS.getManager();
		FileObject listendir = fsManager.resolveFile(file.getAbsolutePath());

		DefaultFileMonitor fm = new DefaultFileMonitor(new FileListener() {

			@Override
			public void fileChanged(FileChangeEvent arg0) throws Exception {
				System.out.println("File Change event ");
				loadConfiguration();
			}

			@Override
			public void fileCreated(FileChangeEvent arg0) throws Exception {
				System.out.println("File Created event ");
				loadConfiguration();
			}

			@Override
			public void fileDeleted(FileChangeEvent arg0) throws Exception {
				System.out.println("File Delete event ");
				loadConfiguration();
			}
		});
		fm.setRecursive(true);
		fm.addFile(listendir);
		fm.start();
	}
	
	public AppConfiguration getAppConfigurationBean() {
		return appConfiguration;
	}
}
