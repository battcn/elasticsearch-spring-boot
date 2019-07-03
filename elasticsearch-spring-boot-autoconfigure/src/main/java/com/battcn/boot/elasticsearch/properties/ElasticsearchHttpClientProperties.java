package com.battcn.boot.elasticsearch.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author <a href="mailto:1837307557@qq.com">Levin</a>
 * @since 2019-07-03
 */
@Data
@ConfigurationProperties(prefix = "spring.elasticsearch.http-client")
public class ElasticsearchHttpClientProperties {

    private Integer maxTotal;
    private Integer defaultMaxPerRoute;
    private Integer connectTimeout;
    private Integer readTimeout;
    private Integer connectionRequestTimeout;

}
