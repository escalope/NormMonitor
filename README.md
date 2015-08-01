NormMonitor is a monitoring solution for the PHAT framework that implements a normative system. The system polls the PHAT framework to obtain current events in the simulator and arranges with JADE to receive a copy of every delivered message. 

NormMonitor requires having a PHAT project to work on. The pom.xml file of the PHAT project must include the dependence

		<dependency>
                  <groupId>net.sf.sociaal</groupId>
                  <artifactId>normmonitor</artifactId>
                  <version>1.0.0-SNAPSHOT</version>
                </dependency>

Also, the PHAT version and the SociAALML must match those of the PHAT project

		<sociaalml.version>1.0.3-SNAPSHOT</sociaalml.version>
		<phat.version>1.0.2-SNAPSHOT</phat.version>

To learn to use PHAT,please check the tutorial at http://grasia.fdi.ucm.es/sociaal


To use it, 

	git clone git@github.com:escalope/NormMonitor.git

And then, install it

	mvn clean install

NormMonitor launches JADE agents. So, before anything, it is required to launch the JADE platform. There is a script to do so in the NormMonitor. To launch it:

	sh startPlatform.sh

It is configured to be launched within a PHAT project with a

	mvn exec.java -Dexec.mainClass=sociaal.NormativeSystem




