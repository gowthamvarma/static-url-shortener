package com.gowthamvarma.static_url_shortener.service;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gowthamvarma.static_url_shortener.config.Config;
import com.gowthamvarma.static_url_shortener.models.ShortUrl;
import com.gowthamvarma.static_url_shortener.repo.StaticUrlShortenerRepository;

@Service
public class StaticUrlShortenerService {
	
	@Autowired
	private StaticUrlShortenerRepository repo;

	public List<ShortUrl> getShortUrls(ShortUrl shortUrl) {
		Iterable<ShortUrl> iterable = repo.findAll();
		List<ShortUrl> list = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
			
		String name = shortUrl.getName();
		if(name  != null && name.length() > 0) {
			list = list.stream().filter(site -> site.getName().toLowerCase().indexOf(name.toLowerCase()) > -1 ).collect(Collectors.toList());
		}
		
		String urlOriginal = shortUrl.getUrlOriginal();
		if(urlOriginal != null && urlOriginal.length() > 0) {
			list = list.stream().filter(site -> site.getUrlOriginal().toLowerCase().indexOf(urlOriginal.toLowerCase()) > -1 ).collect(Collectors.toList());
		}
		
		String urlShort = shortUrl.getUrlShort();
		if(urlShort != null && urlShort.length() > 0) {
			list = list.stream().filter(site -> site.getUrlShort().toLowerCase().indexOf(urlShort.toLowerCase()) > -1 ).collect(Collectors.toList());
		}
		
		/*
		int urlShortLength = shortUrl.getUrlShortLength();
		if(urlShortLength > 0) {
			list = list.stream().filter(site -> urlShortLength == site.getUrlShortLength()).collect(Collectors.toList());
		}
		*/
		
		String isActive = shortUrl.getIsActive();
		if(isActive != null && isActive.length() > 0) {
			list = list.stream().filter(site -> site.getIsActive().toLowerCase().indexOf(isActive.toLowerCase()) > -1 ).collect(Collectors.toList());
		}
		
		return list;
	}
	
	public ShortUrl postShortUrl(ShortUrl site) {
		return repo.save(site);
	}
	
	public ShortUrl updateShortUrl(ShortUrl site) {
		return repo.save(site);
	}
	
	public void deleteShortUrl(ShortUrl site) {
		repo.deleteById(site.getId());
	}

	public void syncUrls() {
		String localSyncPath = Config.PATH_LOCAL_SYNC;
		String templatePath = Config.PATH_TEMPLATE;
		boolean deleteExisting = Config.DELETE_EXISTING_URL_FOLDERS;
		
		Iterable<ShortUrl> iterable = repo.findAll();
		for (ShortUrl shortUrl : iterable) {
			// check if file exists
			// Get the file 
	        File file = new File(localSyncPath + shortUrl.getUrlShort() + Config.INDEX_HTML); 
	        File template = new File(templatePath); 

	        if (file.exists()) {
	            System.out.println("Folder exists");
	        	// if file is present delete and create file if deleteExisting is true
	        	if(deleteExisting) {
	        		System.out.println("Deleting folder");
	        		file.delete();
	        		createFile(template,file,shortUrl.getUrlOriginal());
	        	}
	        } else {
	        	System.out.println("Folder doesn't exist"); 
	        	// if file is not present create file
	        	createFile(template,file,shortUrl.getUrlOriginal());
	        }
	        
		}
		
		// java exec bat command
		if(Config.COMMIT_GITHUB) {
			try {
	            String[] command = {"cmd.exe", "/C", "Start", "E:\\github\\static-site\\s\\commit.bat"};
	            Process p =  Runtime.getRuntime().exec(command);           
	        } catch (IOException ex) {
	        	System.out.println("error in commiting code");
	        }
		}
		
	}

	private void createFile(File template, File file, String urlOriginal) {
		try {
			File directory = new File(file.getParentFile().getAbsolutePath());
			directory.mkdirs();
			Files.copy(template.toPath(), file.toPath());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Path path = Paths.get(file.getAbsolutePath());
			Stream<String> lines = Files.lines(path);
			List<String> replaced = lines.map(line -> line.replaceAll(Config.URL_TO_REPLACE, urlOriginal))
					.collect(Collectors.toList());
			Files.write(path, replaced);
			lines.close();
			System.out.println("Find and Replace done!!!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void activeCheck() {
		Iterable<ShortUrl> iterable = repo.findAll();
		for (ShortUrl shortUrl : iterable) {
			String isActive = shortUrl.getIsActive();
			String isActiveNew = checkActive(shortUrl.getUrlOriginal());
			System.out.println(shortUrl.getUrlOriginal() + " :: " + isActiveNew);
			if(isActive != null && !isActive.equalsIgnoreCase(isActiveNew)) {
				shortUrl.setIsActive(isActiveNew);
				repo.save(shortUrl);
			}
		}
	}

	private String checkActive(String urlOriginal) {
		
		int responseCode = 0;
		
		try {
			URL url = new URL(urlOriginal);
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			responseCode = huc.getResponseCode();
		} catch (IOException e) {
			System.err.println("error in checking URL");
		}
		  
		//System.out.println("responseCode :: " + responseCode);
		
		if(HttpURLConnection.HTTP_OK == responseCode) {
			return "Y";
		}
		return "N";
		
	}
}
