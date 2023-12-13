#!/bin/bash
sudo service postgresql stop
docker-compose up
read -p "Press any key to exit..."
