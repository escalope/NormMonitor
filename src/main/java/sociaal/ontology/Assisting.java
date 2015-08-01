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

public class Assisting extends BasicFact {

	private String assisted;
	private String assisting; 
	private String scope="";
	public String getAssisted() {
		return assisted;
	}
	public String getAssisting() {
		return assisting;
	}
	public Assisting(String assisted,String assisting) {
		this.assisted=assisted;
		this.assisting=assisting;
	}
	
	public Assisting(String assisting,String assisted, String scope) {
		this.assisted=assisted;
		this.assisting=assisting;
		this.scope=scope;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return (assisting+assisted+scope).hashCode();
	}

	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Assisting){
			return  ((Assisting)obj).getAssisted().equals(assisted) && ((Assisting)obj).getAssisting().equals(assisting)&& ((Assisting)obj).getScope().equals(scope);
		}
		return false;
	}
	
	public String toString(){
		return "Assisting("+this.getAssisting()+","+this.getAssisted()+","+this.getTime()+")";
	}

}
