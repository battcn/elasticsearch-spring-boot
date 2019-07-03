package com.battcn.boot.elasticsearch.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:1837307557@qq.com">Levin</a>
 * @since 2019-07-03
 */
@Data
@ConfigurationProperties(prefix = "spring.elasticsearch.rest")
public class ElasticsearchRestClientProperties {

    /**
     * Elasticsearch cluster name.
     */
    private String clusterName = "elasticsearch";
    /**
     * Comma-separated list of the Elasticsearch instances to use.
     */
    private List<String> uris = new ArrayList<>(Collections.singletonList("http://localhost:9200"));

    /**
     * Credentials username.
     */
    private String username;

    /**
     * Credentials password.
     */
    private String password;

    public List<String> getUris() {
        return this.uris;
    }


}
