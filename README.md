# Build instructions:

mvn install

# Execution instructions:

mvn exec:java -pl app

# New plugin creation instructions:

1. Create your plugin folder in "plugins"
2. Add you new plugin submodule in main pom.xml:

    <modules>
        <module>interfaces</module>
        <module>app</module>
        <module>plugins/myplugin</module>
        ADD IT HERE
    </modules>

3. Create your new plugin's pom.xml (check myplugin/pom.xml)
4. Remember to use plugin's package conventions:

   br/edu/ifba/inf008/plugins/<YourPluginNameInCamelCase>.java

5. Run "mvn install" and "mvn exec:java -pl app"

# MariaDB Docker

## Starting the database

docker-compose up -d

## Accessing the database:

### From host's MariaDB client:

mariadb -h 127.0.0.1 -P 3307 -u root -proot --skip-ssl bookstore

### From docker containers:

docker exec -it bookstore-db mariadb -u root -proot bookstore
