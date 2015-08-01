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

public class UnnecesaryAction extends BasicFact{
	private String assisted;
	private String assisting; 
	public String getAssisted() {
		return assisted;
	}
	public String getAssisting() {
		return assisting;
	}
	public UnnecesaryAction(String assisted,String assisting) {
		this.assisted=assisted;
		this.assisting=assisting;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return (assisting+assisted).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UnnecesaryAction){
			return  ((UnnecesaryAction)obj).getAssisted().equals(assisted) && ((UnnecesaryAction)obj).getAssisting().equals(assisting);
		}
		return false;
	}
	
	public String toString(){
		return "UnnecesaryAction("+this.getAssisting()+","+this.getAssisted()+","+this.getTime()+")";
	}


}
