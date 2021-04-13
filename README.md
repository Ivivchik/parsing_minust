# parsing_scala

## Overview

Парсинг сайта [минюста](http://unro.minjust.ru/NKOForeignAgent.aspx)

## How to run

- Разархивация `tar -xvf parsing_scala.tar`
- Выполнить данную команду, где лежат разархивированные файлы `docker build --no-cache=true -t <nameofimage> -f Dockerfile . `
- После успешной сборки образа запускаем котейнер командой `docker run -v <directoryforcsvfile>:/home/output --name <nameofdocker> <nameofimage>`