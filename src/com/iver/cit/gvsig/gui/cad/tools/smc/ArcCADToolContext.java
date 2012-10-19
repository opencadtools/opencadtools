//
// Vicente Caballero Navarro

package com.iver.cit.gvsig.gui.cad.tools.smc;

import java.awt.event.InputEvent;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.gui.cad.tools.ArcCADTool;

public final class ArcCADToolContext extends statemap.FSMContext {
    // ---------------------------------------------------------------
    // Member methods.
    //

    public ArcCADToolContext(ArcCADTool owner) {
	super();

	_owner = owner;
	setState(Arc.FirstPoint);
	Arc.FirstPoint.Entry(this);
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

    public ArcCADToolState getState() throws statemap.StateUndefinedException {
	if (_state == null) {
	    throw (new statemap.StateUndefinedException());
	}

	return ((ArcCADToolState) _state);
    }

    protected ArcCADTool getOwner() {
	return (_owner);
    }

    // ---------------------------------------------------------------
    // Member data.
    //

    transient private ArcCADTool _owner;

    // ---------------------------------------------------------------
    // Inner classes.
    //

    public static abstract class ArcCADToolState extends statemap.State {
	// -----------------------------------------------------------
	// Member methods.
	//

	protected ArcCADToolState(String name, int id) {
	    super(name, id);
	}

	protected void Entry(ArcCADToolContext context) {
	}

	protected void Exit(ArcCADToolContext context) {
	}

	protected void addOption(ArcCADToolContext context, String s) {
	    Default(context);
	}

	protected void addPoint(ArcCADToolContext context, double pointX,
		double pointY, InputEvent event) {
	    Default(context);
	}

	protected void addValue(ArcCADToolContext context, double d) {
	    Default(context);
	}

	protected void Default(ArcCADToolContext context) {
	    throw (new statemap.TransitionUndefinedException("State: "
		    + context.getState().getName() + ", Transition: "
		    + context.getTransition()));
	}

	// -----------------------------------------------------------
	// Member data.
	//
    }

    /* package */static abstract class Arc {
	// -----------------------------------------------------------
	// Member methods.
	//

	// -----------------------------------------------------------
	// Member data.
	//

	// -------------------------------------------------------
	// Statics.
	//
	/* package */static Arc_Default.Arc_FirstPoint FirstPoint;
	/* package */static Arc_Default.Arc_SecondPoint SecondPoint;
	/* package */static Arc_Default.Arc_ThirdPoint ThirdPoint;
	private static Arc_Default Default;

	static {
	    FirstPoint = new Arc_Default.Arc_FirstPoint("Arc.FirstPoint", 0);
	    SecondPoint = new Arc_Default.Arc_SecondPoint("Arc.SecondPoint", 1);
	    ThirdPoint = new Arc_Default.Arc_ThirdPoint("Arc.ThirdPoint", 2);
	    Default = new Arc_Default("Arc.Default", -1);
	}

    }

    protected static class Arc_Default extends ArcCADToolState {
	// -----------------------------------------------------------
	// Member methods.
	//

	protected Arc_Default(String name, int id) {
	    super(name, id);
	}

	@Override
	protected void addOption(ArcCADToolContext context, String s) {
	    ArcCADTool ctxt = context.getOwner();

	    if (s.equals(PluginServices.getText(this, "cancel"))) {
		boolean loopbackFlag = context.getState().getName()
			.equals(Arc.FirstPoint.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.end();
		} finally {
		    context.setState(Arc.FirstPoint);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    } else {
		boolean loopbackFlag = context.getState().getName()
			.equals(Arc.FirstPoint.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.throwOptionException(
			    PluginServices.getText(this, "incorrect_option"), s);
		} finally {
		    context.setState(Arc.FirstPoint);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    }

	    return;
	}

	@Override
	protected void addValue(ArcCADToolContext context, double d) {
	    ArcCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(Arc.FirstPoint.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.throwValueException(
			PluginServices.getText(this, "incorrect_value"), d);
	    } finally {
		context.setState(Arc.FirstPoint);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	@Override
	protected void addPoint(ArcCADToolContext context, double pointX,
		double pointY, InputEvent event) {
	    ArcCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(Arc.FirstPoint.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.throwPointException(
			PluginServices.getText(this, "incorrect_point"),
			pointX, pointY);
	    } finally {
		context.setState(Arc.FirstPoint);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	// -----------------------------------------------------------
	// Inner classse.
	//

	private static final class Arc_FirstPoint extends Arc_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private Arc_FirstPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void Entry(ArcCADToolContext context) {
		ArcCADTool ctxt = context.getOwner();

		ctxt.setQuestion(PluginServices.getText(this,
			"insert_first_point"));
		ctxt.setDescription(new String[] { "cancel" });
		return;
	    }

	    @Override
	    protected void addPoint(ArcCADToolContext context, double pointX,
		    double pointY, InputEvent event) {
		ArcCADTool ctxt = context.getOwner();

		(context.getState()).Exit(context);
		context.clearState();
		try {
		    ctxt.setQuestion(PluginServices.getText(this,
			    "insert_second_point"));
		    ctxt.setDescription(new String[] { "cancel" });
		    ctxt.addPoint(pointX, pointY, event);
		} finally {
		    context.setState(Arc.SecondPoint);
		    (context.getState()).Entry(context);
		}
		return;
	    }

	    // -------------------------------------------------------
	    // Member data.
	    //
	}

	private static final class Arc_SecondPoint extends Arc_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private Arc_SecondPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void addPoint(ArcCADToolContext context, double pointX,
		    double pointY, InputEvent event) {
		ArcCADTool ctxt = context.getOwner();

		(context.getState()).Exit(context);
		context.clearState();
		try {
		    ctxt.setQuestion(PluginServices.getText(this,
			    "insert_last_point"));
		    ctxt.setDescription(new String[] { "cancel" });
		    ctxt.addPoint(pointX, pointY, event);
		} finally {
		    context.setState(Arc.ThirdPoint);
		    (context.getState()).Entry(context);
		}
		return;
	    }

	    // -------------------------------------------------------
	    // Member data.
	    //
	}

	private static final class Arc_ThirdPoint extends Arc_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private Arc_ThirdPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void addPoint(ArcCADToolContext context, double pointX,
		    double pointY, InputEvent event) {
		ArcCADTool ctxt = context.getOwner();

		(context.getState()).Exit(context);
		context.clearState();
		try {
		    ctxt.addPoint(pointX, pointY, event);
		} finally {
		    context.setState(Arc.FirstPoint);
		    (context.getState()).Entry(context);
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
