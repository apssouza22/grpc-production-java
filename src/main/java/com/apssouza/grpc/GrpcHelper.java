package com.apssouza.grpc;

import java.util.Map;

import io.grpc.Context;

/**
 * Contain gRPC helpers
 */
public class GrpcHelper {

    public static final Context.Key<Map<String, String>> CTX_HEADERS = Context.key("context-headers");

}
