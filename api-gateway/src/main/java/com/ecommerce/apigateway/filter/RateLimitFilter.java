package com.ecommerce.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter  implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);

    private static final int MAX_REQUEST = 100;
    private static final long WINDOW_DURATION = 60000;

    private final Map<String, Long> windowCount = new ConcurrentHashMap<>() ;
    private final Map<String, AtomicInteger> requestCount = new ConcurrentHashMap<>();



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    //Get CLIENT IP
      String clientIp =   exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();

      long currentTime = System.currentTimeMillis();

      //for new client Ip
      windowCount.putIfAbsent(clientIp,currentTime);
      requestCount.putIfAbsent(clientIp, new AtomicInteger(0));

      long startTime = windowCount.get(clientIp);

      //reset counter if window expired
      if((currentTime - startTime) > WINDOW_DURATION )
      {
            windowCount.put(clientIp,currentTime);
            requestCount.put(clientIp,new AtomicInteger(0));
      }

      //Increment Counter
        int requests = requestCount.get(clientIp).incrementAndGet();

      if(requests > MAX_REQUEST)
      {
          logger.warn("Rate Limit exceeded for IP : {}", clientIp);
          exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
          return exchange.getResponse().setComplete();
      }

        logger.info("IP: {} → Request {}/{}", clientIp, requests, MAX_REQUEST);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -3;
    }
}
