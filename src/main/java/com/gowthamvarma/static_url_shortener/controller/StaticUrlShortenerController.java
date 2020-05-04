package com.gowthamvarma.static_url_shortener.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gowthamvarma.static_url_shortener.models.ShortUrl;
import com.gowthamvarma.static_url_shortener.service.StaticUrlShortenerService;
 
@RestController
public class StaticUrlShortenerController {
	
	@Autowired
	private StaticUrlShortenerService siteService;
	
	@GetMapping("/short-url")
	public ResponseEntity<List<ShortUrl>> getShortUrls(@RequestParam(required = false) String name,
			@RequestParam(required = false) String urlOriginal, @RequestParam(required = false) String urlShort,
			@RequestParam(required = false) String isActive) {
		ShortUrl shortUrl = new ShortUrl();
		shortUrl.setName(name);
		shortUrl.setUrlOriginal(urlOriginal);
		shortUrl.setUrlShort(urlShort);
		shortUrl.setIsActive(isActive);
		return ResponseEntity.ok(siteService.getShortUrls(shortUrl));
	}
	
	@PostMapping("/short-url")
	public ResponseEntity<ShortUrl> postShortUrl(ShortUrl site) {
		return  ResponseEntity.ok(siteService.postShortUrl(site));
	}
	
	@PutMapping("/short-url")
	public ResponseEntity<ShortUrl> putShortUrl(ShortUrl site) {
		return  ResponseEntity.ok(siteService.updateShortUrl(site));
	}
	
	@DeleteMapping("/short-url")
	public void deleteShortUrl(ShortUrl site) {
		siteService.deleteShortUrl(site);
	}
	
	@GetMapping("/short-url-sync")
	public ResponseEntity<String> syncUrls() {
		siteService.syncUrls();
		return ResponseEntity.ok("Sync completed");
	}
	
	@GetMapping("/short-url-active-check")
	public ResponseEntity<String> activeCheck() {
		siteService.activeCheck();
		return ResponseEntity.ok("Active check completed");
	}
}