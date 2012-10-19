//
// Vicente Caballero Navarro

package com.iver.cit.gvsig.gui.cad.tools.smc;

import java.awt.event.InputEvent;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.gui.cad.tools.SymmetryCADTool;

public final class SymmetryCADToolContext extends statemap.FSMContext {
    // ---------------------------------------------------------------
    // Member methods.
    //

    public SymmetryCADToolContext(SymmetryCADTool owner) {
	super();

	_owner = owner;
	setState(Symmetry.FirstPoint);
	Symmetry.FirstPoint.Entry(this);
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

    public SymmetryCADToolState getState()
	    throws statemap.StateUndefinedException {
	if (_state == null) {
	    throw (new statemap.StateUndefinedException());
	}

	return ((SymmetryCADToolState) _state);
    }

    protected SymmetryCADTool getOwner() {
	return (_owner);
    }

    // ---------------------------------------------------------------
    // Member data.
    //

    transient private SymmetryCADTool _owner;

    // ---------------------------------------------------------------
    // Inner classes.
    //

    public static abstract class SymmetryCADToolState extends statemap.State {
	// -----------------------------------------------------------
	// Member methods.
	//

	protected SymmetryCADToolState(String name, int id) {
	    super(name, id);
	}

	protected void Entry(SymmetryCADToolContext context) {
	}

	protected void Exit(SymmetryCADToolContext context) {
	}

	protected void addOption(SymmetryCADToolContext context, String s) {
	    Default(context);
	}

	protected void addPoint(SymmetryCADToolContext context, double pointX,
		double pointY, InputEvent event) {
	    Default(context);
	}

	protected void addValue(SymmetryCADToolContext context, double d) {
	    Default(context);
	}

	protected void Default(SymmetryCADToolContext context) {
	    throw (new statemap.TransitionUndefinedException("State: "
		    + context.getState().getName() + ", Transition: "
		    + context.getTransition()));
	}

	// -----------------------------------------------------------
	// Member data.
	//
    }

    /* package */static abstract class Symmetry {
	// -----------------------------------------------------------
	// Member methods.
	//

	// -----------------------------------------------------------
	// Member data.
	//

	// -------------------------------------------------------
	// Statics.
	//
	/* package */static Symmetry_Default.Symmetry_FirstPoint FirstPoint;
	/* package */static Symmetry_Default.Symmetry_SecondPoint SecondPoint;
	/* package */static Symmetry_Default.Symmetry_CutOrCopy CutOrCopy;
	private static Symmetry_Default Default;

	static {
	    FirstPoint = new Symmetry_Default.Symmetry_FirstPoint(
		    "Symmetry.FirstPoint", 0);
	    SecondPoint = new Symmetry_Default.Symmetry_SecondPoint(
		    "Symmetry.SecondPoint", 1);
	    CutOrCopy = new Symmetry_Default.Symmetry_CutOrCopy(
		    "Symmetry.CutOrCopy", 2);
	    Default = new Symmetry_Default("Symmetry.Default", -1);
	}

    }

    protected static class Symmetry_Default extends SymmetryCADToolState {
	// -----------------------------------------------------------
	// Member methods.
	//

	protected Symmetry_Default(String name, int id) {
	    super(name, id);
	}

	@Override
	protected void addOption(SymmetryCADToolContext context, String s) {
	    SymmetryCADTool ctxt = context.getOwner();

	    if (s.equals(PluginServices.getText(this, "cancel"))) {
		boolean loopbackFlag = context.getState().getName()
			.equals(Symmetry.FirstPoint.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.end();
		} finally {
		    context.setState(Symmetry.FirstPoint);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    } else {
		boolean loopbackFlag = context.getState().getName()
			.equals(Symmetry.FirstPoint.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.throwOptionException(
			    PluginServices.getText(this, "incorrect_option"), s);
		} finally {
		    context.setState(Symmetry.FirstPoint);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    }

	    return;
	}

	@Override
	protected void addValue(SymmetryCADToolContext context, double d) {
	    SymmetryCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(Symmetry.FirstPoint.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.throwValueException(
			PluginServices.getText(this, "incorrect_value"), d);
	    } finally {
		context.setState(Symmetry.FirstPoint);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	@Override
	protected void addPoint(SymmetryCADToolContext context, double pointX,
		double pointY, InputEvent event) {
	    SymmetryCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(Symmetry.FirstPoint.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.throwPointException(
			PluginServices.getText(this, "incorrect_point"),
			pointX, pointY);
	    } finally {
		context.setState(Symmetry.FirstPoint);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	// -----------------------------------------------------------
	// Inner classse.
	//

	private static final class Symmetry_FirstPoint extends Symmetry_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private Symmetry_FirstPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void Entry(SymmetryCADToolContext context) {
		SymmetryCADTool ctxt = context.getOwner();

		ctxt.selection();
		ctxt.setQuestion(PluginServices.getText(this,
			"insert_first_point"));
		ctxt.setDescription(new String[] { "cancel" });
		return;
	    }

	    @Override
	    protected void addPoint(SymmetryCADToolContext context,
		    double pointX, double pointY, InputEvent event) {
		SymmetryCADTool ctxt = context.getOwner();

		(context.getState()).Exit(context);
		context.clearState();
		try {
		    ctxt.setQuestion(PluginServices.getText(this,
			    "insert_second_point"));
		    ctxt.setDescription(new String[] { "cancel" });
		    ctxt.addPoint(pointX, pointY, event);
		} finally {
		    context.setState(Symmetry.SecondPoint);
		    (context.getState()).Entry(context);
		}
		return;
	    }

	    // -------------------------------------------------------
	    // Member data.
	    //
	}

	private static final class Symmetry_SecondPoint extends
		Symmetry_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private Symmetry_SecondPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void addPoint(SymmetryCADToolContext context,
		    double pointX, double pointY, InputEvent event) {
		SymmetryCADTool ctxt = context.getOwner();

		(context.getState()).Exit(context);
		context.clearState();
		try {
		    ctxt.setQuestion(PluginServices.getText(this,
			    "del_original_geometries")
			    + " "
			    + PluginServices.getText(this, "yes")
			    + "["
			    + PluginServices.getText(this,
				    "SymmetryCADTool.yes")
			    + "], "
			    + PluginServices.getText(this, "cad.or")
			    + " "
			    + PluginServices.getText(this, "no")
			    + "["
			    + PluginServices
				    .getText(this, "SymmetryCADTool.no") + "]");
		    ctxt.setDescription(new String[] { "cancel", "cut", "copy" });
		    ctxt.addPoint(pointX, pointY, event);
		} finally {
		    context.setState(Symmetry.CutOrCopy);
		    (context.getState()).Entry(context);
		}
		return;
	    }

	    // -------------------------------------------------------
	    // Member data.
	    //
	}

	private static final class Symmetry_CutOrCopy extends Symmetry_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private Symmetry_CutOrCopy(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void addOption(SymmetryCADToolContext context, String s) {
		SymmetryCADTool ctxt = context.getOwner();

		(context.getState()).Exit(context);
		context.clearState();
		try {
		    ctxt.setDescription(new String[] { "cancel" });
		    ctxt.addOption(s);
		    ctxt.end();
		} finally {
		    context.setState(Symmetry.FirstPoint);
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
