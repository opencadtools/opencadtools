//
// Vicente Caballero Navarro

package com.iver.cit.gvsig.gui.cad.tools.smc;

import java.awt.event.InputEvent;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.gui.cad.tools.SplineCADTool;

public final class SplineCADToolContext extends statemap.FSMContext {
    // ---------------------------------------------------------------
    // Member methods.
    //

    public SplineCADToolContext(SplineCADTool owner) {
	super();

	_owner = owner;
	setState(Spline.FirstPoint);
	Spline.FirstPoint.Entry(this);
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

    public void endPoint(double pointX, double pointY, InputEvent event) {
	_transition = "endPoint";
	getState().endPoint(this, pointX, pointY, event);
	_transition = "";
	return;
    }

    public SplineCADToolState getState()
	    throws statemap.StateUndefinedException {
	if (_state == null) {
	    throw (new statemap.StateUndefinedException());
	}

	return ((SplineCADToolState) _state);
    }

    protected SplineCADTool getOwner() {
	return (_owner);
    }

    // ---------------------------------------------------------------
    // Member data.
    //

    transient private SplineCADTool _owner;

    // ---------------------------------------------------------------
    // Inner classes.
    //

    public static abstract class SplineCADToolState extends statemap.State {
	// -----------------------------------------------------------
	// Member methods.
	//

	protected SplineCADToolState(String name, int id) {
	    super(name, id);
	}

	protected void Entry(SplineCADToolContext context) {
	}

	protected void Exit(SplineCADToolContext context) {
	}

	protected void addOption(SplineCADToolContext context, String s) {
	    Default(context);
	}

	protected void addPoint(SplineCADToolContext context, double pointX,
		double pointY, InputEvent event) {
	    Default(context);
	}

	protected void addValue(SplineCADToolContext context, double d) {
	    Default(context);
	}

	protected void endPoint(SplineCADToolContext context, double pointX,
		double pointY, InputEvent event) {
	    Default(context);
	}

	protected void Default(SplineCADToolContext context) {
	    throw (new statemap.TransitionUndefinedException("State: "
		    + context.getState().getName() + ", Transition: "
		    + context.getTransition()));
	}

	// -----------------------------------------------------------
	// Member data.
	//
    }

    /* package */static abstract class Spline {
	// -----------------------------------------------------------
	// Member methods.
	//

	// -----------------------------------------------------------
	// Member data.
	//

	// -------------------------------------------------------
	// Statics.
	//
	/* package */static Spline_Default.Spline_FirstPoint FirstPoint;
	/* package */static Spline_Default.Spline_NextPoint NextPoint;
	private static Spline_Default Default;

	static {
	    FirstPoint = new Spline_Default.Spline_FirstPoint(
		    "Spline.FirstPoint", 0);
	    NextPoint = new Spline_Default.Spline_NextPoint("Spline.NextPoint",
		    1);
	    Default = new Spline_Default("Spline.Default", -1);
	}

    }

    protected static class Spline_Default extends SplineCADToolState {
	// -----------------------------------------------------------
	// Member methods.
	//

	protected Spline_Default(String name, int id) {
	    super(name, id);
	}

	@Override
	protected void addOption(SplineCADToolContext context, String s) {
	    SplineCADTool ctxt = context.getOwner();

	    if (s.equals(PluginServices.getText(this, "cancel"))) {
		boolean loopbackFlag = context.getState().getName()
			.equals(Spline.FirstPoint.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.cancel();
		} finally {
		    context.setState(Spline.FirstPoint);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    } else if (s.equals("")) {
		boolean loopbackFlag = context.getState().getName()
			.equals(Spline.FirstPoint.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.endGeometry();
		} finally {
		    context.setState(Spline.FirstPoint);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    } else {
		boolean loopbackFlag = context.getState().getName()
			.equals(Spline.FirstPoint.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.throwOptionException(
			    PluginServices.getText(this, "incorrect_option"), s);
		} finally {
		    context.setState(Spline.FirstPoint);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    }

	    return;
	}

	@Override
	protected void addValue(SplineCADToolContext context, double d) {
	    SplineCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(Spline.FirstPoint.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.throwValueException(
			PluginServices.getText(this, "incorrect_value"), d);
	    } finally {
		context.setState(Spline.FirstPoint);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	@Override
	protected void addPoint(SplineCADToolContext context, double pointX,
		double pointY, InputEvent event) {
	    SplineCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(Spline.FirstPoint.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.throwPointException(
			PluginServices.getText(this, "incorrect_point"),
			pointX, pointY);
	    } finally {
		context.setState(Spline.FirstPoint);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	@Override
	protected void endPoint(SplineCADToolContext context, double pointX,
		double pointY, InputEvent event) {
	    SplineCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(Spline.FirstPoint.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.addPoint(pointX, pointY, event);
		ctxt.endGeometry();
	    } finally {
		context.setState(Spline.FirstPoint);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	// -----------------------------------------------------------
	// Inner classse.
	//

	private static final class Spline_FirstPoint extends Spline_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private Spline_FirstPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void Entry(SplineCADToolContext context) {
		SplineCADTool ctxt = context.getOwner();

		ctxt.setQuestion(PluginServices.getText(this,
			"insert_first_point"));
		ctxt.setDescription(new String[] { "cancel" });
		return;
	    }

	    @Override
	    protected void addPoint(SplineCADToolContext context,
		    double pointX, double pointY, InputEvent event) {
		SplineCADTool ctxt = context.getOwner();

		(context.getState()).Exit(context);
		context.clearState();
		try {
		    ctxt.setQuestion(PluginServices.getText(this,
			    "insert_next_point")
			    + ", "
			    + PluginServices.getText(this, "close")
			    + " "
			    + "["
			    + PluginServices.getText(this,
				    "SplineCADTool.close")
			    + "] "
			    + PluginServices.getText(this, "cad.or")
			    + " "
			    + PluginServices.getText(this, "end")
			    + " "
			    + "["
			    + PluginServices.getText(this, "SplineCADTool.end")
			    + "]");
		    ctxt.setDescription(new String[] { "close", "terminate",
			    "cancel" });
		    ctxt.addPoint(pointX, pointY, event);
		} finally {
		    context.setState(Spline.NextPoint);
		    (context.getState()).Entry(context);
		}
		return;
	    }

	    // -------------------------------------------------------
	    // Member data.
	    //
	}

	private static final class Spline_NextPoint extends Spline_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private Spline_NextPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void addOption(SplineCADToolContext context, String s) {
		SplineCADTool ctxt = context.getOwner();

		if (s.equalsIgnoreCase(PluginServices.getText(this,
			"SplineCADTool.close"))
			|| s.equals(PluginServices.getText(this, "close"))) {

		    (context.getState()).Exit(context);
		    context.clearState();
		    try {
			ctxt.addOption(s);
			ctxt.closeGeometry();
			ctxt.endGeometry();
			ctxt.end();
		    } finally {
			context.setState(Spline.FirstPoint);
			(context.getState()).Entry(context);
		    }
		} else if (s.equalsIgnoreCase(PluginServices.getText(this,
			"SplineCADTool.end"))
			|| s.equals(PluginServices.getText(this, "terminate"))) {

		    (context.getState()).Exit(context);
		    context.clearState();
		    try {
			ctxt.addOption(s);
			ctxt.endGeometry();
			ctxt.end();
		    } finally {
			context.setState(Spline.FirstPoint);
			(context.getState()).Entry(context);
		    }
		} else {
		    super.addOption(context, s);
		}

		return;
	    }

	    @Override
	    protected void addPoint(SplineCADToolContext context,
		    double pointX, double pointY, InputEvent event) {
		SplineCADTool ctxt = context.getOwner();

		SplineCADToolState endState = context.getState();

		context.clearState();
		try {
		    ctxt.setQuestion(PluginServices.getText(this,
			    "insert_next_point")
			    + ", "
			    + PluginServices.getText(this, "close")
			    + " "
			    + "["
			    + PluginServices.getText(this,
				    "SplineCADTool.close")
			    + "] "
			    + PluginServices.getText(this, "cad.or")
			    + " "
			    + PluginServices.getText(this, "end")
			    + " "
			    + "["
			    + PluginServices.getText(this, "SplineCADTool.end")
			    + "]");
		    ctxt.setDescription(new String[] { "close", "terminate",
			    "cancel" });
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
