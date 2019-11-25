The application will need 3 containers to operate: a redis server, a front end server and a backend server. Let name them: hey-redis, hey-frontend, hey-backend. There are 3 ways to start up this application via Docker.

#### 1. Via Docker command directly

###### Redis:
- At any place, just run

``` 
docker run -v hey-redis-data:/data --name hey-redis -d redis  --appendonly yes
```

###### Back end:
- Travel to the hey-backend folder

```
docker build -t hey-backend:1.0 .
docker run --rm -d -p 8080:8080 -p 8090:8090 -v hey-backend-log:/usr/src/app/log -e "env=production" --link hey-redis:redis --name hey-backend-app  hey-backend:1.0
```

###### Front end:
- Travel to the hey-frontend folder

```
docker build -t hey-frontend:1.0 .
docker run --rm -d -v ${PWD}:/usr/src/app -p 3000:3000 --name hey-frontend-app hey-frontend:1.0
```

#### 2. Via Docker Compose
- This is the recommendation choice to start this application. Everything has been summed in the docker-compose.yaml file. You just only need to travel to the root folder of the project then run 

```
docker-compose up --build
```
But... sadly, currently Vertx is having a bug with the DNS lookup of Netty, and it will make our application cannot lookup the redis server. In waiting for this bug to be solved, we have implement an alternative way in 3.

#### 3. Via Shell Script
- For more convenient, a shell script file that contain all the needed script to start the application has also been placed in the root folder. Travel to the root folder then

```
sh start-containers.sh
```