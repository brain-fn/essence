# Essence


## Deploy

    lein uberjar

    # and while it builds login to a server
    cd /opt/essence
    # make a backup
    cp essence-standalone.jar essence-standalone-`date +"%Y-%m-%d-%H:%M:%S"`.jar

    # when build is done, stop server
    stop essence

    # copy new jar to the server
    scp target/essence-standalone.jar user@server:/opt/essence/

    # start service again
    start essence
