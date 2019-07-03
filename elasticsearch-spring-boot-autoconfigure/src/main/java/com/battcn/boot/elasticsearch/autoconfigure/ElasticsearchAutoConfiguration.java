package com.battcn.boot.elasticsearch.autoconfigure;

import com.battcn.boot.elasticsearch.properties.ElasticsearchHttpClientProperties;
import com.battcn.boot.elasticsearch.properties.ElasticsearchRestClientProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author <a href="mailto:1837307557@qq.com">Levin</a>
 * @since 2019-07-03
 */
@Slf4j
@AllArgsConstructor
@Configuration
@EnableAutoConfiguration(exclude = {org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration.class,
        org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration.class,
        org.springframework.boot.autoconfigure.elasticsearch.jest.JestAutoConfiguration.class
})
@EnableConfigurationProperties({ElasticsearchRestClientProperties.class, ElasticsearchHttpClientProperties.class})
@ConditionalOnProperty(name = "spring.elasticsearch", havingValue = "true", matchIfMissing = true)
public class ElasticsearchAutoConfiguration {

    private final ObjectProvider<RestClientBuilderCustomizer> builderCustomizers;


    @Bean
    @ConditionalOnMissingBean
    public RestClient restClient(RestClientBuilder builder) {
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public RestClientBuilder restClientBuilder(ElasticsearchRestClientProperties restClientProperties, ElasticsearchHttpClientProperties httpClientProperties) {
        HttpHost[] hosts = restClientProperties.getUris().stream().map(HttpHost::create).toArray(HttpHost[]::new);


        RestClientBuilder builder = RestClient.builder(hosts);
        PropertyMapper map = PropertyMapper.get();
        map.from(restClientProperties::getUsername).whenHasText().to((username) -> {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            Credentials credentials = new UsernamePasswordCredentials(restClientProperties.getUsername(), restClientProperties.getPassword());
            credentialsProvider.setCredentials(AuthScope.ANY, credentials);
            // 设置 http 客户端回调
            RestClientBuilder.HttpClientConfigCallback httpClientConfigCallback = httpClientBuilder ->
                    httpClientBuilder.setDefaultIOReactorConfig(
                            IOReactorConfig.custom()
                                    .setIoThreadCount(Runtime.getRuntime().availableProcessors() * 5)
                                    .build())
                            .setMaxConnTotal(httpClientProperties.getMaxTotal())
                            .setMaxConnPerRoute(httpClientProperties.getDefaultMaxPerRoute())
                            .setDefaultCredentialsProvider(credentialsProvider);
            builder.setHttpClientConfigCallback(httpClientConfigCallback);
        });
        builder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                .setConnectionRequestTimeout(httpClientProperties.getConnectionRequestTimeout())
                .setConnectTimeout(httpClientProperties.getConnectTimeout())
                .setSocketTimeout(httpClientProperties.getReadTimeout()))
                .setFailureListener(new RestClient.FailureListener() {
                    @Override
                    public void onFailure(Node node) {
                        log.error("elasticsearch server occur error.");
                        super.onFailure(node);
                    }
                });
        this.builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder;
    }

    @Configuration
    @ConditionalOnClass(RestHighLevelClient.class)
    public static class RestHighLevelClientConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public RestHighLevelClient restHighLevelClient(RestClientBuilder restClientBuilder) {
            return new RestHighLevelClient(restClientBuilder);
        }

    }


}
