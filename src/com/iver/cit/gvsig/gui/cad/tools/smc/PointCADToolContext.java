
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import java.awt.event.InputEvent;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.gui.cad.tools.PointCADTool;

public final class PointCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public PointCADToolContext(PointCADTool owner)
    {
        super();

        _owner = owner;
        setState(Point.FirstPoint);
        Point.FirstPoint.Entry(this);
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

    public PointCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((PointCADToolState) _state);
    }

    protected PointCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private PointCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class PointCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected PointCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(PointCADToolContext context) {}
        protected void Exit(PointCADToolContext context) {}

        protected void addOption(PointCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(PointCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void addValue(PointCADToolContext context, double d)
        {
            Default(context);
        }

        protected void Default(PointCADToolContext context)
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

    /* package */ static abstract class Point
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
        /* package */ static Point_Default.Point_FirstPoint FirstPoint;
        private static Point_Default Default;

        static
        {
            FirstPoint = new Point_Default.Point_FirstPoint("Point.FirstPoint", 0);
            Default = new Point_Default("Point.Default", -1);
        }

    }

    protected static class Point_Default
        extends PointCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected Point_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(PointCADToolContext context, String s)
        {
            PointCADTool ctxt = context.getOwner();

            if (s.equals(PluginServices.getText(this,"cancel")))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        Point.FirstPoint.getName());

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
                    context.setState(Point.FirstPoint);

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
                        Point.FirstPoint.getName());

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
                    context.setState(Point.FirstPoint);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }

            return;
        }

        protected void addValue(PointCADToolContext context, double d)
        {
            PointCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Point.FirstPoint.getName());

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
                context.setState(Point.FirstPoint);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void addPoint(PointCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            PointCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Point.FirstPoint.getName());

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
                context.setState(Point.FirstPoint);

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


        private static final class Point_FirstPoint
            extends Point_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Point_FirstPoint(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(PointCADToolContext context)
            {
                PointCADTool ctxt = context.getOwner();

                ctxt.setQuestion(PluginServices.getText(this,"define_point"));
                ctxt.setDescription(new String[]{"cancel"});
                return;
            }

            protected void addPoint(PointCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                PointCADTool ctxt = context.getOwner();

                PointCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_point"));
                    ctxt.setDescription(new String[]{"cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                    ctxt.fireEndGeometry();
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
