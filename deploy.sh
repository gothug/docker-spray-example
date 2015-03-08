#!/bin/sh

echo 'Deploy script'

## Local commands
echo
echo "- local git push:"
git push

## Remote commands
cdCmd='cd /root/docker-spray-example/;'

gitPullCmd='echo; echo "- remote git pull:"; git pull;'

sbtAssemblyCmd='echo; echo "- sbt assembly:"; JAVA_OPTS="-Xms50m -Xmx250m" sbt assembly;'

createSoftLinkForLatestJarCmd='ln -sf `find target/scala-*.*/ -name *.jar ! -type l | xargs ls -t | head -n1` target/scala-2.11/server.jar;'

dockerBuildCmd='echo; echo "- docker build:"; docker build -t="gothug/spray-docker" .;'

dockerStopContainerCmd='echo; echo "- docker stop container:"; docker stop moviegeek;'

dockerRemoveContainerCmd='echo; echo "- docker remove container:"; docker rm moviegeek;'

updateDbCmd='echo; echo "- update db:"; JAVA_OPTS="-Xms250m -Xmx384m" sbt "run-main mvgk.db.DBManager update";'

dockerStartContainerCmd='echo; echo "- docker start container:"; docker run -d -p 9090:8080 --link postgres:pgsql --name moviegeek gothug/spray-docker;'

ssh -i ~/.ssh/id_rsa_digitalocean root@104.131.98.252 "$cdCmd $gitPullCmd $sbtAssemblyCmd $createSoftLinkForLatestJarCmd $dockerBuildCmd $dockerStopContainerCmd $dockerRemoveContainerCmd $updateDbCmd $dockerStartContainerCmd"
