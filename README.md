# pourrfot-server

### Dev Environment

* IntelliJ IDEA 2021.1 (Ultimate Edition)
* adopt-openjdk **11** hotspot (version 11.0.10)

### Deployment

The project will be automatically deployed (after running test and build automatically) to the Aliyun server (Lighthouse
but not ECS) I purchased by this [Github Action](./.github/workflows/gradle.yml). 

There is a Nginx (not in Docker) running on the server to back-proxy backend services. You can just access the service without port but begin with `/api`. #3
