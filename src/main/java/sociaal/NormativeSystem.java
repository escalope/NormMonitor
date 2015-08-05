/*
    Copyright (C) 2015 Jorge Gomez Sanz

    This file is part of NormMonitor a monitoring solution system for PHAT infrastructure, 
    and availabe at https://github.com/escalope/NormMonitor. It has been built over code
    from JADE framework. In particular, the JADE sniffer whose license follows. 

    The NormMonitor is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    INGENIAS Agent Framework is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with NormMonitor; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 */

/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A.

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation,
version 2.1 of the License.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
 *****************************************************************/

package sociaal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.TreeMap;

import jade.util.leap.Iterator;
import jade.util.leap.List;
import jade.util.leap.ArrayList;
import jade.util.Logger;

import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;
import java.util.Vector;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.AMSService;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.*;
import jade.domain.introspection.*;
import jade.domain.FIPAService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.ACLCodec;
import jade.lang.acl.StringACLCodec;
import jade.content.lang.sl.SLCodec;
import jade.content.AgentAction;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Done;
import jade.proto.SimpleAchieveREResponder;
import jade.proto.SimpleAchieveREInitiator;
import jade.tools.ToolAgent;
import jade.tools.sniffer.DoSnifferAction;
import jade.tools.sniffer.Message;
import jade.tools.sniffer.Sniffer;
import jade.util.ExtendedProperties;
import jade.wrapper.StaleProxyException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.io.*;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import org.apache.tools.ant.util.CollectionUtils;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;

import phat.PHATInterface;
import phat.RemotePHATInterface;
import phat.agents.AgentPHATEvent;
import phat.world.MonitorEventQueue;
import phat.world.MonitorEventQueueImp;
import phat.world.RemotePHATEvent;
import sociaal.ontology.BasicFact;
import sociaal.ontology.CurrentTime;
import sociaal.ontology.PerformPunishment;

import com.android.ddmlib.Log;

/**
 * This is the <em>Sniffer</em> agent. <br>
 * This class implements the low level part of the Sniffer, interacting with
 * Jade environment and with the sniffer GUI.<br>
 * At startup, the sniffer subscribes itself as an rma to be informed every time
 * an agent is born or dead, a container is created or deleted. <br>
 * For more information see <a href="../../../../intro.htm"
 * target="_top">Introduction to the Sniffer</a>.
 * <p>
 * A properties file while may be used to control different sniffer properties.
 * These optional properties are as follows:
 * <ul>
 * <li>preload - A list of preload descriptions seperated by a semi-colon. Each
 * description consists of an agent name match string and optional list of
 * performatives each seperated by a space. For details on the agent name match
 * string, see the method isMatch(). If there is no @ in the agent name, it
 * assumes the current HAP for it. If the performative list is not present, then
 * the sniffer will display all messages; otherwise, only those messages that
 * have a matching performative mentioned will be displayed. <br>
 * Examples:
 * 
 * <pre>
 * preload=da0;da1 inform propose
 * preload=agent?? inform
 * preload=*
 * </pre>
 * 
 * <li>clip - A list of agent name prefixes seperated by a semi-colon which will
 * be removed when showing the agent's name in the agent box. This is helpful to
 * eliminate common agent prefixes. <br>
 * Example:
 * 
 * <pre>
 * clip=com.hp.palo-alto.;helper.
 * </pre>
 * 
 * </ul>
 * The property file is looked for in the current directory, and if not found,
 * it looks in the parent directory and continues this until the file is either
 * found or there isn't a parent directory.
 * <p>
 * The original implementation processed a .inf file. For backward compatability
 * this has been preserved but its usage should be converted to use the new
 * .properties file. The format of the .inf file is each line contains an agent
 * name and optional list of performatives. <br>
 * Example:
 * 
 * <pre>
 * da0
 * da1 inform propose
 * </pre>
 * <p>
 * Notes:
 * <ol>
 * <li>If a message is one that is to be ignored, then it is dropped totally. If
 * you look at the sniffer dump of messages, it will not be there. Might want to
 * change this.
 * <li>Should develop a GUI to allow dynamically setting which messages are
 * filtered instead of forcing them to be in the properties file.
 * <li>Probably should allow one to turn on and off the display of the
 * performative name. Although, it seems pretty nice to have this information
 * and although one might consider that it clutters the display, it sure
 * provides a lot of information with it.
 * </ol>
 *
 * @author <a href="mailto:alessandro.beneventi@re.nettuno.it"> Alessandro
 *         Beneventi </a>(Developement)
 * @author Gianluca Tanca (Concept & Early Version)
 * @author Robert Kessler University of Utah (preload configuration, don't
 *         scroll agent boxes)
 * @author Martin Griss HP Labs (display additional message information)
 * @author Dick Cowan HP Labs (property handling, display full agent name when
 *         mouse over)
 * @version $Date: 2010-06-11 15:32:31 +0200 (vie, 11 jun 2010) $ $Revision:
 *          6352 $
 *
 */
public class NormativeSystem extends ToolAgent {

	public static final boolean SNIFF_ON = true;
	public static final boolean SNIFF_OFF = false;

	private Set allAgents = null;
	private Hashtable preload = null;
	private ExtendedProperties properties = null;

	private Vector<AID> agentsUnderSniff = new Vector<AID>();

	private static KieSession ksession = null;

