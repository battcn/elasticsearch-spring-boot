package com.battcn.boot.elasticsearch.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author <a href="mailto:1837307557@qq.com">Levin</a>
 * @since 2019-07-03
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ConfigurationProperties(prefix = "spring.elasticsearch.rest")
public class ElasticsearchRestClientProperties extends RestClientProperties {

    /**
     * Elasticsearch cluster name.
     */
    private String clusterName = "elasticsearch";

    /**
     * Comma-separated list of cluster node addresses.
     */
    private String clusterNodes;


}
