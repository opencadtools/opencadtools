
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.EquidistanceCADTool;
import java.awt.event.InputEvent;
import com.iver.andami.PluginServices;

public final class EquidistanceCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public EquidistanceCADToolContext(EquidistanceCADTool owner)
    {
        super();

        _owner = owner;
        setState(Equidistance.Distance);
        Equidistance.Distance.Entry(this);
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

    public EquidistanceCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((EquidistanceCADToolState) _state);
    }

    protected EquidistanceCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private EquidistanceCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class EquidistanceCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected EquidistanceCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(EquidistanceCADToolContext context) {}
        protected void Exit(EquidistanceCADToolContext context) {}

        protected void addOption(EquidistanceCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(EquidistanceCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void addValue(EquidistanceCADToolContext context, double d)
        {
            Default(context);
        }

        protected void Default(EquidistanceCADToolContext context)
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

    /* package */ static abstract class Equidistance
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
        /* package */ static Equidistance_Default.Equidistance_Distance Distance;
        /* package */ static Equidistance_Default.Equidistance_SecondPointDistance SecondPointDistance;
        /* package */ static Equidistance_Default.Equidistance_Position Position;
        private static Equidistance_Default Default;

        static
        {
            Distance = new Equidistance_Default.Equidistance_Distance("Equidistance.Distance", 0);
            SecondPointDistance = new Equidistance_Default.Equidistance_SecondPointDistance("Equidistance.SecondPointDistance", 1);
            Position = new Equidistance_Default.Equidistance_Position("Equidistance.Position", 2);
            Default = new Equidistance_Default("Equidistance.Default", -1);
        }

    }

    protected static class Equidistance_Default
        extends EquidistanceCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected Equidistance_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(EquidistanceCADToolContext context, String s)
        {
            EquidistanceCADTool ctxt = context.getOwner();

            if (s.equals(PluginServices.getText(this,"cancel")))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        Equidistance.Distance.getName());

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
                    context.setState(Equidistance.Distance);

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
                        Equidistance.Distance.getName());

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
                    context.setState(Equidistance.Distance);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }

            return;
        }

        protected void addValue(EquidistanceCADToolContext context, double d)
        {
            EquidistanceCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Equidistance.Distance.getName());

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
                context.setState(Equidistance.Distance);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void addPoint(EquidistanceCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            EquidistanceCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Equidistance.Distance.getName());

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
                context.setState(Equidistance.Distance);

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


        private static final class Equidistance_Distance
            extends Equidistance_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Equidistance_Distance(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(EquidistanceCADToolContext context)
            {
                EquidistanceCADTool ctxt = context.getOwner();

                ctxt.selection();
                ctxt.setQuestion(PluginServices.getText(this,"insert_first_point_or_distance"));
                ctxt.setDescription(new String[]{"cancel"});
                return;
            }

            protected void addPoint(EquidistanceCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                EquidistanceCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_second_point"));
                    ctxt.setDescription(new String[]{"cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Equidistance.SecondPointDistance);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addValue(EquidistanceCADToolContext context, double d)
            {
                EquidistanceCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"position"));
                    ctxt.setDescription(new String[]{"cancel"});
                    ctxt.addValue(d);
                }
                finally
                {
                    context.setState(Equidistance.Position);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Equidistance_SecondPointDistance
            extends Equidistance_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Equidistance_SecondPointDistance(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(EquidistanceCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                EquidistanceCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"position"));
                    ctxt.setDescription(new String[]{"cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Equidistance.Position);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Equidistance_Position
            extends Equidistance_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Equidistance_Position(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(EquidistanceCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                EquidistanceCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setDescription(new String[]{"cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                    ctxt.end();
                }
                finally
                {
                    context.setState(Equidistance.Distance);
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
