package de.cuseb.bilderbuch.images

import de.cuseb.bilderbuch.helpers.HttpClientProviderService
import org.apache.http.ProtocolVersion
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.entity.BasicHttpEntity
import org.apache.http.message.BasicHttpResponse
import org.apache.http.message.BasicStatusLine
import spock.lang.Specification

import javax.imageio.IIOException

class DeviantArtImageSearchSpec extends Specification {

    DeviantArtImageSearch service = new DeviantArtImageSearch()

    HttpClient httpClient

    def setup() {
        service.httpClientProviderService = Mock(HttpClientProviderService)
        httpClient = Mock(HttpClient)
    }

    def "SetEnabled"() {

        when:
        service.setEnabled(value)

        then:
        service.@enabled == value

        where:
        value << [true, false]
    }

    def "IsEnabled"() {

        given:
        service.@enabled = value

        expect:
        service.isEnabled() == value

        where:
        value << [true, false]
    }

    def "SearchImages success"() {

        given:
        def response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 0), 200, "OK"))
        response.entity = new BasicHttpEntity(content: getClass().getClassLoader().getResourceAsStream("deviantart.xml"))

        when:
        List<Image> result = service.searchImages('query')

        then:
        service.httpClientProviderService.getHttpClient() >> httpClient
        httpClient.execute({ HttpUriRequest request ->
            println request.URI
            request.URI.toString() == 'http://backend.deviantart.com/rss.xml?limit=10&q=in%3Dphotography+query'
        }) >> response

        and:
        result.size() == 2
        result[0].source == 'deviantart'
        result[0].title == 'Backstage-51'
        result[0].url == 'http://th00.deviantart.net/fs70/PRE/i/2014/328/2/f/backstage_51_by_station_0-d87ldte.jpg'
        result[1].source == 'deviantart'
        result[1].title == 'Wild'
        result[1].url == 'http://fc05.deviantart.net/fs70/i/2014/328/1/f/wild_by_merinblue-d87jm5p.jpg'
    }

    def "SearchImages failure should return an empty list"() {

        when:
        List<Image> result = service.searchImages('query')

        then:
        service.httpClientProviderService.getHttpClient() >> httpClient
        httpClient.execute(_) >> { throw new IIOException() }

        and:
        result == []
    }
}
