/**
 * bilderbuch - voice driven image search on the web
 * Copyright (C) 2014  Jan Mönnich
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cuseb.bilderbuch.images;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.*;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "images.flickr")
public class FlickrImageSearch implements ImageSearch {

    private String key;

    private String secret;

    private boolean enabled;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public List<Image> searchImages(String query) throws ImageSearchException {

        Flickr flickr = new Flickr(key, secret, new REST());

        PhotosInterface photosInterface = flickr.getPhotosInterface();

        HashSet<String> extras = new HashSet<String>();
        extras.add(Extras.URL_L);
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setText(query);
        searchParameters.setExtras(extras);
        searchParameters.setTagMode("all");

        List<Image> images = new ArrayList<Image>();
        try {
            PhotoList photoList = photosInterface.search(searchParameters, 5, 0);
            for (Photo photo : (List<Photo>) photoList) {
                Image image = new Image();
                image.setSource("flickr");
                image.setTitle(photo.getTitle());
                image.setUrl(photo.getLargeUrl());
                images.add(image);
            }
        } catch (FlickrException e) {
            throw new ImageSearchException(e.getMessage(), e.getCause());
        }

        return images;
    }

}
