#!/bin/bash

# Vérifier si nous sommes sur WSL
if grep -qi Microsoft /proc/sys/kernel/osrelease; then
    # Si nous sommes sur WSL, utiliser wt.exe
    wt.exe --window new --profile "Ubuntu-22.04" --tabColor "#0F0" --title "Tracker" --startingDirectory . bash -ic "make tracker"
    wt.exe --window new --profile "Ubuntu-22.04" --title "Peer 5000" --startingDirectory . bash -ic "make peer PORT=5000 DEBUG=1"
    wt.exe --window new --profile "Ubuntu-22.04" --title "Peer 5001" --startingDirectory . bash -ic "make peer PORT=5001 DEBUG=1"
    wt.exe --window new --profile "Ubuntu-22.04" --title "Peer 5002" --startingDirectory . bash -ic "make peer PORT=5002 DEBUG=1"
else
    # Sinon, utiliser gnome-terminal
    gnome-terminal -- make peer PORT=5000
    gnome-terminal -- make peer PORT=5001
    gnome-terminal -- make peer PORT=5002

fi
