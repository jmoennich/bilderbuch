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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

@Service
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "images.deviantart")
public class DeviantArtImageSearch implements ImageSearch {

    @Autowired
    private HttpClientProviderService httpClientProviderService;

    private Logger log = Logger.getLogger(BingImageSearch.class);

    private boolean enabled;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public List<Image> searchImages(String query) throws ImageSearchException {

        List<Image> result = new ArrayList<Image>();

        try {
            HttpClient client = httpClientProviderService.getHttpClient();
            HttpUriRequest request = RequestBuilder
                    .get()
                    .setUri("http://backend.deviantart.com/rss.xml")
                    .addParameter("limit", "10")
                    .addParameter("q", "in=photography " + query)
                    .build();
            HttpResponse response = client.execute(request);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(response.getEntity().getContent());

            NodeList items = document.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                NodeList title = item.getElementsByTagName("title");
                NodeList content = item.getElementsByTagName("media:content");
                if (content.getLength() > 0) {
                    Node url = content.item(0).getAttributes().getNamedItem("url");
                    if (url != null) {
                        Image image = new Image();
                        image.setSource("deviantart");
                        image.setUrl(url.getTextContent());
                        image.setTitle(title.item(0).getFirstChild().getTextContent());
                        result.add(image);
                    }
                }
            }
        } catch (Exception e) {
            log.error("searchImages", e);
        }
        return result;
    }

}
