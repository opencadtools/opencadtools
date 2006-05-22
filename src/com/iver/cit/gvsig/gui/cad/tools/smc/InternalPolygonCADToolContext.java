
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.InternalPolygonCADTool;
import java.awt.event.InputEvent;
import com.iver.andami.PluginServices;

public final class InternalPolygonCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public InternalPolygonCADToolContext(InternalPolygonCADTool owner)
    {
        super();

        _owner = owner;
        setState(AddInternalPolygon.AddNextPoint);
        AddInternalPolygon.AddNextPoint.Entry(this);
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

    public InternalPolygonCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((InternalPolygonCADToolState) _state);
    }

    protected InternalPolygonCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private InternalPolygonCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class InternalPolygonCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected InternalPolygonCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(InternalPolygonCADToolContext context) {}
        protected void Exit(InternalPolygonCADToolContext context) {}

        protected void addOption(InternalPolygonCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(InternalPolygonCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void Default(InternalPolygonCADToolContext context)
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

    /* package */ static abstract class AddInternalPolygon
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
        /* package */ static AddInternalPolygon_Default.AddInternalPolygon_AddNextPoint AddNextPoint;
        private static AddInternalPolygon_Default Default;

        static
        {
            AddNextPoint = new AddInternalPolygon_Default.AddInternalPolygon_AddNextPoint("AddInternalPolygon.AddNextPoint", 0);
            Default = new AddInternalPolygon_Default("AddInternalPolygon.Default", -1);
        }

    }

    protected static class AddInternalPolygon_Default
        extends InternalPolygonCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected AddInternalPolygon_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(InternalPolygonCADToolContext context, String s)
        {
            InternalPolygonCADTool ctxt = context.getOwner();

            if (s.equals(PluginServices.getText(this,"cancel")))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        AddInternalPolygon.AddNextPoint.getName());

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
                    context.setState(AddInternalPolygon.AddNextPoint);

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


        private static final class AddInternalPolygon_AddNextPoint
            extends AddInternalPolygon_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private AddInternalPolygon_AddNextPoint(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(InternalPolygonCADToolContext context)
            {
                InternalPolygonCADTool ctxt = context.getOwner();

                ctxt.selection();
                ctxt.setQuestion(PluginServices.getText(this,"next_point_cancel_or_end"));
                ctxt.setDescription(new String[]{"end", "cancel"});
                return;
            }

            protected void addOption(InternalPolygonCADToolContext context, String s)
            {
                InternalPolygonCADTool ctxt = context.getOwner();

                InternalPolygonCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"next_point_cancel_or_end"));
                    ctxt.setDescription(new String[]{"end", "cancel"});
                    ctxt.addOption(s);
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void addPoint(InternalPolygonCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                InternalPolygonCADTool ctxt = context.getOwner();

                InternalPolygonCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"next_point_cancel_or_end"));
                    ctxt.setDescription(new String[]{"end", "cancel"});
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
