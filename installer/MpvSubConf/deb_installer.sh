#!/bin/bash
sudo apt-get install mpv
sudo apt-get install openjfx
sudo cp mpvsubconf /usr/bin/mpvsubconf
sudo chmmod +x /usr/bin/mpvsubconf
sudo cp mpvSubConf.png /usr/share
sudo cp mpvSubConf.desktop /usr/share/applications
