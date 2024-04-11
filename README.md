# RUN ME
```bash
docker-compose -f docker/docker-compose.yml build --parallel
```

You can also run in attached mode, but you'll get the logs of all containers at once which will be a total mess.
I recommend to run the network in detached mode, then create two terminals to display the logs of the data pipe (`publisher => transit-1 => transit-2`) on the left, and the MQTT client outputs on the right
```bash
docker-compose -f docker/docker-compose.yml up -d
docker-compose -f docker/docker-compose.yml logs --follow transit-2 transit-1 publisher
```

Then, on another terminal :
```bash
docker-compose -f docker/docker-compose.yml logs --follow mosquitto-client
```

here's the network graph:

![Alvarium-simple-network.svg](doc/alvarium-simple-network.svg)