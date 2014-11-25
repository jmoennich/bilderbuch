package de.cuseb.bilderbuch.images

import spock.lang.Specification

class ImageSearchServiceSpec extends Specification {

    ImageSearchService service = new ImageSearchService()

    def "SetShuffle"() {

    }

    def "SetTimeout"() {

    }

    def "SearchImages"() {

        given:
        service.googleImageSearch = Mock(GoogleImageSearch)
        service.flickrImageSearch = Mock(FlickrImageSearch)
        service.bingImageSearch = Mock(BingImageSearch)
        service.deviantArtImageSearch = Mock(DeviantArtImageSearch)
        service.init()

        expect:
        service.searchImages("query").images == []
    }
}
