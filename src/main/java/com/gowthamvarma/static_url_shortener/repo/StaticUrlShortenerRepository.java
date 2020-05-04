package com.gowthamvarma.static_url_shortener.repo;

import org.springframework.data.repository.CrudRepository;

import com.gowthamvarma.static_url_shortener.models.ShortUrl;

public interface StaticUrlShortenerRepository extends CrudRepository<ShortUrl, Long> {
	
}