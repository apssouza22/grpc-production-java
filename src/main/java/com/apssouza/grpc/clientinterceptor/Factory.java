package com.apssouza.grpc.clientinterceptor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import io.grpc.ClientInterceptor;

/**
 * Responsible for create all client interceptors
 */
public class Factory {

    private Factory() {
    }

    /**
     * Factory all default interceptors
     *
     * @return aggregated client interceptors
     */
    public static AggregateClientInterceptor allDefaultInterceptors() {
        List<ClientInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new AuditClientInterceptor());
        interceptors.add(new PropHeaderClientInterceptor());
        interceptors.add(new DefaultDeadlineInterceptor(Duration.ofSeconds(5)));
        return new AggregateClientInterceptor(interceptors);
    }
}
