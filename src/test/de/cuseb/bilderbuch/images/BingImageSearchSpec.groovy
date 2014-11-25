package de.cuseb.bilderbuch.images

import de.cuseb.bilderbuch.helpers.HttpClientProviderService
import org.apache.http.ProtocolVersion
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.entity.BasicHttpEntity
import org.apache.http.message.BasicHttpResponse
import org.apache.http.message.BasicStatusLine
import spock.lang.Specification

class BingImageSearchSpec extends Specification {

    BingImageSearch service = new BingImageSearch()
    HttpClient httpClient

    def setup() {
        service.httpClientProviderService = Mock(HttpClientProviderService)
        httpClient = Mock(HttpClient)
    }

    def "SearchImages success"() {

        given:
        def response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 0), 200, "OK"))
        response.entity = new BasicHttpEntity(content: getClass().getClassLoader().getResourceAsStream("bing.json"))
        service.password = 'password'

        when:
        List<Image> result = service.searchImages('query')

        then:
        service.httpClientProviderService.getHttpClientWithBasicAuth('', 'password') >> httpClient
        httpClient.execute({ HttpUriRequest request ->
            request.URI.toString() == 'https://api.datamarket.azure.com/Bing/Search/v1/Image?Query=%27query%27&%24format=json'
        }) >> response

        and:
        result.size() == 2
        result[0].source == 'bing'
        result[0].title == '32 Test Clip Art'
        result[0].url == 'http://bestclipartblog.com/clipart-pics/-test-clipart-9.jpg'
        result[1].source == 'bing'
        result[1].title == '... test. The new findings reveal some pretty alarming statistics'
        result[1].url == 'http://www.citizenship-aei.org/wp-content/uploads/Testing.jpg'
    }

    def "SearchImages failure should return empty list"() {

        when:
        List<Image> result = service.searchImages('query')

        then:
        service.httpClientProviderService.getHttpClientWithBasicAuth(_, _) >> httpClient
        httpClient.execute(_) >> { throw new IOException() }

        and:
        result == []
    }

}
