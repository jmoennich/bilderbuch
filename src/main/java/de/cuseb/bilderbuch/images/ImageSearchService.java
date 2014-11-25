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

import com.google.common.util.concurrent.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "images.search")
public class ImageSearchService {

    @Autowired
    private GoogleImageSearch googleImageSearch;

    @Autowired
    private FlickrImageSearch flickrImageSearch;

    @Autowired
    private BingImageSearch bingImageSearch;

    @Autowired
    private DeviantArtImageSearch deviantArtImageSearch;

    private HashMap<String, ImageSearch> searches;
    private Logger log = Logger.getLogger(ImageSearchService.class);

    private boolean shuffle;
    private long timeout;

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @PostConstruct
    public void init() {
        searches = new LinkedHashMap<String, ImageSearch>();
        searches.put("bing", bingImageSearch);
        searches.put("deviantart", deviantArtImageSearch);
        searches.put("flickr", flickrImageSearch);
        searches.put("google", googleImageSearch);
    }

    public ImageResponse searchImages(final String query) {

        final ImageResponse response = new ImageResponse();
        final ListeningExecutorService executor = MoreExecutors.listeningDecorator(
                Executors.newFixedThreadPool(searches.size()));

        for (final ImageSearch search : searches.values()) {

            if (!search.isEnabled()) {
                continue;
            }

            ListenableFuture<List<Image>> searchResult = executor.submit(new Callable<List<Image>>() {
                @Override
                public List<Image> call() throws Exception {
                    log.debug("starting enabled search " + search.getClass().getSimpleName());
                    return search.searchImages(query);
                }
            });

            Futures.addCallback(searchResult, new FutureCallback<List<Image>>() {
                @Override
                public void onSuccess(List<Image> result) {
                    log.debug(search.getClass().getSimpleName() + " result size: " + result.size());
                    response.addImages(result);
                }

                @Override
                public void onFailure(Throwable t) {
                    log.error(search.getClass().getSimpleName(), t);
                }
            });
        }

        try {
            executor.shutdown();
            executor.awaitTermination(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("awaitTermination interrupted", e);
        }

        if (shuffle) {
            log.debug("shuffling result");
            response.shuffle();
        }

        return response;
    }

}
