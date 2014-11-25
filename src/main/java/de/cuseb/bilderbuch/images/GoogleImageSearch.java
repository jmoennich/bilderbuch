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

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "images.google")
public class GoogleImageSearch implements ImageSearch {

    private String key;

    private String cx;

    private boolean enabled;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    private Customsearch getCustomsearch() {
        return new Customsearch(new NetHttpTransport(), new JacksonFactory(), null);
    }

    @Override
    public List<Image> searchImages(String query) throws ImageSearchException {

        try {
            Customsearch customsearch = getCustomsearch();
            Customsearch.Cse.List list = customsearch.cse().list(query);
            list.setKey(key);
            list.setCx(cx);
            list.setSearchType("image");
            list.setImgSize("xlarge");
            list.setImgType("photo");

            Search results = list.execute();
            List<Image> images = new ArrayList<Image>();

            for (Result result : results.getItems()) {
                Image image = new Image();
                image.setSource("google");
                image.setUrl(result.getLink());
                image.setTitle(result.getTitle());
                images.add(image);
            }
            return images;

        } catch (IOException e) {
            throw new ImageSearchException(e.getMessage(), e.getCause());
        }
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setCx(String cx) {
        this.cx = cx;
    }

}
