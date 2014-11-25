package de.cuseb.bilderbuch.helpers

import org.apache.http.client.HttpClient
import spock.lang.Specification

class HttpClientProviderServiceSpec extends Specification {

    HttpClientProviderService service = new HttpClientProviderService()

    def "GetHttpClient"() {

        expect:
        service.getHttpClient() instanceof HttpClient
    }

    def "GetHttpClientWithBasicAuth"() {

        expect:
        service.getHttpClientWithBasicAuth("user", "pass") instanceof HttpClient
    }

}
