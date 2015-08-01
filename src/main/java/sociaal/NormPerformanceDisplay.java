/*
    Copyright (C) 2015 Jorge Gomez Sanz

    This file is part of NormMonitor a monitoring solution system for PHAT infrastructure, 
    and availabe at https://github.com/escalope/NormMonitor. 

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
package sociaal;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.kie.api.builder.model.ListenerModel;

import sociaal.ontology.ActionPerformed;

public class NormPerformanceDisplay {

	private class NormPerformanceData {
		private int violationsCount;
		private Vector<String> violationTime;
		private Vector<String> violationDuration;
		String normName;

		public NormPerformanceData(String name){
			this.normName=name;
		}

		public void increaseViolations() {
			violationsCount++;
			violationTime.add(new Date().toString());
		}

		public String getName() {
			// TODO Auto-generated method stub
			return normName;
		}
	}

	private class PartnerData {
		String name;
		Vector<NormPerformanceData> normData=new Vector<NormPerformanceData>();
		private Vector<ListDataListener> ldl= new Vector<ListDataListener>();

		public PartnerData(String name){
			this.name=name;
		}

		private NormPerformanceData getNorm(String name){
			for (NormPerformanceData norm:normData)
				if (norm.getName().equals(name))
					return norm;
			return null;
		}

		private int getNormIndex(String name){
			for (int k=0;k<normData.size();k++)
				if (normData.elementAt(k).getName().equals(name))
					return k;
			return -1;
		}

		private void notifyListenersNewElement(int nindex){
			for (ListDataListener lis:ldl){
				lis.intervalAdded(new ListDataEvent(null,ListDataEvent.INTERVAL_ADDED,nindex,nindex));
			}
		}

		private void notifyListenersElChangedt(int nindex) {
			for (ListDataListener lis:ldl){
				lis.contentsChanged(new ListDataEvent(null,ListDataEvent.CONTENTS_CHANGED,nindex,nindex));
			}
		}

		public void normViolated(String normName){
			NormPerformanceData npd=null;
			if (getNorm(normName)==null){
				npd=new NormPerformanceData(normName);
				npd.increaseViolations();
				normData.add(npd);
				notifyListenersNewElement(normData.size());
			}
			else {
				npd=getNorm(normName);
				npd.increaseViolations();
				notifyListenersElChangedt(normData.size());
			}
		}



		public ListModel<String> getNormViolationMode() {

			ListModel<String> lm=new ListModel<String>(){
				@Override
				public int getSize() {
					// TODO Auto-generated method stub
					return normData.size();
				}

				@Override
				public String getElementAt(int index) {
					// TODO Auto-generated method stub
					return normData.elementAt(index).toString();
				}

				@Override
				public void addListDataListener(ListDataListener l) {
					ldl.add(l);
				}

				@Override
				public void removeListDataListener(ListDataListener l) {
					ldl.remove(l);
				}
			};
			return lm;
		}
	}

	private class DisplayParner {

		private JLabel violationsCountLabel;
		private int violationCount=0;
		private Hashtable<String,NormPerformanceData> normPerfData=new Hashtable<String,NormPerformanceData>();
		private Vector<String> data= new Vector<String>();

		public String toString(NormPerformanceData pd){
			return "";
		}



		public void setViolationsCount(JLabel violationsCountLabel) {
			this.violationsCountLabel=violationsCountLabel;
		}



	}

	private static NormPerformanceDisplay instance;
	Hashtable<String,DisplayParner> normsAccount=new Hashtable<String,DisplayParner>();
	private JFrame normListPerformance;
	private Box partners;
	private Hashtable<String, DefaultListModel> par=new Hashtable<String, DefaultListModel>();
	private Hashtable<String, JLabel> violations=new Hashtable<String, JLabel>();
	private Hashtable<String, JLabel> punishments=new Hashtable<String, JLabel>();
	private JButton showFactsStore;
	private JCheckBox breakOnViolation;
	private JCheckBox breakOnPunishment;
	private JPanel breakPanel;

	public NormPerformanceDisplay() {
		normListPerformance=new JFrame("Norm monitoring");
		
		normListPerformance.setVisible(true);
		partners=Box.createVerticalBox();
		normListPerformance.getContentPane().setLayout(new BorderLayout());
		normListPerformance.getContentPane().add(new JScrollPane(partners),BorderLayout.CENTER);
		showFactsStore=new JButton("Show Workspace");
		breakOnViolation=new JCheckBox("Break on violation", false);
		breakOnPunishment=new JCheckBox("Break on punishment", false);
		Box northPanel=Box.createVerticalBox();
		breakPanel=new JPanel();
		breakPanel.add(breakOnViolation);
		breakPanel.add(breakOnPunishment);
		northPanel.add(showFactsStore);		
		northPanel.add(breakPanel);		
		normListPerformance.getContentPane().add(northPanel,BorderLayout.NORTH);		
		normListPerformance.pack();
	}
	
	public void setShowWorkSpaceHandler(ActionListener ap){
		this.showFactsStore.addActionListener(ap);
	}

	public void addPartner(String partner){
		Box partnerAccount=Box.createVerticalBox();
		DisplayParner dp=new DisplayParner();
		JPanel partnerData=new JPanel();
		partnerData.add(new JLabel(partner));
		partnerData.add(new JLabel(" Violations:"));
		JLabel violationsCount=new  JLabel("0");
		JLabel punishmentCount=new  JLabel("0");
		this.violations.put(partner, violationsCount);
		this.punishments.put(partner, punishmentCount);
		dp.setViolationsCount(violationsCount);
		partnerData.add(violationsCount);
		partnerAccount.add(partnerData);
		partnerData.add(new JLabel(" Punishments:"));
		partnerData.add(punishmentCount);
		JPanel violationList=new JPanel();
		partnerAccount.add(violationList);
		DefaultListModel<String> dlm=new DefaultListModel<String>();
		par.put(partner, dlm);
		JList normsAccount=new JList<String>(dlm);
		//normsData.put(partner,dp);
		violationList.add(new JScrollPane(normsAccount));

		partners.add(partnerAccount);
		normListPerformance.pack();
	}
	
	private enum DisplayStatus {RUNNING, PAUSED};
	
	private DisplayStatus status=DisplayStatus.RUNNING;

	public void addViolation(final PunishmentNorm pn){
		final String norm=pn.getName();
		final String partner=pn.getResponsible();
		if (breakOnViolation.isSelected() && status==DisplayStatus.RUNNING ){
			status=DisplayStatus.PAUSED;
			NormativeSystem.pausePHAT();
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {
					final JButton resume=new JButton("Resume");
					breakPanel.add(resume);	
					normListPerformance.pack();
					resume.addActionListener(new ActionListener() {						
						@Override
						public void actionPerformed(ActionEvent e) {
							NormativeSystem.resumePHAT();	
							breakPanel.remove(resume);
							normListPerformance.pack();
							status=DisplayStatus.RUNNING;
						}
					});
					
				}
				
			});
			
		}
	
		
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
					JLabel content=violations.get(partner);
				if (content==null)
					System.err.println("No violation counter stored for \""+partner+"\"");
				else {
					content.setText(""+(Integer.parseInt(content.getText())+1));
					DefaultListModel dlm = par.get(partner);
					dlm.addElement(""+pn.getTime()+": Violation "+norm);
					normListPerformance.pack();
				}
			}
		});

	}

	public void addPunishment(final PunishmentNorm pn){
		final String norm=pn.getName();
		final String partner=pn.getResponsible();
		if (breakOnPunishment.isSelected()  && status==DisplayStatus.RUNNING){
			status=DisplayStatus.PAUSED;
			NormativeSystem.pausePHAT();
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					final JButton resume=new JButton("Resume");
					breakPanel.add(resume);	
					normListPerformance.pack();
					resume.addActionListener(new ActionListener() {						
						@Override
						public void actionPerformed(ActionEvent e) {
							NormativeSystem.resumePHAT();	
							breakPanel.remove(resume);
							normListPerformance.pack();
							status=DisplayStatus.RUNNING;
						}
					});
					
				}
				
			});
			
		}
	
				
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				JLabel content=punishments.get(partner);
				if (content!=null){
					content.setText(""+(Integer.parseInt(content.getText())+1));
					DefaultListModel dlm = par.get(partner);
					dlm.addElement(""+pn.getTime()+": Punishment "+norm);
					normListPerformance.pack();
				} else
					System.err.println("Trying to add norm " +norm+" to nonregistered partner "+partner); 
			}

		});

	}


	public static NormPerformanceDisplay getInstance(){
		if (instance==null){
			instance=new NormPerformanceDisplay();
		}
		return instance;
	}

	public static void main(String args[]){

		NormPerformanceDisplay.getInstance().addPartner("partner1");
		NormPerformanceDisplay.getInstance().addPartner("partner2");
		NormPerformanceDisplay.getInstance().addViolation(new PunishOnTheGround("onenorm", "partner1",1,null));		
		NormPerformanceDisplay.getInstance().addViolation(new PunishOnTheGround("onenorm", "partner2",100,null));
		NormPerformanceDisplay.getInstance().addViolation(new PunishOnTheGround("onenorm", "partner2",200,null));
		NormPerformanceDisplay.getInstance().addPunishment(new PunishOnTheGround("onenorm", "partner2",100,null));
	}

	public boolean isPaused() {
		// TODO Auto-generated method stub
		return status==DisplayStatus.PAUSED;
	}



}
