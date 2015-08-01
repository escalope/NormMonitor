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


public class Action extends BasicFact {
	

	private String responsible;
	private String content;
	private String scope; 

	
	
	public String getResponsible() {
		return responsible;
	}

	public void setResponsible(String responsible) {
		this.responsible = responsible;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public Action(String responsible,String content,String scope) {
		this.responsible=responsible;
		this.content=content;
		this.scope=scope;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return (responsible+":"+content+" "+scope+" "+time).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Action){
			return  ((Action)obj).getResponsible().equals(responsible) && 
					((Action)obj).getContent().equals(content) 
					&&	((Action)obj).getScope().equals(scope)
					&&	((Action)obj).getTime()==getTime();
		}
		return false;
	}

	public String toString(){
		return "Action("+this.getResponsible()+", "+this.getContent()+","+this.getScope()+","+this.getTime()+")";
	}
}
