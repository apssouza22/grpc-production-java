# gRPC for production ![Build status](https://github.com/apssouza22/grpc-production-java/actions/workflows/ci.yml/badge.svg)

This project abstracts away the details of the gRPC server and client configuration. 

Here are the main features:
- Health check service — We use the grpc_health_probe utility which allows you to query health of gRPC services that expose service their status through the gRPC Health Checking Protocol.
- Shutdown hook — The library registers a shutdown hook with the gRPC server to ensure that the application is closed gracefully on exit
- Keep alive params — Keepalives are an optional feature but it can be handy to signal how the persistence of the open connection should be kept for further messages
- Custom Server builder with some opinionated production ready settings 
- Added ability to recover the system from a Runtime exception in the request
- Added ability to add multiple ordered interceptors 
- Handy Server interceptors(Authentication, request cancelled, Audit request/response...)
- Handy Client interceptors(Timeout logs, Audit request, Default timeout...)
 
 ## Examples
 
 ```
    List<BindableService> services = new ArrayList<>();
    List<ServerInterceptor> interceptors = Factory.allDefaultInterceptors();
    GrpcServerBuilder builder = GrpcServerBuilder.port(50052);
    GrpcServer grpcServer = builder
            .withServices(services)
            .withInterceptors(interceptors)
            .withFixedThreadPool(4)
            .withMaxConnectionAge(5, TimeUnit.MINUTES)
            .withReflectionEnabled(true)
            .withHealthCheck()
            .build();
    grpcServer.start();
```

 
 ## Please leave a star if this project helped you!
 