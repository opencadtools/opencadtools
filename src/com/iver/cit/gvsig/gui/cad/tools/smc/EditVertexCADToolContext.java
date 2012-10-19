//
// Vicente Caballero Navarro

package com.iver.cit.gvsig.gui.cad.tools.smc;

import java.awt.event.InputEvent;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.gui.cad.tools.EditVertexCADTool;

public final class EditVertexCADToolContext extends statemap.FSMContext {
    // ---------------------------------------------------------------
    // Member methods.
    //

    public EditVertexCADToolContext(EditVertexCADTool owner) {
	super();

	_owner = owner;
	setState(EditVertex.SelectVertexOrDelete);
	EditVertex.SelectVertexOrDelete.Entry(this);
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

    public EditVertexCADToolState getState()
	    throws statemap.StateUndefinedException {
	if (_state == null) {
	    throw (new statemap.StateUndefinedException());
	}

	return ((EditVertexCADToolState) _state);
    }

    protected EditVertexCADTool getOwner() {
	return (_owner);
    }

    // ---------------------------------------------------------------
    // Member data.
    //

    transient private EditVertexCADTool _owner;

    // ---------------------------------------------------------------
    // Inner classes.
    //

    public static abstract class EditVertexCADToolState extends statemap.State {
	// -----------------------------------------------------------
	// Member methods.
	//

	protected EditVertexCADToolState(String name, int id) {
	    super(name, id);
	}

	protected void Entry(EditVertexCADToolContext context) {
	}

	protected void Exit(EditVertexCADToolContext context) {
	}

	protected void addOption(EditVertexCADToolContext context, String s) {
	    Default(context);
	}

	protected void addPoint(EditVertexCADToolContext context,
		double pointX, double pointY, InputEvent event) {
	    Default(context);
	}

	protected void addValue(EditVertexCADToolContext context, double d) {
	    Default(context);
	}

	protected void Default(EditVertexCADToolContext context) {
	    throw (new statemap.TransitionUndefinedException("State: "
		    + context.getState().getName() + ", Transition: "
		    + context.getTransition()));
	}

	// -----------------------------------------------------------
	// Member data.
	//
    }

    /* package */static abstract class EditVertex {
	// -----------------------------------------------------------
	// Member methods.
	//

	// -----------------------------------------------------------
	// Member data.
	//

	// -------------------------------------------------------
	// Statics.
	//
	/* package */static EditVertex_Default.EditVertex_SelectVertexOrDelete SelectVertexOrDelete;
	/* package */static EditVertex_Default.EditVertex_AddVertex AddVertex;
	private static EditVertex_Default Default;

	static {
	    SelectVertexOrDelete = new EditVertex_Default.EditVertex_SelectVertexOrDelete(
		    "EditVertex.SelectVertexOrDelete", 0);
	    AddVertex = new EditVertex_Default.EditVertex_AddVertex(
		    "EditVertex.AddVertex", 1);
	    Default = new EditVertex_Default("EditVertex.Default", -1);
	}

    }

    protected static class EditVertex_Default extends EditVertexCADToolState {
	// -----------------------------------------------------------
	// Member methods.
	//

	protected EditVertex_Default(String name, int id) {
	    super(name, id);
	}

	@Override
	protected void addOption(EditVertexCADToolContext context, String s) {
	    EditVertexCADTool ctxt = context.getOwner();

	    if (s.equals(PluginServices.getText(this, "cancel"))) {
		boolean loopbackFlag = context.getState().getName()
			.equals(EditVertex.SelectVertexOrDelete.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.end();
		} finally {
		    context.setState(EditVertex.SelectVertexOrDelete);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    } else {
		boolean loopbackFlag = context.getState().getName()
			.equals(EditVertex.SelectVertexOrDelete.getName());

		if (loopbackFlag == false) {
		    (context.getState()).Exit(context);
		}

		context.clearState();
		try {
		    ctxt.throwOptionException(
			    PluginServices.getText(this, "incorrect_option"), s);
		} finally {
		    context.setState(EditVertex.SelectVertexOrDelete);

		    if (loopbackFlag == false) {
			(context.getState()).Entry(context);
		    }

		}
	    }

	    return;
	}

	@Override
	protected void addValue(EditVertexCADToolContext context, double d) {
	    EditVertexCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(EditVertex.SelectVertexOrDelete.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.throwValueException(
			PluginServices.getText(this, "incorrect_value"), d);
	    } finally {
		context.setState(EditVertex.SelectVertexOrDelete);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	@Override
	protected void addPoint(EditVertexCADToolContext context,
		double pointX, double pointY, InputEvent event) {
	    EditVertexCADTool ctxt = context.getOwner();

	    boolean loopbackFlag = context.getState().getName()
		    .equals(EditVertex.SelectVertexOrDelete.getName());

	    if (loopbackFlag == false) {
		(context.getState()).Exit(context);
	    }

	    context.clearState();
	    try {
		ctxt.throwPointException(
			PluginServices.getText(this, "incorrect_point"),
			pointX, pointY);
	    } finally {
		context.setState(EditVertex.SelectVertexOrDelete);

		if (loopbackFlag == false) {
		    (context.getState()).Entry(context);
		}

	    }
	    return;
	}

	// -----------------------------------------------------------
	// Inner classse.
	//

	private static final class EditVertex_SelectVertexOrDelete extends
		EditVertex_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private EditVertex_SelectVertexOrDelete(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void Entry(EditVertexCADToolContext context) {
		EditVertexCADTool ctxt = context.getOwner();

		ctxt.selection();
		ctxt.setQuestion(PluginServices.getText(this, "point")
			+ ", "
			+ PluginServices.getText(this, "next")
			+ "["
			+ PluginServices.getText(this,
				"EditVertexCADTool.nextvertex")
			+ "], "
			+ PluginServices.getText(this, "previous")
			+ "["
			+ PluginServices.getText(this,
				"EditVertexCADTool.previousvertex")
			+ "], "
			+ PluginServices.getText(this, "add")
			+ "["
			+ PluginServices.getText(this,
				"EditVertexCADTool.addvertex")
			+ "] "
			+ PluginServices.getText(this, "cad.or")
			+ " "
			+ PluginServices.getText(this, "del")
			+ "["
			+ PluginServices.getText(this,
				"EditVertexCADTool.delvertex") + "]");
		ctxt.setDescription(new String[] { "next", "previous", "add",
			"del", "cancel" });
		return;
	    }

	    @Override
	    protected void addOption(EditVertexCADToolContext context, String s) {
		EditVertexCADTool ctxt = context.getOwner();

		if (s.equals("i") || s.equals("I")
			|| s.equals(PluginServices.getText(this, "add"))) {

		    (context.getState()).Exit(context);
		    context.clearState();
		    try {
			ctxt.setQuestion(PluginServices.getText(this,
				"add_vertex"));
			ctxt.setDescription(new String[] { "cancel" });
			ctxt.addOption(s);
		    } finally {
			context.setState(EditVertex.AddVertex);
			(context.getState()).Entry(context);
		    }
		} else if (!s.equals("i") && !s.equals("I")
			&& !s.equals(PluginServices.getText(this, "add"))) {
		    EditVertexCADToolState endState = context.getState();

		    context.clearState();
		    try {
			ctxt.setQuestion(PluginServices.getText(this, "point")
				+ ", "
				+ PluginServices.getText(this, "next")
				+ "["
				+ PluginServices.getText(this,
					"EditVertexCADTool.nextvertex")
				+ "], "
				+ PluginServices.getText(this, "previous")
				+ "["
				+ PluginServices.getText(this,
					"EditVertexCADTool.previousvertex")
				+ "], "
				+ PluginServices.getText(this, "add")
				+ "["
				+ PluginServices.getText(this,
					"EditVertexCADTool.addvertex")
				+ "] "
				+ PluginServices.getText(this, "cad.or")
				+ " "
				+ PluginServices.getText(this, "del")
				+ "["
				+ PluginServices.getText(this,
					"EditVertexCADTool.delvertex") + "]");
			ctxt.setDescription(new String[] { "next", "previous",
				"add", "del", "cancel" });
			ctxt.addOption(s);
		    } finally {
			context.setState(endState);
		    }
		} else {
		    super.addOption(context, s);
		}

		return;
	    }

	    @Override
	    protected void addPoint(EditVertexCADToolContext context,
		    double pointX, double pointY, InputEvent event) {
		EditVertexCADTool ctxt = context.getOwner();

		EditVertexCADToolState endState = context.getState();

		context.clearState();
		try {
		    ctxt.setQuestion(PluginServices.getText(this,
			    "select_from_point"));
		    ctxt.setDescription(new String[] { "next", "previous",
			    "add", "del", "cancel" });
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

	private static final class EditVertex_AddVertex extends
		EditVertex_Default {
	    // -------------------------------------------------------
	    // Member methods.
	    //

	    private EditVertex_AddVertex(String name, int id) {
		super(name, id);
	    }

	    @Override
	    protected void addPoint(EditVertexCADToolContext context,
		    double pointX, double pointY, InputEvent event) {
		EditVertexCADTool ctxt = context.getOwner();

		(context.getState()).Exit(context);
		context.clearState();
		try {
		    ctxt.setQuestion(PluginServices.getText(this,
			    "select_from_point"));
		    ctxt.setDescription(new String[] { "next", "previous",
			    "add", "del", "cancel" });
		    ctxt.addPoint(pointX, pointY, event);
		} finally {
		    context.setState(EditVertex.SelectVertexOrDelete);
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
