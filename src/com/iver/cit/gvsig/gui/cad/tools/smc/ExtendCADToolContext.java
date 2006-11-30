
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.ExtendCADTool;
import java.awt.event.InputEvent;
import com.iver.andami.PluginServices;

public final class ExtendCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public ExtendCADToolContext(ExtendCADTool owner)
    {
        super();

        _owner = owner;
        setState(Extend.SelectGeometryToExtend);
        Extend.SelectGeometryToExtend.Entry(this);
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

    public ExtendCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((ExtendCADToolState) _state);
    }

    protected ExtendCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private ExtendCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class ExtendCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected ExtendCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(ExtendCADToolContext context) {}
        protected void Exit(ExtendCADToolContext context) {}

        protected void addOption(ExtendCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(ExtendCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void addValue(ExtendCADToolContext context, double d)
        {
            Default(context);
        }

        protected void Default(ExtendCADToolContext context)
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

    /* package */ static abstract class Extend
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
        /* package */ static Extend_Default.Extend_SelectGeometryToExtend SelectGeometryToExtend;
        private static Extend_Default Default;

        static
        {
            SelectGeometryToExtend = new Extend_Default.Extend_SelectGeometryToExtend("Extend.SelectGeometryToExtend", 0);
            Default = new Extend_Default("Extend.Default", -1);
        }

    }

    protected static class Extend_Default
        extends ExtendCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected Extend_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(ExtendCADToolContext context, String s)
        {
            ExtendCADTool ctxt = context.getOwner();

            if (s.equals(PluginServices.getText(this,"cancel")))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        Extend.SelectGeometryToExtend.getName());

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
                    context.setState(Extend.SelectGeometryToExtend);

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
                        Extend.SelectGeometryToExtend.getName());

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
                    context.setState(Extend.SelectGeometryToExtend);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }

            return;
        }

        protected void addValue(ExtendCADToolContext context, double d)
        {
            ExtendCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Extend.SelectGeometryToExtend.getName());

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
                context.setState(Extend.SelectGeometryToExtend);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void addPoint(ExtendCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            ExtendCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Extend.SelectGeometryToExtend.getName());

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
                context.setState(Extend.SelectGeometryToExtend);

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


        private static final class Extend_SelectGeometryToExtend
            extends Extend_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Extend_SelectGeometryToExtend(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(ExtendCADToolContext context)
            {
                ExtendCADTool ctxt = context.getOwner();

                ctxt.selection();
                ctxt.setQuestion(PluginServices.getText(this,"select_geometry_to_extend"));
                ctxt.setDescription(new String[]{"cancel"});
                return;
            }

            protected void addPoint(ExtendCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                ExtendCADTool ctxt = context.getOwner();

                ExtendCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"select_geometry_to_extend"));
                    ctxt.setDescription(new String[]{"cancel"});
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
