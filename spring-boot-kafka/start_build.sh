#!/bin/bash
sudo service postgresql stop
docker-compose up -d --build
read -p "Press any key to exit..."
