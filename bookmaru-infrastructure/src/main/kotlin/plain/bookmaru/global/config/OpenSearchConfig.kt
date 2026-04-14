package plain.bookmaru.global.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.hc.client5.http.auth.AuthScope
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider
import org.apache.hc.core5.http.HttpHost
import org.opensearch.client.json.jackson.JacksonJsonpMapper
import org.opensearch.client.opensearch.OpenSearchClient
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder
import org.opensearch.data.client.osc.OpenSearchTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories
import plain.bookmaru.global.properties.OpenSearchProperties

@Configuration
@EnableElasticsearchRepositories(
    basePackages = ["plain.bookmaru.domain.search.persistent.repository"],
    elasticsearchTemplateRef = "opensearchTemplate"
)
class OpenSearchConfig(
    private val openSearchProperties: OpenSearchProperties
) {

    @Bean
    fun opensearchClient(): OpenSearchClient {
        val host = HttpHost("https", openSearchProperties.host, openSearchProperties.port.toInt())

        val credentialsProvider = BasicCredentialsProvider().apply {
            setCredentials(
                AuthScope(host),
                UsernamePasswordCredentials(
                    openSearchProperties.username,
                    openSearchProperties.password.toCharArray()
                )
            )
        }

        val transport = ApacheHttpClient5TransportBuilder.builder(host)
            .setMapper(JacksonJsonpMapper(jacksonObjectMapper()))
            .setHttpClientConfigCallback { httpClientBuilder ->
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
            }
            .build()

        return OpenSearchClient(transport)
    }

    @Bean
    fun opensearchTemplate(client: OpenSearchClient): ElasticsearchOperations {
        return OpenSearchTemplate(client)
    }
}