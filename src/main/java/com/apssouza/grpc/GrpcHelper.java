package com.apssouza.grpc;

import java.util.Map;

import io.grpc.Context;

/**
 * Contain gRPC helpers
 */
public class GrpcHelper {

    public static final Context.Key<Map<String, String>> CTX_HEADERS = Context.key("context-headers");
    public static final String HEALTH_CHECK_METHOD_NAME = "grpc.health.v1.Health/Check";
    public static final String EVENT_ID = "event-id";
    public static final String STATUS = "status";
    public static final String ERROR = "error";
}
