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

import org.drools.core.spi.KnowledgeHelper;

import sociaal.PunishmentNorm;

public class NotAidingTheFallen extends PunishmentNorm{

	public NotAidingTheFallen(String name, String responsible, long time, KnowledgeHelper kb) {
		super(name,responsible, time, kb);
	}

	@Override
	public void performPunishment() {
		
	}

	@Override
	public boolean shouldPunish(long millis) {
		return false;
	}
	
}
