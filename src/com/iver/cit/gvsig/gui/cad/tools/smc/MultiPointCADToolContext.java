//
// Vicente Caballero Navarro

package com.iver.cit.gvsig.gui.cad.tools.smc;

import java.awt.event.InputEvent;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.gui.cad.CADStatus;
import com.iver.cit.gvsig.gui.cad.tools.MultiPointCADTool;

public final class MultiPointCADToolContext extends statemap.FSMContext {
    // ---------------------------------------------------------------
    // Member methods.
    //

    public MultiPointCADToolContext(MultiPointCADTool owner) {
	super();

	_owner = owner;
	setState(MultiPoint.FirstPoint);
	MultiPoint.FirstPoint.Entry(this);
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

    public void removePoint(InputEvent event, int numPoints) {
	_transition = "removePoint";
	getState().removePoint(this, event, numPoints);
	_transition = "";
	return;
    }

    public MultiPointCADToolState getState()
	    throws statemap.StateUndefinedException {
	if (_state == null) {
	    throw (new statemap.StateUndefinedException());
	}

	return ((MultiPointCADToolState) _state);
    }

    protected MultiPointCADTool getOwner() {
	return (_owner);
    }

    // ---------------------------------------------------------------
    // Member data.
    //

    transient private MultiPointCADTool _owner;

    // ---------------------------------------------------------------
    // Inner classes.
    //

    public static abstract class MultiPointCADToolState extends statemap.State {
	// -----------------------------------------------------------
	// Member methods.
	//

	protected MultiPointCADToolState(String name, int id) {
	    super(name, id);
	}

	protected void Entry(MultiPointCADToolContext context) {
	}

	protected void Exit(MultiPointCADToolContext context) {
	}

	protected void addOption(MultiPointCADToolContext context, String s) {
	    Default(context);
	}

	protected void addPoint(MultiPointCADToolContext context,
		double pointX, double pointY, InputEvent event) {
	    Default(context);
	}

	protected void addValue(MultiPointCADToolContext context, double d) {
	    Default(context);
	}

	protected void removePoint(MultiPointCADToolContext context,
		InputEvent event, int numPoints) {
	    Default(context);
	}

	protected void Default(MultiPointCADToolContext context) {
	    throw (new statemap.TransitionUndefinedException("State: "
		    + context.getState().getName() + ", Transition: "
		    + context.getTransition()));
	}

	// -----------------------------------------------------------
	// Member data.
	//
    }

    /* package */static abstract class MultiPoint {
	// -----------------------------------------------------------
	// Member methods.
	//

	// -----------------------------------------------------------
	// Member data.
	//

	// -------------------------------------------------------
	// Statics.
	//
	/* package */static MultiPoint_Default.MultiPoint_InsertPoint InsertPoint;
	private static MultiPoint_Default Default;
	private static MultiPoint_Default.MultiPoint_FirstPoint FirstPoint;

	static {
	    InsertPoint = new MultiPoint_Default.MultiPoint_InsertPoint(
		    "MultiPoint.InsertPoint", 0);
	    Default = new MultiPoint_Default("MultiPoint.Default", -1);
	    FirstPoint = new MultiPoint_Default.MultiPoint_FirstPoint(
		    "MultiPoint.FirstPoint", 0);
	}

    }

    protected static class MultiPoint_Default extends MultiPointCADToolState {
	// -----------------------------------------------------------
	// Member methods.
	//

	protected MultiPoint_Default(String name, int id) {
	    super(name, id);
	}

	@Override
	protected void addOption(MultiPointCADToolContext context, String s) {
	    MultiPointCADTool ctxt = context.getOwner();

	    if (s.equals(PluginServices.getText(this, "cancel"))
		    || s.equalsIgnoreCase("c")) {
		boolean loopbackFlag = context.getState().getName()
			.equals(MultiPoint.InsertPoint.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.end();
		} finally {
		    context.setState(MultiPoint.InsertPoint);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    } else {
		boolean loopbackFlag = context.getState().getName()
			.equals(MultiPoint.InsertPoint.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.throwOptionException(
			    PluginServices.getText(this, "incorrect_option"), s);
		} finally {
		    context.setState(MultiPoint.InsertPoint);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    }

	    return;
	}

	@Override
	protected void addValue(MultiPointCADToolContext context, double d) {
	    MultiPointCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(MultiPoint.InsertPoint.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.throwValueException(
			PluginServices.getText(this, "incorrect_value"), d);
	    } finally {
		context.setState(MultiPoint.InsertPoint);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	@Override
	protected void addPoint(MultiPointCADToolContext context,
		double pointX, double pointY, InputEvent event) {
	    MultiPointCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(MultiPoint.InsertPoint.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.throwPointException(
			PluginServices.getText(this, "incorrect_point"),
			pointX, pointY);
	    } finally {
		context.setState(MultiPoint.InsertPoint);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	@Override
	protected void removePoint(MultiPointCADToolContext context,
		InputEvent event, int numPoints) {

	    MultiPointCADTool tool = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(MultiPoint.FirstPoint.getName());

	    if (!loopbackFlag) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		tool.throwNoPointsException(PluginServices.getText(this,
			"no_points"));
	    } finally {
		context.setState(MultiPoint.FirstPoint);
		if (!loopbackFlag) {
		    (context.getState()).Entry(context);
		}
	    }

	}

	// -----------------------------------------------------------
	// Inner classse.
	//

	private static final class MultiPoint_FirstPoint extends
		MultiPoint_Default {

	    private MultiPoint_FirstPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void Entry(MultiPointCADToolContext context) {
		MultiPointCADTool ctxt = context.getOwner();

		ctxt.setQuestion(PluginServices.getText(this, "insert_point"));
		ctxt.setDescription(new String[] { "cancel", });
		return;
	    }

	    @Override
	    protected void addOption(MultiPointCADToolContext context, String s) {

		if (s.equals("espacio")
			|| s.equals(PluginServices.getText(this, "end"))) {

		    MultiPointCADToolState endState = context.getState();
		    MultiPointCADTool tool = context.getOwner();

		    context.clearState();
		    try {
			tool.throwInvalidGeometryException(PluginServices
				.getText(this, "incorrect_geometry"));
		    } finally {
			context.setState(endState);
		    }
		}
	    }

	    @Override
	    protected void addPoint(MultiPointCADToolContext context,
		    double pointX, double pointY, InputEvent event) {

		MultiPointCADTool tool = context.getOwner();

		context.clearState();
		try {
		    boolean deleteButton3 = CADStatus.getCADStatus()
			    .isDeleteButtonActivated();
		    if (deleteButton3) {
			tool.setQuestion(PluginServices.getText(this,
				"insert_next_point_del"));
		    } else {
			tool.setQuestion(PluginServices.getText(this,
				"insert_next_point"));
		    }
		    tool.addPoint(pointX, pointY, event);
		} finally {
		    context.setState(MultiPoint.InsertPoint);
		    (context.getState()).Entry(context);
		}

	    }

	}

	private static final class MultiPoint_InsertPoint extends
		MultiPoint_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private MultiPoint_InsertPoint(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void Entry(MultiPointCADToolContext context) {
		MultiPointCADTool ctxt = context.getOwner();

		ctxt.setDescription(new String[] { "cancel", "end",
			"removePoint" });
		return;
	    }

	    @Override
	    protected void addOption(MultiPointCADToolContext context, String s) {
		MultiPointCADTool ctxt = context.getOwner();

		if (s.equalsIgnoreCase("espacio")
			|| s.equals(PluginServices.getText(this, "end"))) {
		    MultiPointCADToolState endState = context.getState();

		    context.clearState();
		    try {
			ctxt.setQuestion(PluginServices.getText(this,
				"insert_point"));
			ctxt.setDescription(new String[] { "cancel" });
			ctxt.addOption(s);
			ctxt.endGeometry();
			ctxt.fireEndGeometry();
		    } finally {
			context.setState(endState);
		    }
		} else {
		    super.addOption(context, s);
		}

		return;
	    }

	    @Override
	    protected void addPoint(MultiPointCADToolContext context,
		    double pointX, double pointY, InputEvent event) {
		MultiPointCADTool ctxt = context.getOwner();

		MultiPointCADToolState endState = context.getState();

		context.clearState();
		try {
		    boolean deleteButton3 = CADStatus.getCADStatus()
			    .isDeleteButtonActivated();
		    if (deleteButton3) {
			ctxt.setQuestion(PluginServices.getText(this,
				"insert_next_point_del"));
		    } else {
			ctxt.setQuestion(PluginServices.getText(this,
				"insert_next_point"));
		    }
		    ctxt.addPoint(pointX, pointY, event);
		} finally {
		    context.setState(endState);
		}
		return;
	    }

	    @Override
	    protected void removePoint(MultiPointCADToolContext context,
		    InputEvent event, int numPoints) {

		MultiPointCADTool tool = context.getOwner();

		if (numPoints > 1) {
		    MultiPointCADToolState endState = context.getState();

		    context.clearState();
		    try {
			tool.removePoint(event);
		    } finally {
			context.setState(endState);
		    }

		} else if (numPoints == 1) {
		    (context.getState()).Exit(context);
		    context.clearState();
		    try {
			tool.removePoint(event);
		    } finally {
			context.setState(MultiPoint.FirstPoint);
			(context.getState()).Entry(context);
		    }
		} else {
		    super.removePoint(context, event, numPoints);
		}

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
