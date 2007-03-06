
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import java.awt.event.InputEvent;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.gui.cad.tools.EllipseCADTool;

public final class EllipseCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public EllipseCADToolContext(EllipseCADTool owner)
    {
        super();

        _owner = owner;
        setState(Ellipse.FirstPointAxis);
        Ellipse.FirstPointAxis.Entry(this);
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

    public EllipseCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((EllipseCADToolState) _state);
    }

    protected EllipseCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private EllipseCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class EllipseCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected EllipseCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(EllipseCADToolContext context) {}
        protected void Exit(EllipseCADToolContext context) {}

        protected void addOption(EllipseCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(EllipseCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void addValue(EllipseCADToolContext context, double d)
        {
            Default(context);
        }

        protected void Default(EllipseCADToolContext context)
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

    /* package */ static abstract class Ellipse
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
        /* package */ static Ellipse_Default.Ellipse_FirstPointAxis FirstPointAxis;
        /* package */ static Ellipse_Default.Ellipse_SecondPointAxis SecondPointAxis;
        /* package */ static Ellipse_Default.Ellipse_DistanceOtherAxis DistanceOtherAxis;
        private static Ellipse_Default Default;

        static
        {
            FirstPointAxis = new Ellipse_Default.Ellipse_FirstPointAxis("Ellipse.FirstPointAxis", 0);
            SecondPointAxis = new Ellipse_Default.Ellipse_SecondPointAxis("Ellipse.SecondPointAxis", 1);
            DistanceOtherAxis = new Ellipse_Default.Ellipse_DistanceOtherAxis("Ellipse.DistanceOtherAxis", 2);
            Default = new Ellipse_Default("Ellipse.Default", -1);
        }

    }

    protected static class Ellipse_Default
        extends EllipseCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected Ellipse_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(EllipseCADToolContext context, String s)
        {
            EllipseCADTool ctxt = context.getOwner();

            if (s.equals(PluginServices.getText(this,"cancel")))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        Ellipse.FirstPointAxis.getName());

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
                    context.setState(Ellipse.FirstPointAxis);

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
                        Ellipse.FirstPointAxis.getName());

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
                    context.setState(Ellipse.FirstPointAxis);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }

            return;
        }

        protected void addValue(EllipseCADToolContext context, double d)
        {
            EllipseCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Ellipse.FirstPointAxis.getName());

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
                context.setState(Ellipse.FirstPointAxis);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void addPoint(EllipseCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            EllipseCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Ellipse.FirstPointAxis.getName());

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
                context.setState(Ellipse.FirstPointAxis);

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


        private static final class Ellipse_FirstPointAxis
            extends Ellipse_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Ellipse_FirstPointAxis(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(EllipseCADToolContext context)
            {
                EllipseCADTool ctxt = context.getOwner();

                ctxt.setQuestion(PluginServices.getText(this,"insert_initial_point_axis"));
                ctxt.setDescription(new String[]{"cancel"});
                return;
            }

            protected void addPoint(EllipseCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                EllipseCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_last_point_axis"));
                    ctxt.setDescription(new String[]{"cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Ellipse.SecondPointAxis);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Ellipse_SecondPointAxis
            extends Ellipse_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Ellipse_SecondPointAxis(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(EllipseCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                EllipseCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_distance_another_axis"));
                    ctxt.setDescription(new String[]{"cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Ellipse.DistanceOtherAxis);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Ellipse_DistanceOtherAxis
            extends Ellipse_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Ellipse_DistanceOtherAxis(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(EllipseCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                EllipseCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addPoint(pointX, pointY, event);
                    ctxt.end();
                }
                finally
                {
                    context.setState(Ellipse.FirstPointAxis);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addValue(EllipseCADToolContext context, double d)
            {
                EllipseCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addValue(d);
                    ctxt.end();
                }
                finally
                {
                    context.setState(Ellipse.FirstPointAxis);
                    (context.getState()).Entry(context);
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
