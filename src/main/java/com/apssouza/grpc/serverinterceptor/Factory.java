package com.apssouza.grpc.serverinterceptor;

import java.util.ArrayList;
import java.util.List;

import io.grpc.ServerInterceptor;

/**
 * Responsible for create all server interceptors
 */
public class Factory {

    private Factory() {
    }

    /**
     * Factory all default interceptors
     *
     * @return list of interceptors
     */
    public static List<ServerInterceptor> allDefaultInterceptors() {
        List<ServerInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new ExceptionServerInterceptor());
        interceptors.add(new HeaderToContextServerInterceptor());
        interceptors.add(new AuditServerInterceptor());
        return interceptors;
    }

    /**
     * Factory AggregateServerInterceptor
     *
     * @return AggregateServerInterceptor with the list of interceptors
     */
    public static ServerInterceptor aggregateServerInterceptor(final List<ServerInterceptor> interceptors) {
        return new AggregateServerInterceptor(interceptors);
    }
}
