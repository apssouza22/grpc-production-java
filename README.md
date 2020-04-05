# gRPC for production

This project abstracts away the details of the GRPC server and client configuration. 

Here are the main features:
- Health check service — We use the grpc_health_probe utility which allows you to query health of gRPC services that expose service their status through the gRPC Health Checking Protocol.
- Shutdown hook — The library registers a shutdown hook with the GRPC server to ensure that the application is closed gracefully on exit
- Keep alive params — Keepalives are an optional feature but it can be handy to signal how the persistence of the open connection should be kept for further messages
- In memory communication between client and server, helpful to write unit and integration tests. When writing integration tests we should avoid having the networking element from your test as it is slow to assign and release ports.
- Server and client builder for uniform object creation
- Added ability to recover the system from a Runtime exception in the request
- Added ability to add multiple interceptors in order
- Added client tracing metadata propagation
- Handy Server interceptors(Authentication, request cancelled, execution time, panic recovery)
- Handy Client interceptors(Timeout logs, Tracing, default call options)
 
 ## Examples
 
 ```
    ServerInterceptor aggregate = new AggregateServerInterceptor(
        new CancelledRequestInterceptor(),
        new UnexpectedExceptionInterceptor()
    );
    List<ServerInterceptor> interceptors =  Arrays.asList(aggregate);
    List<BindableService> services = new ArrayList<>();
    services.add(new HealthCheckService());
    GrpcServerBuilder builder = GrpcServerBuilder.port(50051);
    GrpcServer grpcServer = builder.services(services)
            .fixedThreadPool(4)
            .maxConnectionAge(5, TimeUnit.MINUTES)
            .interceptors(getInterceptors())
            .build();
    grpcServer.start();
```

 
 ## That's all. Please leave a star if this project helped you!
 