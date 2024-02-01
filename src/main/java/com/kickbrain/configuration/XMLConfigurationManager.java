package com.kickbrain.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import com.kickbrain.beans.configuration.ChallengeConfig;
import com.kickbrain.beans.configuration.GameConfig;

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
			
			int answerFullMatchRatio = xmlConfiguration.getInt("answerFullMatchRatio");
			int answerPartMatchRatio = xmlConfiguration.getInt("answerPartMatchRatio");
			int delayNextChallenge = xmlConfiguration.getInt("delayNextChallenge");
			int underMaintenance = xmlConfiguration.getInt("underMaintenance");
			float premiumPointsRatio = xmlConfiguration.getFloat("premiumPointsRatio");
			float minimumPremiumPointsPrivateGame = xmlConfiguration.getFloat("minimumPremiumPointsPrivateGame");
			float adToPremiumPoints = xmlConfiguration.getFloat("adToPremiumPoints");
			
			appConfiguration.setAnswerFullMatchRatio(answerFullMatchRatio);
			appConfiguration.setAnswerPartMatchRatio(answerPartMatchRatio);
			appConfiguration.setDelayNextChallenge(delayNextChallenge);
			appConfiguration.setPremiumPointsRatio(premiumPointsRatio);
			appConfiguration.setMinimumPremiumPointsPrivateGame(minimumPremiumPointsPrivateGame);
			appConfiguration.setUnderMaintenance(underMaintenance);
			appConfiguration.setAdToPremiumPoints(adToPremiumPoints);
			
			GameConfig onlineGameConfig = new GameConfig();
			HierarchicalConfiguration onlineConfig = xmlConfiguration.configurationAt("onlineGame");
			List<HierarchicalConfiguration> challengesConfig = (List<HierarchicalConfiguration>)onlineConfig.configurationsAt("challenges.challenge");
			List<ChallengeConfig> challenges = new ArrayList<ChallengeConfig>();
			for(HierarchicalConfiguration challengeConfig : challengesConfig)
			{
				ChallengeConfig onlineChallengeConfig = new ChallengeConfig();
				
				int category = challengeConfig.getInt("category");
				onlineChallengeConfig.setCategory(category);
				onlineChallengeConfig.setNbQuestions(challengeConfig.getInt("nbQuestions"));

				onlineChallengeConfig.setTitleEn(challengeConfig.getString("titleEn"));
				onlineChallengeConfig.setTitleAr(challengeConfig.getString("titleAr"));
				onlineChallengeConfig.setAppearInSingleGame(challengeConfig.getInt("appearInSingleGame"));
				onlineChallengeConfig.setAnswerTimer(challengeConfig.getInt("answerTimer"));
				
				if(category == 3)
				{
					onlineChallengeConfig.setBellTimer(challengeConfig.getInt("bellTimer"));
				}
				if(category == 2)
				{
					onlineChallengeConfig.setBidTimer(challengeConfig.getInt("bidTimer"));
				}
				
				challenges.add(onlineChallengeConfig);
			}
			
			onlineGameConfig.setChallenges(challenges);
			appConfiguration.setOnlineGameConfig(onlineGameConfig);
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
