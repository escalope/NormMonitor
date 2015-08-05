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
package sociaal.ontology;

import java.util.Date;


public class ActionPerformed extends BasicFact {
	

	private String responsible="";
	private String actionType="";
	private String scope="none";
	private boolean successAction=false;
	private boolean failureAction=false;


	
	public boolean isSuccessAction(){
		return successAction;
	}
	
	public boolean isFailureAction(){
		return failureAction;
	}
	
	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		if (actionType!=null)
		this.actionType = actionType;
	}

	public String getResponsible() {
		return responsible;
	}

	public void setResponsible(String responsible) {
		if (responsible!=null)
		this.responsible = responsible;
	}


	public ActionPerformed(String responsible,String actionType, long time) {
		super(time);
		this.responsible=responsible;
		if (actionType!=null)
		this.actionType=actionType;
	}
	
	public ActionPerformed(String responsible,String actionType, long time,boolean isSuccessAction, boolean isFailureAction) {
		super(time);
		this.responsible=responsible;
		if (actionType!=null)
		this.actionType=actionType;
		this.successAction=isSuccessAction;
		this.failureAction=isFailureAction;
	}
	
	
	public ActionPerformed(String responsible,String actionType, String scope, long time) {
		super(time);
		this.responsible=responsible;
		if (actionType!=null)
		this.actionType=actionType;
		this.scope=scope;
	}
	
	public ActionPerformed(String responsible,String actionType, String scope, long time, boolean isSuccessAction, boolean isFailureAction) {
		super(time);
		this.responsible=responsible;
		if (actionType!=null)
		this.actionType=actionType;
		this.scope=scope;
		this.successAction=isSuccessAction;
		this.failureAction=isFailureAction;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return (responsible+":"+actionType+":"+scope+":"+time).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ActionPerformed){
			boolean part1=((ActionPerformed)obj).getResponsible().equals(responsible) ;
			boolean part2=((ActionPerformed)obj).getActionType().equals(actionType) ;
			return  part1&&part2 &&	((ActionPerformed)obj).getTime()==getTime() &&((ActionPerformed)obj).getScope().equals(getScope());
		}
		return false;
	}

	public String getScope() {
		return scope;
	}

	public String toString(){
		return "ActionPerformed("+this.getResponsible()+", "+this.getActionType()+","+scope+","+(this.getTime())+", successAct:"+successAction+",failureAct:"+failureAction+")";
	}
}
