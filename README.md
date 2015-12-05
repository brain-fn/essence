# Essence


## Deploy

    lein uberjar
    scp target/essence-standalone.jat user@server:/opt/essence/
    ssh user@server
    restart essence
