/*
 * 
 * Copyright 2009 Deputación Provincial de Pontevedra
 * Copyright 2010 CartoLab, Universidad de A Coruña
 *
 * This file is part of openCADTools, developed by the Cartography
 * Engineering Laboratory of the University of A Coruña (CartoLab).
 * http://www.cartolab.es
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 */
package com.iver.cit.gvsig.project.documents.view.snapping;

/**
 * @author Javier Estévez <jestevez (at) cartolab.es>
 * @author Francisco Puga <fpuga (at) cartolab.es>
 */
public class SnapperStatus {

	private static boolean vertexActivated;
	private static boolean nearLineActivated;

	private static SnapperStatus snapperStatus;

	private SnapperStatus(){
		snapperStatus = this;
		vertexActivated = true;
		nearLineActivated = true;
	}

	public static SnapperStatus getSnapperStatus(){
		if (snapperStatus == null){
			snapperStatus = new SnapperStatus();
		}
		return snapperStatus;
	}

	public static boolean isVertexActivated() {
		return vertexActivated;
	}

	public static boolean isNearLineActivated() {
		return nearLineActivated;
	}

	public static void setVertexActivated(boolean activate) {
		vertexActivated = activate;
	}

	public static void setNearLineActivated(boolean activate) {
		nearLineActivated = activate;
	}

}
