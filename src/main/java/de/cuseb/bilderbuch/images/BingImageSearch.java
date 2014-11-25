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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.cuseb.bilderbuch.helpers.HttpClientProviderService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "images.bing")
public class BingImageSearch implements ImageSearch {

    private boolean enabled;

    private String password;

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Autowired
    private HttpClientProviderService httpClientProviderService;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    private Logger log = Logger.getLogger(BingImageSearch.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Image> searchImages(String query) throws ImageSearchException {

        List<Image> result = new ArrayList<Image>();

        try {
            HttpClient client = httpClientProviderService.getHttpClientWithBasicAuth("", password);
            HttpUriRequest request = RequestBuilder
                    .get()
                    .setUri("https://api.datamarket.azure.com/Bing/Search/v1/Image")
                    .addParameter("Query", "'" + query + "'")
                    .addParameter("$format", "json")
                    .build();
            HttpResponse response = client.execute(request);

            JsonNode json = objectMapper.readTree(response.getEntity().getContent());
            Iterator<JsonNode> results = json
                    .get("d")
                    .get("results")
                    .elements();

            while (results.hasNext()) {
                JsonNode next = results.next();
                Image image = new Image();
                image.setSource("bing");
                image.setTitle(next.get("Title").textValue());
                image.setUrl(next.get("MediaUrl").textValue());
                result.add(image);
            }

        } catch (IOException e) {
            log.error("searchImages", e);
        }

        return result;
    }

}