	java.util.concurrent.ConcurrentLinkedDeque<ACLMessage> mqueue = new java.util.concurrent.ConcurrentLinkedDeque<ACLMessage>();
	java.util.concurrent.ConcurrentLinkedDeque<Event> evqueue = new java.util.concurrent.ConcurrentLinkedDeque<Event>();
	java.util.concurrent.ConcurrentLinkedDeque<RemotePHATEvent> remoteEvents = new java.util.concurrent.ConcurrentLinkedDeque<RemotePHATEvent>();

	KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();

	public static KieSession getSession() {
		return ksession;
	}

	public static long getSimTime() {

		int port = 60200;
		System.setProperty("java.rmi.server.useCodebaseOnly", "false");
		if (System.getProperty("phat.monitorport") != null) {
			port = Integer.parseInt(System.getProperty("phat.monitorport"));
		}
		try {
			Registry registry = LocateRegistry.getRegistry(port);
			RemotePHATInterface stub;
			try {
				stub = (RemotePHATInterface) registry
						.lookup(PHATInterface.SERVER_NAME);
				// after creating it, it starts delivering orders
				return stub.getSimTime().getTimeInMillis();


			} catch (RemoteException | NotBoundException e) {
				//e.printStackTrace();
				// TODO Auto-generated catch block
				// System.err.println(e.getMessage());
				return -1;
			}
		} catch (RemoteException e) {

		}
		return -1;
	}

	public static long getElapsedSimTimeSeconds() {

		int port = 60200;
		System.setProperty("java.rmi.server.useCodebaseOnly", "false");
		if (System.getProperty("phat.monitorport") != null) {
			port = Integer.parseInt(System.getProperty("phat.monitorport"));
		}
		try {
			Registry registry = LocateRegistry.getRegistry(port);
			RemotePHATInterface stub;
			try {
				stub = (RemotePHATInterface) registry
						.lookup(PHATInterface.SERVER_NAME);
				// after creating it, it starts delivering orders
				return stub.getElapsedSimTimeSeconds();


			} catch (RemoteException | NotBoundException e) {
				//e.printStackTrace();
				// TODO Auto-generated catch block
				// System.err.println(e.getMessage());
				return -1;
			}
		} catch (RemoteException e) {

		}
		return -1;
	}


	public static void pausePHAT() {

		int port = 60200;
		System.setProperty("java.rmi.server.useCodebaseOnly", "false");
		if (System.getProperty("phat.monitorport") != null) {
			port = Integer.parseInt(System.getProperty("phat.monitorport"));
		}
		try {
			Registry registry = LocateRegistry.getRegistry(port);
			RemotePHATInterface stub;
			try {
				stub = (RemotePHATInterface) registry
						.lookup(PHATInterface.SERVER_NAME);
				// after creating it, it starts delivering orders
				stub.pausePHAT();

			} catch (RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				// System.err.println(e.getMessage());

			}
		} catch (RemoteException e) {

		}

	}

	public static void resumePHAT() {

		int port = 60200;
		System.setProperty("java.rmi.server.useCodebaseOnly", "false");
		if (System.getProperty("phat.monitorport") != null) {
			port = Integer.parseInt(System.getProperty("phat.monitorport"));
		}
		try {
			Registry registry = LocateRegistry.getRegistry(port);
			RemotePHATInterface stub;
			try {
				stub = (RemotePHATInterface) registry
						.lookup(PHATInterface.SERVER_NAME);
				// after creating it, it starts delivering orders
				stub.resumePHAT();

			} catch (RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				// System.err.println(e.getMessage());

			}
		} catch (RemoteException e) {

		}

	}

