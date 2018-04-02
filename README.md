# DdosSimulator

Build a Master and Slave Bot capable of generating distributed denial of service attacks on command
from the Bot Master.

## Getting Started

Clone the repository

### Prerequisites

JAVA 8

### Installing

Open the terminal at the file directory. To compile the file,

```
make
```
## Running the tests

Bot Master (MasterBot.java) provides the code for the slaves (SlaveBot.java) which is to be commanded by the Bot Master. The master, when started, will present a command line interface to the user like that provided by the shell in Unix (i.e. present prompt ‘>’).This program support multiple commends.

Master will take the following command line argument:
-p PortNumber
This will be the port where master will listen for incoming connections from Slaves. To run MasterBot.class (server)

```
java MasterBot -p 60000
```

Slave will take two arguments:
-h IPAddress|Hostname of Master -p port where master is listening for connections. To run SlaveBot.class (client)

```
java SlaveBot -h localhost 60000
```

### Break down into end to end tests

1.-list
Will list all current slaves with the following format:
SlaveHostName IPAddress SourcePortNumber RegistrationDate

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment


## Built With

* Intellij IDEA from Jetbrain

## Contributing

personal project

## Versioning


## Authors

* **Bing Shi (https://github.com/bingshi0112)

See also the list of [contributors](https://github.com/bingshi0112/DDOSSimulator/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments



