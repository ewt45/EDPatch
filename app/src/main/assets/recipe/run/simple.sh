#!/bin/bash

. /opt/recipe/util/progress.sh

if [ "$(locale -a | grep $LC_ALL)" != "$LC_ALL" ]; then
    progress "-1" "Generating locale..."
    locale-gen --no-archive $LC_ALL
fi

progress "-1" "Launching application..."

echo 测试输出PATH=$PATH
export PATH
export TERM

ed=/opt/ed

if [ ! -f $ed/ed.conf ]; then
mkdir -p $ed
cp /opt/recipe/ed.conf $ed/ed.conf
fi


. $ed/ed.conf



if [ "${pulse}" = "1" ]; then
export PULSE_SERVER="tcp:127.0.0.1:4713"
fi

if [ "${hud}" = "1" ]; then
export GALLIUM_HUD="simple,fps"
fi


progress "-1" "Pulse=${pulse}/Hud=${hud}"

eval "$@"
