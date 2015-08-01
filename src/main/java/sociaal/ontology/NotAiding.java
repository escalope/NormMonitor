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

public class NotAiding extends BasicFact {
	
	private String aidee;
	private String aided; 
	public String getAided() {
		return aidee;
	}
	public String getAidee() {
		return aidee;
	}
	public NotAiding(String assisted,String assisting) {
		this.aidee=assisting;
		this.aided=assisted;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return (aided+aidee).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NotAiding){
			return  ((NotAiding)obj).getAided().equals(aidee) && ((NotAiding)obj).getAidee().equals(aided);
		}
		return false;
	}
	
	public String toString(){
		return "NotAiding("+this.getAidee()+","+this.getAided()+","+this.getTime()+")";
	}

}
