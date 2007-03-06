
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import java.awt.event.InputEvent;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.gui.cad.tools.InternalPolygonCADTool;

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
        setState(InternalPolygon.AddNextPoint);
        InternalPolygon.AddNextPoint.Entry(this);
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

    public void addValue(double d)
    {
        _transition = "addValue";
        getState().addValue(this, d);
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

        protected void addValue(InternalPolygonCADToolContext context, double d)
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

    /* package */ static abstract class InternalPolygon
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
        /* package */ static InternalPolygon_Default.InternalPolygon_AddNextPoint AddNextPoint;
        private static InternalPolygon_Default Default;

        static
        {
            AddNextPoint = new InternalPolygon_Default.InternalPolygon_AddNextPoint("InternalPolygon.AddNextPoint", 0);
            Default = new InternalPolygon_Default("InternalPolygon.Default", -1);
        }

    }

    protected static class InternalPolygon_Default
        extends InternalPolygonCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected InternalPolygon_Default(String name, int id)
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
                        InternalPolygon.AddNextPoint.getName());

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
                    context.setState(InternalPolygon.AddNextPoint);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }
            else
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        InternalPolygon.AddNextPoint.getName());

                if (loopbackFlag == false)
                {
                    (context.getState()).Exit(context);
                }

                context.clearState();
                try
                {
                    ctxt.throwOptionException(PluginServices.getText(this,"incorrect_option"), s);
                }
                finally
                {
                    context.setState(InternalPolygon.AddNextPoint);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }

            return;
        }

        protected void addValue(InternalPolygonCADToolContext context, double d)
        {
            InternalPolygonCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    InternalPolygon.AddNextPoint.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.clearState();
            try
            {
                ctxt.throwValueException(PluginServices.getText(this,"incorrect_value"), d);
            }
            finally
            {
                context.setState(InternalPolygon.AddNextPoint);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void addPoint(InternalPolygonCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            InternalPolygonCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    InternalPolygon.AddNextPoint.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.clearState();
            try
            {
                ctxt.throwPointException(PluginServices.getText(this,"incorrect_point"), pointX, pointY);
            }
            finally
            {
                context.setState(InternalPolygon.AddNextPoint);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

    //-----------------------------------------------------------
    // Inner classse.
    //


        private static final class InternalPolygon_AddNextPoint
            extends InternalPolygon_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private InternalPolygon_AddNextPoint(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(InternalPolygonCADToolContext context)
            {
                InternalPolygonCADTool ctxt = context.getOwner();

                ctxt.selection();
                ctxt.setQuestion(PluginServices.getText(this,"next_point")+" "+
		    PluginServices.getText(this,"cad.or")+" "+
		    PluginServices.getText(this,"end")+
		    "["+PluginServices.getText(this,"InternalPolygonCADTool.end")+"]");
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
                    ctxt.setQuestion(PluginServices.getText(this,"next_point")+" "+
		    		PluginServices.getText(this,"cad.or")+" "+
		    		"["+PluginServices.getText(this,"InternalPolygonCADTool.end")+"]");
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
                    ctxt.setQuestion(PluginServices.getText(this,"next_point")+" "+
		    		PluginServices.getText(this,"cad.or")+" "+
		    		PluginServices.getText(this,"end")+
		    		"["+PluginServices.getText(this,"InternalPolygonCADTool.end")+"]");
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
