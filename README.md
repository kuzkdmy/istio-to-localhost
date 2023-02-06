MonolithMain is preconfigured to start all on local
it wire REST web server and GRPC services from different microservices  

After start health endpoint is available under
http://localhost:9000/health/readiness
and other endpoints web-api module are also available they are just require extra headers to validate
```bash
curl -v -H "x-tenant-id: 0" -H "x-user-id: 0" -H "x-request-id: 123" http://localhost:9000/api/v1/integration/metadata/destinations
```
