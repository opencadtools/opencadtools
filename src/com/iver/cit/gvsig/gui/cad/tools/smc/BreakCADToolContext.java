//
// Vicente Caballero Navarro

package com.iver.cit.gvsig.gui.cad.tools.smc;

import java.awt.event.InputEvent;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.gui.cad.tools.BreakCADTool;

public final class BreakCADToolContext extends statemap.FSMContext {
    // ---------------------------------------------------------------
    // Member methods.
    //

    public BreakCADToolContext(BreakCADTool owner) {
	super();

	_owner = owner;
	setState(Break.FirstPoint);
	Break.FirstPoint.Entry(this);
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

    public BreakCADToolState getState() throws statemap.StateUndefinedException {
	if (_state == null) {
	    throw (new statemap.StateUndefinedException());
	}

	return ((BreakCADToolState) _state);
    }

    protected BreakCADTool getOwner() {
	return (_owner);
    }

    // ---------------------------------------------------------------
    // Member data.
    //

    transient private BreakCADTool _owner;

    // ---------------------------------------------------------------
    // Inner classes.
    //

    public static abstract class BreakCADToolState extends statemap.State {
	// -----------------------------------------------------------
	// Member methods.
	//

	protected BreakCADToolState(String name, int id) {
	    super(name, id);
	}

	protected void Entry(BreakCADToolContext context) {
	}

	protected void Exit(BreakCADToolContext context) {
	}

	protected void addOption(BreakCADToolContext context, String s) {
	    Default(context);
	}

	protected void addPoint(BreakCADToolContext context, double pointX,
		double pointY, InputEvent event) {
	    Default(context);
	}

	protected void addValue(BreakCADToolContext context, double d) {
	    Default(context);
	}

	protected void Default(BreakCADToolContext context) {
	    throw (new statemap.TransitionUndefinedException("State: "
		    + context.getState().getName() + ", Transition: "
		    + context.getTransition()));
	}

	// -----------------------------------------------------------
	// Member data.
	//
    }

    /* package */static abstract class Break {
	// -----------------------------------------------------------
	// Member methods.
	//

	// -----------------------------------------------------------
	// Member data.
	//

	// -------------------------------------------------------
	// Statics.
	//
	/* package */static Break_Default.Break_FirstPoint FirstPoint;
	/* package */static Break_Default.Break_SecondPoint SecondPoint;
	private static Break_Default Default;

	static {
	    FirstPoint = new Break_Default.Break_FirstPoint("Break.FirstPoint",
		    0);
	    SecondPoint = new Break_Default.Break_SecondPoint(
		    "Break.SecondPoint", 1);
	    Default = new Break_Default("Break.Default", -1);
	}

    }

    protected static class Break_Default extends BreakCADToolState {
	// -----------------------------------------------------------
	// Member methods.
	//

	protected Break_Default(String name, int id) {
	    super(name, id);
	}

	@Override
	protected void addOption(BreakCADToolContext context, String s) {
	    BreakCADTool ctxt = context.getOwner();

	    if (s.equals(PluginServices.getText(this, "cancel"))) {
		boolean loopbackFlag = context.getState().getName()
			.equals(Break.FirstPoint.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.end();
		} finally {
		    context.setState(Break.FirstPoint);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    } else {
		boolean loopbackFlag = context.getState().getName()
			.equals(Break.FirstPoint.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.throwOptionException(
			    PluginServices.getText(this, "incorrect_option"), s);
		} finally {
		    context.setState(Break.FirstPoint);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    }

	    return;
	}

	@Override
	protected void addValue(BreakCADToolContext context, double d) {
	    BreakCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(Break.FirstPoint.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.throwValueException(
			PluginServices.getText(this, "incorrect_value"), d);
	    } finally {
		context.setState(Break.FirstPoint);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	@Override
	protected void addPoint(BreakCADToolContext context, double pointX,
		double pointY, InputEvent event) {
	    BreakCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(Break.FirstPoint.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.throwPointException(
			PluginServices.getText(this, "incorrect_point"),
			pointX, pointY);
	    } finally {
		context.setState(Break.FirstPoint);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	// -----------------------------------------------------------
	// Inner classse.
	//

	private static final class Break_FirstPoint extends Break_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private Break_FirstPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void Entry(BreakCADToolContext context) {
		BreakCADTool ctxt = context.getOwner();

		ctxt.selection();
		ctxt.setQuestion(PluginServices.getText(this,
			"insert_first_point"));
		ctxt.setDescription(new String[] { "cancel" });
		return;
	    }

	    @Override
	    protected void addOption(BreakCADToolContext context, String s) {
		BreakCADTool ctxt = context.getOwner();

		BreakCADToolState endState = context.getState();

		context.clearState();
		try {
		    ctxt.setQuestion(PluginServices.getText(this,
			    "insert_first_point"));
		    ctxt.setDescription(new String[] { "cancel" });
		    ctxt.addOption(s);
		} finally {
		    context.setState(endState);
		}
		return;
	    }

	    @Override
	    protected void addPoint(BreakCADToolContext context, double pointX,
		    double pointY, InputEvent event) {
		BreakCADTool ctxt = context.getOwner();

		if (ctxt.intersects(pointX, pointY)) {

		    (context.getState()).Exit(context);
		    context.clearState();
		    try {
			ctxt.setQuestion(PluginServices.getText(this,
				"insert_second_point"));
			ctxt.setDescription(new String[] { "cancel" });
			ctxt.addPoint(pointX, pointY, event);
		    } finally {
			context.setState(Break.SecondPoint);
			(context.getState()).Entry(context);
		    }
		} else if (!ctxt.intersects(pointX, pointY)) {
		    BreakCADToolState endState = context.getState();

		    context.clearState();
		    try {
			ctxt.setQuestion(PluginServices.getText(this,
				"insert_first_point"));
			ctxt.setDescription(new String[] { "cancel" });
		    } finally {
			context.setState(endState);
		    }
		} else {
		    super.addPoint(context, pointX, pointY, event);
		}

		return;
	    }

	    // -------------------------------------------------------
	    // Member data.
	    //
	}

	private static final class Break_SecondPoint extends Break_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private Break_SecondPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void addOption(BreakCADToolContext context, String s) {
		BreakCADTool ctxt = context.getOwner();

		(context.getState()).Exit(context);
		context.clearState();
		try {
		    ctxt.setQuestion(PluginServices.getText(this,
			    "insert_first_point"));
		    ctxt.setDescription(new String[] { "cancel" });
		    ctxt.addOption(s);
		} finally {
		    context.setState(Break.FirstPoint);
		    (context.getState()).Entry(context);
		}
		return;
	    }

	    @Override
	    protected void addPoint(BreakCADToolContext context, double pointX,
		    double pointY, InputEvent event) {
		BreakCADTool ctxt = context.getOwner();

		if (ctxt.intersects(pointX, pointY)) {

		    (context.getState()).Exit(context);
		    context.clearState();
		    try {
			ctxt.setQuestion(PluginServices.getText(this,
				"insert_first_point"));
			ctxt.setDescription(new String[] { "cancel" });
			ctxt.addPoint(pointX, pointY, event);
			ctxt.end();
		    } finally {
			context.setState(Break.FirstPoint);
			(context.getState()).Entry(context);
		    }
		} else if (!ctxt.intersects(pointX, pointY)) {
		    BreakCADToolState endState = context.getState();

		    context.clearState();
		    try {
			ctxt.setQuestion(PluginServices.getText(this,
				"insert_second_point"));
			ctxt.setDescription(new String[] { "cancel" });
		    } finally {
			context.setState(endState);
		    }
		} else {
		    super.addPoint(context, pointX, pointY, event);
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