	public NormativeSystem() {
		super();
		jade.lang.acl.ACLMessage mes = new ACLMessage();
		mes.setPostTimeStamp();
		System.out.println(kc.verify().getMessages().toString());

		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		String ruleFile = "target/generated/rules.drl";
		String ddrulesFile = "src/main/resources/DomainDependentRules.drl";

		KieServices kServices = KieServices.Factory.get();

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(ruleFile);

			if (new File(ruleFile).exists())
				kbuilder.add(ResourceFactory.newInputStreamResource(fis),
						ResourceType.DRL);

			kbuilder.add(ResourceFactory.newInputStreamResource(getClass().getResourceAsStream("/DomainIndependentRules.drl")),
					ResourceType.DRL);

			if (new File(ddrulesFile).exists())
				kbuilder.add(ResourceFactory.newInputStreamResource(new FileInputStream(ddrulesFile)),
						ResourceType.DRL);


			System.out.println("Loading file: " + ruleFile);
			ksession = kbuilder.newKnowledgeBase().newKieSession();
			// debug information
			//KieRuntimeLogger logger = KieServices.Factory.get().getLoggers().newConsoleLogger(ksession);
			NormPerformanceDisplay.getInstance().setShowWorkSpaceHandler(
					new java.awt.event.ActionListener() {
						// by Paul Vargas from http://stackoverflow.com/questions/17627431/auto-resizing-the-jtable-column-widths
						public void resizeColumnWidth(JTable table) {
							final TableColumnModel columnModel = table.getColumnModel();
							for (int column = 0; column < table.getColumnCount(); column++) {
								int width = 50; // Min width
								for (int row = 0; row < table.getRowCount(); row++) {
									TableCellRenderer renderer = table.getCellRenderer(row, column);
									Component comp = table.prepareRenderer(renderer, row, column);
									width = Math.max(comp.getPreferredSize().width, width);
								}
								columnModel.getColumn(column).setPreferredWidth(width);
							}
						}
						@Override
						public void actionPerformed(ActionEvent e) {
							JFrame wsdisplay = new JFrame("Workspace");
							wsdisplay.setTitle("Workspace at "+getElapsedSimTimeSeconds()+" sim time");
							wsdisplay.getContentPane().setLayout(
									new BorderLayout());

							Object[][] data=new Object[ksession.getObjects().size()][2];
							Vector<Object> ordered = new Vector<Object>();

							int y = 0;


							for (Object obj : ksession.getObjects()) {
								if (obj instanceof BasicFact)
									ordered.add((Object) obj);
								else if (obj instanceof AgentPHATEvent)
									ordered.add((Object) obj);
								else {
									data[y][0]="";
									data[y][1]=obj;
									y = y + 1;
								}
							}

							Collections.sort(ordered, new Comparator<Object>() {
								@Override
								public int compare(Object o1, Object o2) {
									return (int) (getTime(o1) - getTime(o2));
								}
							});

							for (Object obj : ordered) {

								data[y][0]="" + getTime(obj);
								data[y][1]=obj;
								y = y + 1;

							}

							DefaultTableModel dtm=new DefaultTableModel(data,new Object[]{"time","fact"});
							JTable table=new JTable(dtm);
							table.setAutoCreateRowSorter(true);
							final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(dtm);

							this.resizeColumnWidth(table);
							table.setRowSorter(sorter);
							JScrollPane scp = new JScrollPane(table);
							wsdisplay.getContentPane().add(scp,
									BorderLayout.CENTER);
							JPanel filters=new JPanel();
							wsdisplay.getContentPane().add(filters,
									BorderLayout.SOUTH);

							final JCheckBox showevents=new JCheckBox("Show events", false);
							final JCheckBox showtime=new JCheckBox("Show time", false);
							final JCheckBox shownorms=new JCheckBox("Show norms", true);
							final JCheckBox showpunish=new JCheckBox("Show Punishments", true);
							final JCheckBox showfacts=new JCheckBox("Show facts", true);
							filters.add(showevents);
							filters.add(showtime);
							filters.add(showfacts);
							filters.add(shownorms);
							filters.add(showpunish);



							final RowFilter<Object,Object> startsWithAFilter = new RowFilter<Object,Object>() {
								public boolean include(Entry<? extends Object, ? extends Object> entry) {
									if (entry.getValueCount()>=2 && entry.getValue(1)!=null){
										if (entry.getValue(1).toString().startsWith("AgentPHAT"))
											if (showevents.isSelected())
												return true;
											else 
												return false;
										if (entry.getValue(1).toString().startsWith("CurrentTime"))
											if (showtime.isSelected())
												return true;
											else 
												return false;
										if (entry.getValue(1) instanceof PunishmentNorm)
											if (shownorms.isSelected())
												return true;
											else 
												return false;
										if (entry.getValue(1) instanceof PerformPunishment)
											if (showpunish.isSelected())
												return true;
											else 
												return false;
										if (showfacts.isSelected()&& entry.getValue(1) instanceof BasicFact)
											return true;
										return false;
									} else
										return true;
								}
							};

							sorter.setRowFilter(startsWithAFilter);

							showevents.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									sorter.setRowFilter(startsWithAFilter);
								}
							});


							showtime.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									sorter.setRowFilter(startsWithAFilter);
								}
							});

							shownorms.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									sorter.setRowFilter(startsWithAFilter);
								}
							});
							showfacts.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									sorter.setRowFilter(startsWithAFilter);
								}
							});


							wsdisplay.pack();
							wsdisplay.setVisible(true);

						}

					});




			Thread listener = new Thread() {

				private long lastSimtime;

				public void run() {
					while (true) {
						if (!mqueue.isEmpty()) {
							while (!mqueue.isEmpty()) {
								jade.lang.acl.ACLMessage mes = mqueue.poll();
								mes.setPostTimeStamp();
								System.out.println("---->message:" + mes);
								ksession.insert(mes);
							}
							//ksession.fireAllRules();
							//printWorkingMemory(ksession);
						}
						if (!evqueue.isEmpty()) {
							Event ev = evqueue.poll();
							//System.out.println("---->ams event:" + ev);
							//printWorkingMemory(ksession);
						}
						if (!remoteEvents.isEmpty()) {
							while (!remoteEvents.isEmpty()) {
								RemotePHATEvent ev = remoteEvents.poll();
								//System.out.println("---->PHAT event:" + ev);
								ksession.insert(ev);
							}
							//ksession.fireAllRules();

							//printWorkingMemory(ksession);

						}
						try {
							Thread.currentThread().sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						long currentSimtime = getElapsedSimTimeSeconds();
						if (currentSimtime != -1 && currentSimtime!=lastSimtime) {
							ksession.insert(new CurrentTime(currentSimtime));

							while (ksession.fireAllRules()>0)
								try {
									Thread.currentThread().sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							lastSimtime=currentSimtime;
							evaluatePunishmentRules(ksession);
						}
						while(NormPerformanceDisplay.getInstance().isPaused()){
							try {
								Thread.currentThread().sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						};

					}

				}

				private void evaluatePunishmentRules(KieSession ksession) {
					Vector<FactHandle> handlers = new Vector<FactHandle>();
					for (Object obj : ksession.getObjects())
						if (obj instanceof PunishmentNorm) {
							PunishmentNorm pn = (PunishmentNorm) obj;
							if (pn.shouldPunish(getElapsedSimTimeSeconds())) {
								FactHandle handle = ksession
										.insert(new PerformPunishment(pn,getElapsedSimTimeSeconds()));
								handlers.add(handle);

							}
						}

					ksession.fireAllRules();
					while(NormPerformanceDisplay.getInstance().isPaused()){
						try {
							Thread.currentThread().sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};

					for (FactHandle fh : handlers)
						ksession.delete(fh);

				}
			};
			listener.start();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private static long getTime(Object obj) {
		if (obj instanceof BasicFact)
			return ((BasicFact) obj).getTime();
		else if (obj instanceof AgentPHATEvent)
			return ((AgentPHATEvent) obj).getElapsedTime();
		return 0;
	}

	private static void printWorkingMemory(KieSession ksession) {
		System.out.println("--------BEGIN Working Memory-------------");
		Vector<Object> ordered = new Vector<Object>();
		for (Object obj : ksession.getObjects()) {
			if (obj instanceof BasicFact)
				ordered.add((Object) obj);
			else if (obj instanceof AgentPHATEvent)
				ordered.add((Object) obj);
			else
				System.out.println(obj);
		}

		Collections.sort(ordered, new Comparator<Object>() {

			@Override
			public int compare(Object o1, Object o2) {

				return (int) (getTime(o1) - getTime(o2));
			}

		});
		for (Object obj : ordered) {
			System.out.println(getTime(obj) + ":" + obj);
		}

		System.out.println("--------END Working Memory-------------");
	}

	// Sends requests to the AMS
	private class AMSClientBehaviour extends SimpleAchieveREInitiator {

		private String actionName;

		public AMSClientBehaviour(String an, ACLMessage request) {
			super(NormativeSystem.this, request);
			actionName = an;
		}

		protected void handleNotUnderstood(ACLMessage reply) {
			System.err.println(reply);
			// myGUI.showError("NOT-UNDERSTOOD received during " + actionName);
		}

		protected void handleRefuse(ACLMessage reply) {
			System.err.println(reply);
			// myGUI.showError("REFUSE received during " + actionName);
		}

		protected void handleAgree(ACLMessage reply) {
			System.err.println(reply);
			/*
			 * if(logger.isLoggable(Logger.FINE))
			 * logger.log(Logger.FINE,"AGREE received");
			 */
		}

		protected void handleFailure(ACLMessage reply) {
			System.err.println(reply);
			// myGUI.showError("FAILURE received during " + actionName);
		}

		protected void handleInform(ACLMessage reply) {
			System.err.println(reply);
			/*
			 * if(logger.isLoggable(Logger.FINE))
			 * logger.log(Logger.FINE,"INFORM received");
			 */
		}

	} // End of AMSClientBehaviour class

	private class SniffListenerBehaviour extends CyclicBehaviour {

		private MessageTemplate listenSniffTemplate;

		SniffListenerBehaviour() {
			listenSniffTemplate = MessageTemplate
					.MatchOntology("JADE-Introspection");
		}

		public void action() {

			ACLMessage current = receive(listenSniffTemplate);
			try {
				if (current != null
						&& getContentManager().extractContent(current) instanceof Occurred) {

					Occurred o = (Occurred) getContentManager().extractContent(
							current);
					EventRecord er = o.getWhat();
					Event ev = er.getWhat();
					String content = null;
					Envelope env = null;
					AID unicastReceiver = null;
					if (ev instanceof SentMessage) {
						content = ((SentMessage) ev).getMessage().getPayload();
						env = ((SentMessage) ev).getMessage().getEnvelope();
						unicastReceiver = ((SentMessage) ev).getReceiver();
					} else if (ev instanceof PostedMessage) {
						content = ((PostedMessage) ev).getMessage()
								.getPayload();
						env = ((PostedMessage) ev).getMessage().getEnvelope();
						unicastReceiver = ((PostedMessage) ev).getReceiver();
						AID sender = ((PostedMessage) ev).getSender();
						// If the sender is currently under sniff, then the
						// message was already
						// displayed when the 'sent-message' event occurred -->
						// just skip this message.
						if (agentsUnderSniff.contains(sender)) {
							return;
						}
					} else {
						return;
					}

					ACLCodec codec = new StringACLCodec();
					String charset = null;
					if ((env == null)
							|| ((charset = env.getPayloadEncoding()) == null)) {
						charset = ACLCodec.DEFAULT_CHARSET;
					}
					ACLMessage tmp = codec.decode(content.getBytes(charset),
							charset);
					tmp.setEnvelope(env);
					Message msg = new Message(tmp, unicastReceiver);

					/*
					 * If this is a 'posted-message' event and the sender is //
					 * currently under sniff, then the message was already //
					 * displayed when the 'sent-message' event occurred. In that
					 * // case, we simply skip this message. if(ev instanceof
					 * PostedMessage) { Agent a = new Agent(msg.getSender());
					 * if(agentsUnderSniff.contains(a)) return; }
					 */

					// If the message that we just got is one that should be
					// filtered out
					// then drop it. WARNING - this means that the log file
					// that the sniffer might dump does not include the
					// message!!!!
					boolean filters[];
					String agentName = msg.getSender().getName();
					String key = preloadContains(agentName);
					if (key != null) {
						filters = (boolean[]) preload.get(key);
						if ((msg.getPerformative() >= 0)
								&& filters[msg.getPerformative()]) {
							mqueue.add(msg);
						}
					} else {
						mqueue.add(msg);
					}

				} else
					block();
			} catch (Throwable e) {
				// System.out.println("Serious problem Occurred");
				Log.logAndDisplay(Log.LogLevel.ERROR, "normsys",
						"An error occurred parsing the incoming message.\n"
								+ "          The message was lost.");
				if (logger.isLoggable(Logger.WARNING))
					logger.log(Logger.WARNING,
							"The sniffer lost the following message because of a parsing error:"
									+ current);
				e.printStackTrace();
			}
		}

	} // End of SniffListenerBehaviour

	//
	//
	// * Search keys in preload for a string which matches (using isMatch
	// method)
	// * the agent name.
	// * @param agentName The agent name.
	// * @return String The key which matched.
	//
	protected String preloadContains(String agentName) {
		for (Enumeration enumeration = preload.keys(); enumeration
				.hasMoreElements();) {
			String key = (String) enumeration.nextElement();
			if (isMatch(key, agentName)) {
				return key;
			}
		}
		return null;
	}

	/**
	 * Given two strings determine if they match. We iterate over the match
	 * expression string from left to right as follows:
	 * <ol>
	 * <li>If we encounter a '*' in the expression token they match.
	 * <li>If there aren't any more characters in the subject string token they
	 * don't match.
	 * <li>If we encounter a '?' in the expression token we ignore the subject
	 * string's character and move on to the next iteration.
	 * <li>If the character in the expression token isn't equal to the character
	 * in the subject string they don't match.
	 * </ol>
	 * If we complete the iteration they match only if there are the same number
	 * of characters in both strings.
	 * 
	 * @param aMatchExpression
	 *            An expression string with special significance to '?' and '*'.
	 * @param aString
	 *            The subject string.
	 * @return True if they match, false otherwise.
	 */
	protected boolean isMatch(String aMatchExpression, String aString) {
		int expressionLength = aMatchExpression.length();
		for (int i = 0; i < expressionLength; i++) {
			char expChar = aMatchExpression.charAt(i);
			if (expChar == '*')
				return true; // * matches the remainder of anything
			if (i == aString.length())
				return false; // if we run out of characters they don't match
			if (expChar == '?')
				continue; // ? matches any single character so keep going
			if (expChar != aString.charAt(i))
				return false; // if non wild then must be exactly equal
		}
		return (expressionLength == aString.length());
	}

	private SequentialBehaviour AMSSubscribe = new SequentialBehaviour();

	/**
	 * @serial
	 */

	/**
	 * @serial
	 */
	private String myContainerName;

	class SnifferAMSListenerBehaviour extends AMSListenerBehaviour {

		protected void installHandlers(Map handlersTable) {

			// Fill the event handler table.

			handlersTable.put(IntrospectionVocabulary.META_RESETEVENTS,
					new EventHandler() {
				public void handle(Event ev) {
					ResetEvents re = (ResetEvents) ev;
					allAgents.clear();
					evqueue.clear();
				}
			});

			handlersTable.put(IntrospectionVocabulary.ADDEDCONTAINER,
					new EventHandler() {
				public void handle(Event ev) {
					evqueue.add(ev);
				}
			});

			handlersTable.put(IntrospectionVocabulary.REMOVEDCONTAINER,
					new EventHandler() {
				public void handle(Event ev) {
					evqueue.add(ev);
				}
			});

			handlersTable.put(IntrospectionVocabulary.BORNAGENT,
					new EventHandler() {
				public void handle(Event ev) {
					BornAgent ba = (BornAgent) ev;
					ContainerID cid = ba.getWhere();
					String container = cid.getName();
					AID agent = ba.getAgent();
					allAgents.add(agent);
					if (agent.equals(getAID()))
						myContainerName = container;
					// Here we check to see if the agent is one that we
					// automatically will
					// start sniffing. If so, we invoke
					// DoSnifferAction's doSniff and start
					// the sniffing process.
					// Avoid sniffing myself to avoid infinite recursion
					if (!agent.equals(getAID())
							&& !agent.getLocalName().toLowerCase()
							.startsWith("sniffer")
							&& !agent.getLocalName().toLowerCase()
							.startsWith("df")
							&& !agent.getLocalName().toLowerCase()
							.startsWith("rma")
							&& !agent.getLocalName().toLowerCase()
							.startsWith("ams")
							&& !agent
							.getLocalName()
							.toLowerCase()
							.startsWith(
									NormativeSystem.this
									.getAID()
									.getLocalName()
									.toLowerCase())) {
						// if (preloadContains(agent.getName()) != null)
						// {
						Vector<AID> sniffedAgents = new Vector<AID>();
						sniffedAgents.add(agent);
						sniffMsg(sniffedAgents, Sniffer.SNIFF_ON); // Sniff
						// the
						// Agents
						sniffedAgents.clear();
						agentsUnderSniff.add(agent);
						evqueue.add(ev);
						// }
					}

				}
			});

			handlersTable.put(IntrospectionVocabulary.DEADAGENT,
					new EventHandler() {
				public void handle(Event ev) {
					evqueue.add(ev);

				}
			});

			handlersTable.put(IntrospectionVocabulary.MOVEDAGENT,
					new EventHandler() {
				public void handle(Event ev) {
					evqueue.add(ev);
				}
			});

		}
	} // END of inner class SnifferAMSListenerBehaviour

	/**
	 * ACLMessages for subscription and unsubscription as <em>rma</em> are
	 * created and corresponding behaviours are set up.
	 */
	public void toolSetup() {

		properties = new ExtendedProperties();

		/*
		 * preload agents from argument array if arguments present, otherwise
		 * load sniffer.properties file.
		 */
		Object[] arguments = getArguments();
		if ((arguments != null) && (arguments.length > 0)) {
			String s = "";
			for (int i = 0; i < arguments.length; ++i) {
				s += arguments[i].toString() + ' ';
			}
			properties.setProperty("preload", s);

		} else {
			String fileName = locateFile("sniffer.properties");
			if (fileName != null) {
				try {
					properties.load(new FileInputStream(fileName));
				} catch (IOException ioe) {
					// ignore - Properties not processed
				}
			} else {
				// This is only being done for backward compatability.
				fileName = locateFile("sniffer.inf");
				if (fileName != null) {
					loadSnifferConfigurationFile(fileName, properties);
				}
			}
		}

		allAgents = new HashSet();
		preload = new Hashtable();

		String preloadDescriptions = properties.getProperty("preload", null);

		if (preloadDescriptions != null) {
			StringTokenizer parser = new StringTokenizer(preloadDescriptions,
					";");
			while (parser.hasMoreElements()) {
				parsePreloadDescription(parser.nextToken());
			}
		}

		addBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
				AMSAgentDescription[] agents = null;

				try {
					SearchConstraints c = new SearchConstraints();
					c.setMaxResults(new Long(-1));
					agents = AMSService.search(NormativeSystem.this,
							new AMSAgentDescription(), c);
					Vector<AID> agentsToSniff = new Vector<AID>();
					for (AMSAgentDescription agentDesc : agents) {
						AID agent = agentDesc.getName();
						if (!agent.getLocalName().toLowerCase()
								.startsWith("sniffer")
								&& !agent.getLocalName().toLowerCase()
								.startsWith("df")
								&& !agent.getLocalName().toLowerCase()
								.startsWith("rma")
								&& !agent.getLocalName().toLowerCase()
								.startsWith("ams")
								&& !agent.getLocalName().equalsIgnoreCase(
										NormativeSystem.this.getAID()
										.getLocalName())) {
							agentsToSniff.add(agent);

						}
					}

					sniffMsg(agentsToSniff, true);
					agentsUnderSniff.addAll(agentsToSniff);
					allAgents.addAll(agentsToSniff);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// #DOTNET_EXCLUDE_BEGIN
		// Send 'subscribe' message to the AMS
		AMSSubscribe.addSubBehaviour(new SenderBehaviour(this, getSubscribe()));

		// Handle incoming 'inform' messages
		AMSSubscribe.addSubBehaviour(new SnifferAMSListenerBehaviour());

		// Handle incoming REQUEST to start/stop sniffing agents
		addBehaviour(new RequestListenerBehaviour());

		// Schedule Behaviours for execution
		addBehaviour(AMSSubscribe);
		ParallelBehaviour pb = new ParallelBehaviour();
		pb.addSubBehaviour(new SniffListenerBehaviour());
		pb.addSubBehaviour(new CyclicBehaviour() {
			Registry registry = null;

			@Override
			public void action() {
				int port = 60200;
				System.setProperty("java.rmi.server.useCodebaseOnly", "false");
				if (System.getProperty("phat.monitorport") != null) {
					port = Integer.parseInt(System
							.getProperty("phat.monitorport"));
				}
				try {
					registry = LocateRegistry.getRegistry(port);
					MonitorEventQueue stub;
					try {
						stub = (MonitorEventQueue) registry
								.lookup(MonitorEventQueue.DefaultName);
						// after creating it, it starts delivering orders
						Vector<RemotePHATEvent> events = stub
								.retrieveAllEvents();
						remoteEvents.addAll(events);

					} catch (RemoteException | NotBoundException e) {
						// TODO Auto-generated catch block
						// System.err.println(e.getMessage());
						this.block(1000);
					}
				} catch (RemoteException e) {

				}

			}
		});
		addBehaviour(pb);

		// Show Graphical User Interface
		/*
		 * myGUI = new MainWindow(this, properties); myGUI.ShowCorrect();
		 */
		// #DOTNET_EXCLUDE_END

		/*
		 * #DOTNET_INCLUDE_BEGIN //Create GUI System.Threading.ThreadStart
		 * tStart = new System.Threading.ThreadStart( createUI );
		 * System.Threading.Thread t = new System.Threading.Thread( tStart );
		 * t.Start(); #DOTNET_INCLUDE_END
		 */
	}

	/*
	 * #DOTNET_INCLUDE_BEGIN private void createUI() { // Show Graphical User
	 * Interface myGUI = new MainWindow(this, properties); myGUI.ShowCorrect();
	 * System.Windows.Forms.Application.Run(); }
	 * 
	 * protected void startBehaviours() { // Send 'subscribe' message to the AMS
	 * AMSSubscribe.addSubBehaviour(new SenderBehaviour(this, getSubscribe()));
	 * 
	 * // Handle incoming 'inform' messages AMSSubscribe.addSubBehaviour(new
	 * SnifferAMSListenerBehaviour());
	 * 
	 * // Handle incoming REQUEST to start/stop sniffing agents addBehaviour(new
	 * RequestListenerBehaviour());
	 * 
	 * // Schedule Behaviours for execution addBehaviour(AMSSubscribe);
	 * addBehaviour(new SniffListenerBehaviour()); } #DOTNET_INCLUDE_END
	 */

	/*
	 * private void addAgent(AID id) { ActionProcessor ap = myGUI.actPro;
	 * DoSnifferAction sa =
	 * (DoSnifferAction)ap.actions.get(ap.DO_SNIFFER_ACTION);
	 * sa.doSniff(id.getName()); }
	 * 
	 * private void removeAgent(AID id) { ActionProcessor ap = myGUI.actPro;
	 * DoNotSnifferAction nsa =
	 * (DoNotSnifferAction)ap.actions.get(ap.DO_NOT_SNIFFER_ACTION);
	 * nsa.doNotSniff(id.getName()); }
	 */

	/**
	 * Private function to read configuration file containing names of agents to
	 * be preloaded. Also supports message filtering based on performatives.
	 * Each line of the file lists an agent, optionally followed by a set of
	 * messages to sniff. If there are no tokens, then we assume that means to
	 * sniff all. The tokens are the name of the performative type such as
	 * INFORM, QUERY, etc.
	 * 
	 * @deprecated Use sniffer.properties file instead.
	 */
	private void loadSnifferConfigurationFile(String aFileName,
			ExtendedProperties theProperties) {
		StringBuffer sb = new StringBuffer();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(aFileName));
			boolean eof = false;
			while (!eof) {
				String line = in.readLine();
				eof = (line == null);
				if (!eof) {
					line = line.trim();
					if (line.length() > 0) {
						sb.append(line);
						sb.append(";");
					}
				}
			}
		} catch (Exception e) {
			// ignore
		}
		if (in != null) {
			try {
				in.close();
			} catch (IOException ee) {
				// ignore
			}
		}
		if (sb.length() > 0) {
			theProperties.setProperty("preload", sb.toString());
		}
	}

	private String locateFile(String aFileName) {
		try {
			String path = (new File(".")).getAbsolutePath();
			while (path != null) {
				path = path.replace('\\', '/');
				if (path.endsWith(".")) {
					path = path.substring(0, path.length() - 1); // drop dot
				}

				if (path.endsWith("/")) {
					path = path.substring(0, path.length() - 1); // drop last
					// separator
				}
				File dir = new File(path);
				File theFile = new File(dir, aFileName);
				if (theFile.exists()) {
					return theFile.getCanonicalPath();
				} else {
					path = dir.getParent(); // reduce the path by one
				}
			}
		} catch (Exception any) {
			// ignore
		}
		return null;
	}

	private void parsePreloadDescription(String aDescription) {
		StringTokenizer st = new StringTokenizer(aDescription);
		String name = st.nextToken();
		if (!name.endsWith("*")) {
			int atPos = name.lastIndexOf('@');
			if (atPos == -1) {
				name = name + "@" + getHap();
			}
		}

		int performativeCount = ACLMessage.getAllPerformativeNames().length;
		boolean[] filter = new boolean[performativeCount];
		boolean initVal = (st.hasMoreTokens() ? false : true);
		for (int i = 0; i < performativeCount; i++) {
			filter[i] = initVal;
		}
		while (st.hasMoreTokens()) {
			int perfIndex = ACLMessage.getInteger(st.nextToken());
			if (perfIndex != -1) {
				filter[perfIndex] = true;
			}
		}
		preload.put(name, filter);
	}

	/**
	 * Cleanup during agent shutdown. This method cleans things up when
	 * <em>Sniffer</em> agent is destroyed, disconnecting from <em>AMS</em>
	 * agent and closing down the Sniffer administration <em>GUI</em>. Currently
	 * sniffed agents are also unsniffed to avoid errors.
	 */
	protected void toolTakeDown() {

		Vector<AID> l = (Vector<AID>) (agentsUnderSniff.clone());
		ACLMessage request = getSniffMsg(l, SNIFF_OFF);

		// Start a FIPARequestProtocol to sniffOff all the agents since
		// the sniffer is shutting down
		try {
			if (request != null)
				FIPAService.doFipaRequestClient(this, request);
		} catch (jade.domain.FIPAException e) {
			// When the AMS replies the tool notifier is no longer registered.
			// But we don't care as we are exiting
			// System.out.println(e.getMessage());
		}

		// Now we unsubscribe from the rma list
		send(getCancel());
		// myGUI.setVisible(false); Not needed. Can cause thread deadlock.

	}

	/**
	 * This method adds an AMSClientBehaviour that performs a request to the AMS
	 * for sniffing/unsniffing list of agents.
	 **/
	public void sniffMsg(Vector<AID> agents, boolean onFlag) {
		ACLMessage request = getSniffMsg(agents, onFlag);
		if (request != null)
			addBehaviour(new AMSClientBehaviour((onFlag ? "SniffAgentOn"
					: "SniffAgentOff"), request));

	}

	/**
	 * Creates the ACLMessage to be sent to the <em>Ams</em> with the list of
	 * the agents to be sniffed/unsniffed. The internal list of sniffed agents
	 * is also updated.
	 *
	 * @param agentVect
	 *            vector containing TreeData item representing the agents
	 * @param onFlag
	 *            can be:
	 *            <ul>
	 *            <li>Sniffer.SNIFF_ON to activate sniffer on an agent/group
	 *            <li>Sniffer.SNIFF_OFF to deactivate sniffer on an agent/group
	 *            </ul>
	 */
	public ACLMessage getSniffMsg(Vector<AID> agents, boolean onFlag) {

		java.util.Iterator<AID> it = agents.iterator();

		if (onFlag) {
			SniffOn so = new SniffOn();
			so.setSniffer(getAID());
			boolean empty = true;
			while (it.hasNext()) {
				AID a = (AID) it.next();
				if (!agentsUnderSniff.contains(a)) {
					agentsUnderSniff.add(a);
					so.addSniffedAgents(a);
					empty = false;
				}
			}
			if (!empty) {
				try {
					Action a = new Action();
					a.setActor(getAMS());
					a.setAction(so);

					ACLMessage requestMsg = getRequest();
					requestMsg.setOntology(JADEManagementOntology.NAME);
					getContentManager().fillContent(requestMsg, a);
					return requestMsg;
				} catch (Exception fe) {
					fe.printStackTrace();
				}
			}
		}

		else {
			SniffOff so = new SniffOff();
			so.setSniffer(getAID());
			boolean empty = true;
			while (it.hasNext()) {
				AID agentID = (AID) it.next();
				// agentID.setName(a.getName() + '@' + getHap());
				if (agentsUnderSniff.contains(agentID)) {
					agentsUnderSniff.remove(agentID);
					so.addSniffedAgents(agentID);
					empty = false;
				}
			}
			if (!empty) {
				try {
					Action a = new Action();
					a.setActor(getAMS());
					a.setAction(so);

					ACLMessage requestMsg = getRequest();
					requestMsg.setOntology(JADEManagementOntology.NAME);
					getContentManager().fillContent(requestMsg, a);
					requestMsg.setReplyWith(getName() + (new Date().getTime()));
					return requestMsg;
				} catch (Exception fe) {
					fe.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * Inner class RequestListenerBehaviour. This behaviour serves requests to
	 * start sniffing agents. If an agent does not exist it is put into the
	 * preload table so that it will be sniffed as soon as it starts.
	 */
	private class RequestListenerBehaviour extends SimpleAchieveREResponder {
		private Action requestAction;
		private AgentAction aa;

		RequestListenerBehaviour() {
			// We serve REQUEST messages refering to the JADE Management
			// Ontology
			super(NormativeSystem.this, MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchOntology(JADEManagementOntology.NAME)));
		}

		protected ACLMessage prepareResponse(ACLMessage request) {
			ACLMessage response = request.createReply();
			try {
				requestAction = (Action) getContentManager().extractContent(
						request);
				aa = (AgentAction) requestAction.getAction();
				if (aa instanceof SniffOn || aa instanceof SniffOff) {
					if (getAID().equals(requestAction.getActor())) {
						response.setPerformative(ACLMessage.AGREE);
						response.setContent(request.getContent());
					} else {
						response.setPerformative(ACLMessage.REFUSE);
						response.setContent("((unrecognised-parameter-value actor "
								+ requestAction.getActor() + "))");
					}
				} else {
					response.setPerformative(ACLMessage.REFUSE);
					response.setContent("((unsupported-act "
							+ aa.getClass().getName() + "))");
				}
			} catch (Exception e) {
				e.printStackTrace();
				response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			}
			return response;
		}

		protected ACLMessage prepareResultNotification(ACLMessage request,
				ACLMessage response) {
			if (aa instanceof SniffOn) {
				// SNIFF ON
				SniffOn requestSniffOn = (SniffOn) aa;
				// Start sniffing existing agents.
				// Put non existing agents in the preload map. We will start
				// sniffing them as soon as they start.
				List agentsToSniff = requestSniffOn.getCloneOfSniffedAgents();
				for (int i = 0; i < agentsToSniff.size(); i++) {
					AID aid = (AID) agentsToSniff.get(i);
					if (allAgents.contains(aid)) {
						System.out.println("recibido agente");
						// addAgent(aid);
					} else {
						// not alive -> put it into preload
						int performativeCount = ACLMessage
								.getAllPerformativeNames().length;
						boolean[] filter = new boolean[performativeCount];
						for (int j = 0; j < performativeCount; j++) {
							filter[j] = true;
						}
						preload.put(aid.getName(), filter);
					}
				}
			} else {
				// SNIFF OFF
				SniffOff requestSniffOff = (SniffOff) aa;
				List agentsToSniff = requestSniffOff.getCloneOfSniffedAgents();
				for (int i = 0; i < agentsToSniff.size(); i++) {
					AID aid = (AID) agentsToSniff.get(i);
					// removeAgent(aid);
				}
			}

			// Send back the notification
			ACLMessage result = request.createReply();
			result.setPerformative(ACLMessage.INFORM);
			Done d = new Done(requestAction);
			try {
				myAgent.getContentManager().fillContent(result, d);
			} catch (Exception e) {
				// Should never happen
				e.printStackTrace();
			}
			return result;
		}
	} // END of inner class RequestListenerBehaviour

	public static void main(String args[]) throws IOException,
	StaleProxyException {
		resumePHAT(); // to prevent a previous resume state

		// Get a hold on JADE runtime
		jade.core.Runtime rt = jade.core.Runtime.instance();

		// Exit the JVM when there are no more containers around
		rt.setCloseVM(true);

		// Create a default profile
		Profile p = new ProfileImpl();
		p.setParameter("preload", "a*");
		// p.setParameter(Profile.MAIN_PORT, "60000");

		if (new File("target/jade").exists()
				&& new File("target/jade").isDirectory())
			p.setParameter(Profile.FILE_DIR, "target/jade/");
		else {
			// from
			// http://stackoverflow.com/questions/617414/create-a-temporary-directory-in-java
			final File temp;
			temp = File
					.createTempFile("jade", Long.toString(System.nanoTime()));

			if (!(temp.delete())) {
				throw new IOException("Could not delete temp file: "
						+ temp.getAbsolutePath());
			}

			if (!(temp.mkdir())) {
				throw new IOException("Could not create temp directory: "
						+ temp.getAbsolutePath());
			}
			p.setParameter(Profile.FILE_DIR, temp.getAbsolutePath() + "/");
		}

		// Create a new non-main container, connecting to the default
		// main container (i.e. on this host, port 1099)
		final jade.wrapper.AgentContainer ac = rt.createAgentContainer(p);
		{
			// Create a new agent
			final jade.wrapper.AgentController agcSimpleAuxiliaryReviewer = ac
					.createNewAgent("NormativeSystem",
							"sociaal.NormativeSystem", new Object[0]);
			new Thread() {
				public void run() {
					try {
						System.out.println("Starting up NormativeSystem...");
						agcSimpleAuxiliaryReviewer.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();

		}

	}
}
