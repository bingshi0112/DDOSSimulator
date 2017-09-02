all:server client

server:
	javac MasterBot.java
client: 
	javac SlaveBot.java
clean:
	rm -f *.class