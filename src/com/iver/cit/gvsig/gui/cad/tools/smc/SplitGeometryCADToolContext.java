/**
 Finite state machine, generated with fsm tool
 (http://smc.sourceforge.net)
 @author Alvaro Zabala
 */

package com.iver.cit.gvsig.gui.cad.tools.smc;

import java.awt.event.InputEvent;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.gui.cad.tools.SplitGeometryCADTool;

public final class SplitGeometryCADToolContext extends statemap.FSMContext {
    // ---------------------------------------------------------------
    // Member methods.
    //

    public SplitGeometryCADToolContext(SplitGeometryCADTool owner) {
	super();

	_owner = owner;
	setState(SplitGeometry.FirstPoint);
	SplitGeometry.FirstPoint.Entry(this);
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

    public void addValue(double d) {
	_transition = "addValue";
	getState().addValue(this, d);
	_transition = "";
	return;
    }

    public SplitGeometryCADToolState getState()
	    throws statemap.StateUndefinedException {
	if (_state == null) {
	    throw (new statemap.StateUndefinedException());
	}

	return ((SplitGeometryCADToolState) _state);
    }

    protected SplitGeometryCADTool getOwner() {
	return (_owner);
    }

    // ---------------------------------------------------------------
    // Member data.
    //

    transient private SplitGeometryCADTool _owner;

    // ---------------------------------------------------------------
    // Inner classes.
    //

    public static abstract class SplitGeometryCADToolState extends
	    statemap.State {
	// -----------------------------------------------------------
	// Member methods.
	//

	protected SplitGeometryCADToolState(String name, int id) {
	    super(name, id);
	}

	protected void Entry(SplitGeometryCADToolContext context) {
	}

	protected void Exit(SplitGeometryCADToolContext context) {
	}

	protected void addOption(SplitGeometryCADToolContext context, String s) {
	    Default(context);
	}

	protected void addPoint(SplitGeometryCADToolContext context,
		double pointX, double pointY, InputEvent event) {
	    Default(context);
	}

	protected void addValue(SplitGeometryCADToolContext context, double d) {
	    Default(context);
	}

	protected void Default(SplitGeometryCADToolContext context) {
	    throw (new statemap.TransitionUndefinedException("State: "
		    + context.getState().getName() + ", Transition: "
		    + context.getTransition()));
	}

	// -----------------------------------------------------------
	// Member data.
	//
    }

    /* package */static abstract class SplitGeometry {
	// -----------------------------------------------------------
	// Member methods.
	//

	// -----------------------------------------------------------
	// Member data.
	//

	// -------------------------------------------------------
	// Statics.
	//
	/* package */static SplitGeometry_Default.SplitGeometry_FirstPoint FirstPoint;
	/* package */static SplitGeometry_Default.SplitGeometry_DigitizingLine DigitizingLine;
	private static SplitGeometry_Default Default;

	static {
	    FirstPoint = new SplitGeometry_Default.SplitGeometry_FirstPoint(
		    "SplitGeometry.FirstPoint", 0);
	    DigitizingLine = new SplitGeometry_Default.SplitGeometry_DigitizingLine(
		    "SplitGeometry.DigitizingLine", 1);
	    Default = new SplitGeometry_Default("SplitGeometry.Default", -1);
	}

    }

    protected static class SplitGeometry_Default extends
	    SplitGeometryCADToolState {
	// -----------------------------------------------------------
	// Member methods.
	//

	protected SplitGeometry_Default(String name, int id) {
	    super(name, id);
	}

	@Override
	protected void addOption(SplitGeometryCADToolContext context, String s) {
	    SplitGeometryCADTool ctxt = context.getOwner();

	    if (s.equals("")) {
		boolean loopbackFlag = context.getState().getName()
			.equals(SplitGeometry.FirstPoint.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.restorePreviousTool();
		    ctxt.setQuestion(PluginServices.getText(this,
			    "insert_first_point"));
		    ctxt.setDescription(new String[] { "cancel" });
		    ctxt.end();
		} finally {
		    context.setState(SplitGeometry.FirstPoint);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    } else if (s.equals(PluginServices.getText(this, "cancel"))) {
		boolean loopbackFlag = context.getState().getName()
			.equals(SplitGeometry.FirstPoint.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.end();
		} finally {
		    context.setState(SplitGeometry.FirstPoint);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    } else {
		boolean loopbackFlag = context.getState().getName()
			.equals(SplitGeometry.FirstPoint.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.throwOptionException(
			    PluginServices.getText(this, "incorrect_option"), s);
		} finally {
		    context.setState(SplitGeometry.FirstPoint);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    }

	    return;
	}

	@Override
	protected void addValue(SplitGeometryCADToolContext context, double d) {
	    SplitGeometryCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(SplitGeometry.FirstPoint.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.throwValueException(
			PluginServices.getText(this, "incorrect_value"), d);
	    } finally {
		context.setState(SplitGeometry.FirstPoint);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	@Override
	protected void addPoint(SplitGeometryCADToolContext context,
		double pointX, double pointY, InputEvent event) {
	    SplitGeometryCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(SplitGeometry.FirstPoint.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.throwPointException(
			PluginServices.getText(this, "incorrect_point"),
			pointX, pointY);
	    } finally {
		context.setState(SplitGeometry.FirstPoint);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	// -----------------------------------------------------------
	// Inner classse.
	//

	private static final class SplitGeometry_FirstPoint extends
		SplitGeometry_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private SplitGeometry_FirstPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void Entry(SplitGeometryCADToolContext context) {
		SplitGeometryCADTool ctxt = context.getOwner();

		ctxt.setQuestion(PluginServices.getText(this,
			"insert_first_point"));
		ctxt.setDescription(new String[] { "cancel" });
		return;
	    }

	    @Override
	    protected void addPoint(SplitGeometryCADToolContext context,
		    double pointX, double pointY, InputEvent event) {
		SplitGeometryCADTool ctxt = context.getOwner();

		(context.getState()).Exit(context);
		context.clearState();
		try {
		    ctxt.setQuestion(PluginServices.getText(this,
			    "insert_more_points_or_finish"));
		    ctxt.setDescription(new String[] { "cancel" });
		    ctxt.addPoint(pointX, pointY, event);
		} finally {
		    context.setState(SplitGeometry.DigitizingLine);
		    (context.getState()).Entry(context);
		}
		return;
	    }

	    // -------------------------------------------------------
	    // Member data.
	    //
	}

	private static final class SplitGeometry_DigitizingLine extends
		SplitGeometry_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private SplitGeometry_DigitizingLine(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void addOption(SplitGeometryCADToolContext context,
		    String s) {
		SplitGeometryCADTool ctxt = context.getOwner();

		if (s.equalsIgnoreCase(PluginServices.getText(this,
			"SplitGeometryCADTool.end"))
			|| s.equalsIgnoreCase(PluginServices.getText(this,
				"terminate"))) {

		    (context.getState()).Exit(context);
		    context.clearState();
		    try {
			ctxt.finishDigitizedLine();
			ctxt.splitSelectedGeometryWithDigitizedLine();
			ctxt.end();
		    } finally {
			context.setState(SplitGeometry.FirstPoint);
			(context.getState()).Entry(context);
		    }
		} else {
		    super.addOption(context, s);
		}

		return;
	    }

	    @Override
	    protected void addPoint(SplitGeometryCADToolContext context,
		    double pointX, double pointY, InputEvent event) {
		SplitGeometryCADTool ctxt = context.getOwner();

		SplitGeometryCADToolState endState = context.getState();

		context.clearState();
		try {
		    ctxt.setQuestion(PluginServices.getText(this,
			    "insert_next_point")
			    + PluginServices.getText(this, "cad.or")
			    + " "
			    + PluginServices.getText(this, "end")
			    + "["
			    + PluginServices.getText(this,
				    "PolylineCADTool.end") + "]");
		    ctxt.setDescription(new String[] { "inter_arc",
			    "terminate", "cancel" });
		    ctxt.addPoint(pointX, pointY, event);
		} finally {
		    context.setState(endState);
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
