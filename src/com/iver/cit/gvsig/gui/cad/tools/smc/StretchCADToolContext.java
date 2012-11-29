//
// Vicente Caballero Navarro

package com.iver.cit.gvsig.gui.cad.tools.smc;

import java.awt.event.InputEvent;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.gui.cad.tools.StretchCADTool;

public final class StretchCADToolContext extends statemap.FSMContext {
    // ---------------------------------------------------------------
    // Member methods.
    //

    public StretchCADToolContext(StretchCADTool owner) {
	super();

	_owner = owner;
	setState(Stretch.SelFirstPoint);
	Stretch.SelFirstPoint.Entry(this);
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

    public StretchCADToolState getState()
	    throws statemap.StateUndefinedException {
	if (_state == null) {
	    throw (new statemap.StateUndefinedException());
	}

	return ((StretchCADToolState) _state);
    }

    protected StretchCADTool getOwner() {
	return (_owner);
    }

    // ---------------------------------------------------------------
    // Member data.
    //

    transient private StretchCADTool _owner;

    // ---------------------------------------------------------------
    // Inner classes.
    //

    public static abstract class StretchCADToolState extends statemap.State {
	// -----------------------------------------------------------
	// Member methods.
	//

	protected StretchCADToolState(String name, int id) {
	    super(name, id);
	}

	protected void Entry(StretchCADToolContext context) {
	}

	protected void Exit(StretchCADToolContext context) {
	}

	protected void addOption(StretchCADToolContext context, String s) {
	    Default(context);
	}

	protected void addPoint(StretchCADToolContext context, double pointX,
		double pointY, InputEvent event) {
	    Default(context);
	}

	protected void addValue(StretchCADToolContext context, double d) {
	    Default(context);
	}

	protected void Default(StretchCADToolContext context) {
	    throw (new statemap.TransitionUndefinedException("State: "
		    + context.getState().getName() + ", Transition: "
		    + context.getTransition()));
	}

	// -----------------------------------------------------------
	// Member data.
	//
    }

    /* package */static abstract class Stretch {
	// -----------------------------------------------------------
	// Member methods.
	//

	// -----------------------------------------------------------
	// Member data.
	//

	// -------------------------------------------------------
	// Statics.
	//
	/* package */static Stretch_Default.Stretch_SelFirstPoint SelFirstPoint;
	/* package */static Stretch_Default.Stretch_SelLastPoint SelLastPoint;
	/* package */static Stretch_Default.Stretch_MoveFirstPoint MoveFirstPoint;
	/* package */static Stretch_Default.Stretch_MoveLastPoint MoveLastPoint;
	private static Stretch_Default Default;

	static {
	    SelFirstPoint = new Stretch_Default.Stretch_SelFirstPoint(
		    "Stretch.SelFirstPoint", 0);
	    SelLastPoint = new Stretch_Default.Stretch_SelLastPoint(
		    "Stretch.SelLastPoint", 1);
	    MoveFirstPoint = new Stretch_Default.Stretch_MoveFirstPoint(
		    "Stretch.MoveFirstPoint", 2);
	    MoveLastPoint = new Stretch_Default.Stretch_MoveLastPoint(
		    "Stretch.MoveLastPoint", 3);
	    Default = new Stretch_Default("Stretch.Default", -1);
	}

    }

    protected static class Stretch_Default extends StretchCADToolState {
	// -----------------------------------------------------------
	// Member methods.
	//

	protected Stretch_Default(String name, int id) {
	    super(name, id);
	}

	@Override
	protected void addOption(StretchCADToolContext context, String s) {
	    StretchCADTool ctxt = context.getOwner();

	    if (s.equals(PluginServices.getText(this, "cancel"))) {
		boolean loopbackFlag = context.getState().getName()
			.equals(Stretch.SelFirstPoint.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.end();
		} finally {
		    context.setState(Stretch.SelFirstPoint);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    } else {
		boolean loopbackFlag = context.getState().getName()
			.equals(Stretch.SelFirstPoint.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.throwOptionException(
			    PluginServices.getText(this, "incorrect_option"), s);
		} finally {
		    context.setState(Stretch.SelFirstPoint);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    }

	    return;
	}

	@Override
	protected void addValue(StretchCADToolContext context, double d) {
	    StretchCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(Stretch.SelFirstPoint.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.throwValueException(
			PluginServices.getText(this, "incorrect_value"), d);
	    } finally {
		context.setState(Stretch.SelFirstPoint);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	@Override
	protected void addPoint(StretchCADToolContext context, double pointX,
		double pointY, InputEvent event) {
	    StretchCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(Stretch.SelFirstPoint.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.throwPointException(
			PluginServices.getText(this, "incorrect_point"),
			pointX, pointY);
	    } finally {
		context.setState(Stretch.SelFirstPoint);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	// -----------------------------------------------------------
	// Inner classse.
	//

	private static final class Stretch_SelFirstPoint extends
		Stretch_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private Stretch_SelFirstPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void Entry(StretchCADToolContext context) {
		StretchCADTool ctxt = context.getOwner();

		ctxt.selection();
		ctxt.setQuestion(PluginServices.getText(this,
			"insert_selection_point"));
		ctxt.setDescription(new String[] { "cancel" });
		return;
	    }

	    @Override
	    protected void addPoint(StretchCADToolContext context,
		    double pointX, double pointY, InputEvent event) {
		StretchCADTool ctxt = context.getOwner();

		(context.getState()).Exit(context);
		context.clearState();
		try {
		    ctxt.setQuestion(PluginServices.getText(this,
			    "insert_selection_last_point"));
		    ctxt.setDescription(new String[] { "cancel" });
		    ctxt.addPoint(pointX, pointY, event);
		} finally {
		    context.setState(Stretch.SelLastPoint);
		    (context.getState()).Entry(context);
		}
		return;
	    }

	    // -------------------------------------------------------
	    // Member data.
	    //
	}

	private static final class Stretch_SelLastPoint extends Stretch_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private Stretch_SelLastPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void addPoint(StretchCADToolContext context,
		    double pointX, double pointY, InputEvent event) {
		StretchCADTool ctxt = context.getOwner();

		(context.getState()).Exit(context);
		context.clearState();
		try {
		    ctxt.setQuestion(PluginServices.getText(this,
			    "insert_move_first_point"));
		    ctxt.setDescription(new String[] { "cancel" });
		    ctxt.addPoint(pointX, pointY, event);
		} finally {
		    context.setState(Stretch.MoveFirstPoint);
		    (context.getState()).Entry(context);
		}
		return;
	    }

	    // -------------------------------------------------------
	    // Member data.
	    //
	}

	private static final class Stretch_MoveFirstPoint extends
		Stretch_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private Stretch_MoveFirstPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void addPoint(StretchCADToolContext context,
		    double pointX, double pointY, InputEvent event) {
		StretchCADTool ctxt = context.getOwner();

		(context.getState()).Exit(context);
		context.clearState();
		try {
		    ctxt.setQuestion(PluginServices.getText(this,
			    "insert_move_last_point"));
		    ctxt.setDescription(new String[] { "cancel" });
		    ctxt.addPoint(pointX, pointY, event);
		} finally {
		    context.setState(Stretch.MoveLastPoint);
		    (context.getState()).Entry(context);
		}
		return;
	    }

	    // -------------------------------------------------------
	    // Member data.
	    //
	}

	private static final class Stretch_MoveLastPoint extends
		Stretch_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private Stretch_MoveLastPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void addPoint(StretchCADToolContext context,
		    double pointX, double pointY, InputEvent event) {
		StretchCADTool ctxt = context.getOwner();

		(context.getState()).Exit(context);
		context.clearState();
		try {
		    ctxt.setQuestion(PluginServices.getText(this,
			    "insert_selection_point"));
		    ctxt.setDescription(new String[] { "cancel" });
		    ctxt.addPoint(pointX, pointY, event);
		    ctxt.end();
		} finally {
		    context.setState(Stretch.SelFirstPoint);
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
