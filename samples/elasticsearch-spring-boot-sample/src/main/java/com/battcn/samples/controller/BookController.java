package com.battcn.samples.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.battcn.samples.entity.Book;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author Levin
 */
@Slf4j
@RestController
@RequestMapping("/books")
@AllArgsConstructor
@Api(tags = "1.0.0", description = "书籍信息", value = "书籍信息")
public class BookController {

    private final RestHighLevelClient restHighLevelClient;

    @GetMapping("/{index}/{id}")
    public Book find(@PathVariable String index, @PathVariable String id) throws IOException {
        GetRequest getRequest = new GetRequest(index, id);
        GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        return JSONObject.parseObject(response.getSourceAsBytes(), Book.class);
    }

    @PostMapping("/{index}")
    public IndexResponse add(@PathVariable String index, @RequestBody Book book) throws IOException {
        IndexRequest request = new IndexRequest(index).id(book.getId() + "").source(JSON.toJSONString(book), XContentType.JSON);
        // 设置 10 秒的超时时间
        request.timeout(TimeValue.timeValueSeconds(10));
        //ShardId shardId = new ShardId(new Index("my-shard", "uuid"), 10);
        //request.setShardId(shardId);
        IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        DocWriteResponse.Result indexResponseResult = indexResponse.getResult();
        if (indexResponseResult == DocWriteResponse.Result.CREATED) {
            // 处理(如果需要)第一次创建文档的情况
        } else if (indexResponseResult == DocWriteResponse.Result.UPDATED) {
            //处理(如果需要)文档被重写的情况，因为它已经存在
        }
        ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {

        }
        if (shardInfo.getFailed() > 0) {
            for (ReplicationResponse.ShardInfo.Failure failure :
                    shardInfo.getFailures()) {
                String reason = failure.reason();
                log.warn("失败原因 {}", reason);
            }
        }
        return indexResponse;

    }

    @GetMapping("/query")
    public MultiGetResponse query() throws IOException {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 设置 query 查询方式
        sourceBuilder.query(QueryBuilders.termQuery("user", "kimchy"));
        // 过滤返回的 source 数据
        sourceBuilder.fetchSource(true);
        // 设置排序
        sourceBuilder.sort("id", SortOrder.DESC);
        String[] includeFields = new String[]{"title", "innerObject.*"};
        String[] excludeFields = new String[]{"user"};
        // 接受一个或多个通配符模式的数组，以控制以更精细的方式包含或排除哪些字段：
        sourceBuilder.fetchSource(includeFields, excludeFields);

        // 设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightTitle = new HighlightBuilder.Field("title");
        // 设置高亮的类型（选填）
        highlightTitle.highlighterType("unified");
        // 将字段突出显示器添加到突出显示构建器
        highlightBuilder.field(highlightTitle);
        sourceBuilder.highlighter(highlightBuilder);
        // 设置分页
        sourceBuilder.from(1);
        sourceBuilder.size(10);
        // 谁知分数（类似权重）
        sourceBuilder.minScore(1.2F);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(sourceBuilder);
        restHighLevelClient.searchAsync(searchRequest, RequestOptions.DEFAULT, new ActionListener<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse searchResponse) {
                log.info("[成功的响应] - [{}]", searchResponse);
            }

            @Override
            public void onFailure(Exception e) {
                log.error("[失败的响应]", e);
            }
        });


        MultiGetRequest request = new MultiGetRequest();
        request.add(new MultiGetRequest.Item("books", "1"));
        request.add(new MultiGetRequest.Item("books", "2"));
        request.add(new MultiGetRequest.Item("books", "3")
                .fetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE));
        return restHighLevelClient.mget(request, RequestOptions.DEFAULT);
    }

}
