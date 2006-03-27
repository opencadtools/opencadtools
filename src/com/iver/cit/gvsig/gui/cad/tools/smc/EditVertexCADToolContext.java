
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.EditVertexCADTool;
import java.awt.event.InputEvent;
import com.iver.andami.PluginServices;

public final class EditVertexCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public EditVertexCADToolContext(EditVertexCADTool owner)
    {
        super();

        _owner = owner;
        setState(EditVertex.SelectVertexOrDelete);
        EditVertex.SelectVertexOrDelete.Entry(this);
    }

    public void addOption(String s)
    {
        _transition = "addOption";
        getState().addOption(this, s);
        _transition = "";
        return;
    }

    public void addPoint(double pointX, double pointY, InputEvent event)
    {
        _transition = "addPoint";
        getState().addPoint(this, pointX, pointY, event);
        _transition = "";
        return;
    }

    public EditVertexCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((EditVertexCADToolState) _state);
    }

    protected EditVertexCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private EditVertexCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class EditVertexCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected EditVertexCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(EditVertexCADToolContext context) {}
        protected void Exit(EditVertexCADToolContext context) {}

        protected void addOption(EditVertexCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(EditVertexCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void Default(EditVertexCADToolContext context)
        {
            throw (
                new statemap.TransitionUndefinedException(
                    "State: " +
                    context.getState().getName() +
                    ", Transition: " +
                    context.getTransition()));
        }

    //-----------------------------------------------------------
    // Member data.
    //
    }

    /* package */ static abstract class EditVertex
    {
    //-----------------------------------------------------------
    // Member methods.
    //

    //-----------------------------------------------------------
    // Member data.
    //

        //-------------------------------------------------------
        // Statics.
        //
        /* package */ static EditVertex_Default.EditVertex_SelectVertexOrDelete SelectVertexOrDelete;
        private static EditVertex_Default Default;

        static
        {
            SelectVertexOrDelete = new EditVertex_Default.EditVertex_SelectVertexOrDelete("EditVertex.SelectVertexOrDelete", 0);
            Default = new EditVertex_Default("EditVertex.Default", -1);
        }

    }

    protected static class EditVertex_Default
        extends EditVertexCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected EditVertex_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(EditVertexCADToolContext context, String s)
        {
            EditVertexCADTool ctxt = context.getOwner();

            if (s.equals(PluginServices.getText(this,"cancel")))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        EditVertex.SelectVertexOrDelete.getName());

                if (loopbackFlag == false)
                {
                    (context.getState()).Exit(context);
                }

                context.clearState();
                try
                {
                    ctxt.end();
                }
                finally
                {
                    context.setState(EditVertex.SelectVertexOrDelete);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }
            else
            {
                super.addOption(context, s);
            }

            return;
        }

    //-----------------------------------------------------------
    // Inner classse.
    //


        private static final class EditVertex_SelectVertexOrDelete
            extends EditVertex_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private EditVertex_SelectVertexOrDelete(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(EditVertexCADToolContext context)
            {
                EditVertexCADTool ctxt = context.getOwner();

                ctxt.selection();
                ctxt.setQuestion(ctxt.getName() + "\n" +
		PluginServices.getText(this,"next_previous_add_del_cancel"));
                ctxt.setDescription(new String[]{"next", "previous", "add", "del", "cancel"});
                return;
            }

            protected void addOption(EditVertexCADToolContext context, String s)
            {
                EditVertexCADTool ctxt = context.getOwner();

                EditVertexCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"next_previous_add_del_cancel"));
                    ctxt.setDescription(new String[]{"next", "previous", "add", "del", "cancel"});
                    ctxt.addOption(s);
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void addPoint(EditVertexCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                EditVertexCADTool ctxt = context.getOwner();

                EditVertexCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"select_from_point"));
                    ctxt.setDescription(new String[]{"next", "previous", "add", "del", "cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

    //-----------------------------------------------------------
    // Member data.
    //
    }
}
