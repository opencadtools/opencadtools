/*
 * Copyright 2008 Deputación Provincial de A Coruña
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

package com.iver.cit.gvsig.gui.cad.tools.smc;

import java.awt.event.InputEvent;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.gui.cad.CADStatus;
import com.iver.cit.gvsig.gui.cad.tools.RedigitalizePolygonCADTool;

/**
 * @author Jose Ignacio Lamas Fonte [LBD]
 * @author Nacho Varela [Cartolab]
 * @author Pablo Sanxiao [CartoLab]
 */
public final class RedigitalizePolygonCADToolContext extends
	statemap.FSMContext {
    // ---------------------------------------------------------------
    // Member methods.

    public RedigitalizePolygonCADToolContext(RedigitalizePolygonCADTool owner) {
	super();

	_owner = owner;
	setState(RedigitalizePolygon.FirstPoint);
	RedigitalizePolygon.FirstPoint.Entry(this);
    }

    public void addOption(String s) {
	_transition = "addOption";
	getState().addOption(this, s);
	_transition = "";
	return;
    }

    public void addPoint(double pointX, double pointY, InputEvent event) {
	_transition = "addPoint";
	getState().addPoint(this, pointX, pointY, event);
	_transition = "";
	return;
    }

    public void removePoint(InputEvent event, int numPoints) {
	_transition = "removePoint";
	getState().removePoint(this, event, numPoints);
	_transition = "";
	return;
    }

    public RedigitalizePolygonCADToolState getState()
	    throws statemap.StateUndefinedException {
	if (_state == null) {
	    throw (new statemap.StateUndefinedException());
	}

	return ((RedigitalizePolygonCADToolState) _state);
    }

    protected RedigitalizePolygonCADTool getOwner() {
	return (_owner);
    }

    // ---------------------------------------------------------------
    // Member data.

    transient private RedigitalizePolygonCADTool _owner;

    // ---------------------------------------------------------------
    // Inner classes.

    public static abstract class RedigitalizePolygonCADToolState extends
	    statemap.State {
	// -----------------------------------------------------------
	// Member methods.
	//

	protected RedigitalizePolygonCADToolState(String name, int id) {
	    super(name, id);
	}

	protected void Entry(RedigitalizePolygonCADToolContext context) {
	}

	protected void Exit(RedigitalizePolygonCADToolContext context) {
	}

	protected void addOption(RedigitalizePolygonCADToolContext context,
		String s) {
	    Default(context);
	}

	protected void addPoint(RedigitalizePolygonCADToolContext context,
		double pointX, double pointY, InputEvent event) {
	    Default(context);
	}

	protected void removePoint(RedigitalizePolygonCADToolContext context,
		InputEvent event, int numPoints) {
	    Default(context);
	}

	protected void Default(RedigitalizePolygonCADToolContext context) {
	    throw (new statemap.TransitionUndefinedException("State: "
		    + context.getState().getName() + ", Transition: "
		    + context.getTransition()));
	}

	// -----------------------------------------------------------
	// Member data.
	//
    }

    /* package */static abstract class RedigitalizePolygon {
	// -----------------------------------------------------------
	// Member methods.
	//

	// -----------------------------------------------------------
	// Member data.
	//

	// -------------------------------------------------------
	// Statics.
	//
	/* package */static RedigitalizePolygon_Default.RedigitalizePolygon_FirstPoint FirstPoint;
	/* package */static RedigitalizePolygon_Default.RedigitalizePolygon_SecondPoint SecondPoint;
	/* package */static RedigitalizePolygon_Default.RedigitalizePolygon_NextPoint NextPoint;
	private static RedigitalizePolygon_Default Default;

	static {
	    FirstPoint = new RedigitalizePolygon_Default.RedigitalizePolygon_FirstPoint(
		    "RedigitalizePolygon.FirstPoint", 0);
	    SecondPoint = new RedigitalizePolygon_Default.RedigitalizePolygon_SecondPoint(
		    "RedigitalizePolygon.SecondPoint", 1);
	    NextPoint = new RedigitalizePolygon_Default.RedigitalizePolygon_NextPoint(
		    "RedigitalizePolygon.NextPoint", 2);
	    Default = new RedigitalizePolygon_Default(
		    "RedigitalizePolygon.Default", -1);
	}

    }

    protected static class RedigitalizePolygon_Default extends
	    RedigitalizePolygonCADToolState {
	// -----------------------------------------------------------
	// Member methods.
	//

	protected RedigitalizePolygon_Default(String name, int id) {
	    super(name, id);
	}

	@Override
	protected void addPoint(RedigitalizePolygonCADToolContext context,
		double pointX, double pointY, InputEvent event) {
	    RedigitalizePolygonCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(RedigitalizePolygon.FirstPoint.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.throwPointException(
			PluginServices.getText(this, "incorrect_point"),
			pointX, pointY);
	    } finally {
		context.setState(RedigitalizePolygon.FirstPoint);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	@Override
	protected void removePoint(RedigitalizePolygonCADToolContext context,
		InputEvent event, int numPoints) {
	    RedigitalizePolygonCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(RedigitalizePolygon.FirstPoint.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.throwNoPointsException(PluginServices.getText(this,
			"no_points"));
	    } finally {
		context.setState(RedigitalizePolygon.FirstPoint);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	@Override
	protected void addOption(RedigitalizePolygonCADToolContext context,
		String s) {
	    RedigitalizePolygonCADTool ctxt = context.getOwner();

	    if (s.equals("C") || s.equals("c")
		    || s.equals(PluginServices.getText(this, "cancel"))) {
		boolean loopbackFlag = context.getState().getName()
			.equals(RedigitalizePolygon.FirstPoint.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.clear();
		} finally {
		    context.setState(RedigitalizePolygon.FirstPoint);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    } else {
		RedigitalizePolygonCADToolState endState = context.getState();

		context.clearState();
		try {
		    ctxt.throwOptionException(
			    PluginServices.getText(this, "incorrect_option"), s);
		} finally {
		    context.setState(endState);
		}
	    }

	    return;
	}

	// -----------------------------------------------------------
	// Inner classse.
	//

	private static final class RedigitalizePolygon_FirstPoint extends
		RedigitalizePolygon_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private RedigitalizePolygon_FirstPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void Entry(RedigitalizePolygonCADToolContext context) {
		RedigitalizePolygonCADTool ctxt = context.getOwner();

		ctxt.setQuestion(PluginServices.getText(this,
			"redigitaliza_insert_first_point"));
		ctxt.setDescription(new String[] { "cancel" });
		return;
	    }

	    @Override
	    protected void addPoint(RedigitalizePolygonCADToolContext context,
		    double pointX, double pointY, InputEvent event) {
		RedigitalizePolygonCADTool ctxt = context.getOwner();

		if (ctxt.pointInsideFeature(pointX, pointY)) {

		    (context.getState()).Exit(context);
		    context.clearState();
		    try {
			boolean deleteButton3 = CADStatus.getCADStatus()
				.isDeleteButtonActivated();
			if (deleteButton3) {
			    ctxt.setQuestion(PluginServices.getText(this,
				    "redigitaliza_insert_second_point_del"));
			} else {
			    ctxt.setQuestion(PluginServices.getText(this,
				    "redigitaliza_insert_second_point"));
			}
		    } finally {
			context.setState(RedigitalizePolygon.SecondPoint);
			(context.getState()).Entry(context);
		    }
		} else {
		    RedigitalizePolygonCADToolState endState = context
			    .getState();

		    context.clearState();
		    try {
			ctxt.throwPointException(PluginServices.getText(this,
				"redigitaliza_incorrect_point"), pointX, pointY);
			ctxt.setQuestion(PluginServices.getText(this,
				"redigitaliza_insert_first_point"));
		    } finally {
			context.setState(endState);
		    }
		}

		return;
	    }

	    // -------------------------------------------------------
	    // Member data.
	    //
	}

	private static final class RedigitalizePolygon_SecondPoint extends
		RedigitalizePolygon_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private RedigitalizePolygon_SecondPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void Entry(RedigitalizePolygonCADToolContext context) {
		RedigitalizePolygonCADTool ctxt = context.getOwner();
		ctxt.setDescription(new String[] { "cancel", "removePoint" });
	    }

	    @Override
	    protected void addPoint(RedigitalizePolygonCADToolContext context,
		    double pointX, double pointY, InputEvent event) {
		RedigitalizePolygonCADTool ctxt = context.getOwner();

		if (ctxt.secondPointInsideFeature(pointX, pointY)) {

		    (context.getState()).Exit(context);
		    context.clearState();
		    try {
			boolean deleteButton3 = CADStatus.getCADStatus()
				.isDeleteButtonActivated();
			if (deleteButton3) {
			    ctxt.setQuestion(PluginServices
				    .getText(this,
					    "redigitaliza_poligono_insert_other_point_del"));
			} else {
			    ctxt.setQuestion(PluginServices.getText(this,
				    "redigitaliza_poligono_insert_other_point"));
			}
		    } finally {
			context.setState(RedigitalizePolygon.NextPoint);
			(context.getState()).Entry(context);
		    }
		} else {
		    RedigitalizePolygonCADToolState endState = context
			    .getState();

		    context.clearState();
		    try {
			ctxt.throwPointException(PluginServices.getText(this,
				"redigitaliza_incorrect_point"), pointX, pointY);
			boolean deleteButton3 = CADStatus.getCADStatus()
				.isDeleteButtonActivated();
			if (deleteButton3) {
			    ctxt.setQuestion(PluginServices.getText(this,
				    "redigitaliza_insert_second_point_del"));
			} else {
			    ctxt.setQuestion(PluginServices.getText(this,
				    "redigitaliza_insert_second_point"));
			}
		    } finally {
			context.setState(endState);
		    }
		}

		return;
	    }

	    @Override
	    protected void removePoint(
		    RedigitalizePolygonCADToolContext context,
		    InputEvent event, int numPoints) {
		RedigitalizePolygonCADTool ctxt = context.getOwner();

		(context.getState()).Exit(context);
		context.clearState();
		try {
		    ctxt.setQuestion(PluginServices.getText(this,
			    "redigitaliza_insert_first_point"));
		    ctxt.removeFirstPoint(event);
		} finally {
		    context.setState(RedigitalizePolygon.FirstPoint);
		    (context.getState()).Entry(context);
		}
		return;
	    }

	    // -------------------------------------------------------
	    // Member data.
	    //
	}

	private static final class RedigitalizePolygon_NextPoint extends
		RedigitalizePolygon_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private RedigitalizePolygon_NextPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void Entry(RedigitalizePolygonCADToolContext context) {

		(context.getOwner()).setDescription(new String[] { "cancel",
			"terminate", "change_base_geom", "removePoint" });

	    }

	    @Override
	    protected void addOption(RedigitalizePolygonCADToolContext context,
		    String s) {
		RedigitalizePolygonCADTool ctxt = context.getOwner();

		// NACHOV if ((((s.equals("g")||s.equals("G")))) &&
		// ctxt.checksOnEdition(ctxt.getGeometriaResultante(),
		// ctxt.getCurrentGeoid())))
		if (s.equals("espacio")
			|| s.equals(PluginServices.getText(this, "terminate"))) {

		    (context.getState()).Exit(context);
		    context.clearState();
		    try {
			ctxt.setQuestion(PluginServices.getText(this,
				"redigitaliza_insert_first_point"));
			ctxt.saveChanges();
			ctxt.clear();
		    } finally {
			context.setState(RedigitalizePolygon.FirstPoint);
			(context.getState()).Entry(context);
		    }
		}
		// else if (s.equals("g")||s.equals("G"))
		// {

		// // No actions.
		// }
		else if (s.equals("tab")
			|| s.equals(PluginServices.getText(this,
				"change_base_geom"))) {

		    RedigitalizePolygonCADToolState endState = context
			    .getState();
		    context.clearState();

		    try {
			ctxt.changesPolygonPart();
		    } finally {
			context.setState(endState);
		    }
		} else {
		    super.addOption(context, s);
		}
		return;
	    }

	    @Override
	    protected void addPoint(RedigitalizePolygonCADToolContext context,
		    double pointX, double pointY, InputEvent event) {
		RedigitalizePolygonCADTool ctxt = context.getOwner();

		RedigitalizePolygonCADToolState endState = context.getState();

		context.clearState();
		try {
		    boolean deleteButton3 = CADStatus.getCADStatus()
			    .isDeleteButtonActivated();
		    if (deleteButton3) {
			ctxt.setQuestion(PluginServices.getText(this,
				"redigitaliza_poligono_insert_other_point_del"));
		    } else {
			ctxt.setQuestion(PluginServices.getText(this,
				"redigitaliza_poligono_insert_other_point"));
		    }
		    ctxt.addPoint(pointX, pointY, event);
		} finally {
		    context.setState(endState);
		}
		return;
	    }

	    @Override
	    protected void removePoint(
		    RedigitalizePolygonCADToolContext context,
		    InputEvent event, int numPoints) {
		RedigitalizePolygonCADTool ctxt = context.getOwner();

		if (numPoints > 0) {
		    RedigitalizePolygonCADToolState endState = context
			    .getState();

		    context.clearState();
		    try {
			ctxt.removePoint(event);
		    } finally {
			context.setState(endState);
		    }
		} else if (numPoints == 0) {

		    (context.getState()).Exit(context);
		    context.clearState();
		    try {
			boolean deleteButton3 = CADStatus.getCADStatus()
				.isDeleteButtonActivated();
			if (deleteButton3) {
			    ctxt.setQuestion(PluginServices.getText(this,
				    "redigitaliza_insert_second_point_del"));
			} else {
			    ctxt.setQuestion(PluginServices.getText(this,
				    "redigitaliza_insert_second_point"));
			}
			ctxt.removeSecondPoint(event);
		    } finally {
			context.setState(RedigitalizePolygon.SecondPoint);
			ctxt.setDescription(new String[] { "cancel" });
			(context.getState()).Entry(context);
		    }
		} else {
		    super.removePoint(context, event, numPoints);
		}

		return;
	    }

	    // -------------------------------------------------------
	    // Member data.
	    //
	}

	// -----------------------------------------------------------
	// Member data.
	//
    }
}
