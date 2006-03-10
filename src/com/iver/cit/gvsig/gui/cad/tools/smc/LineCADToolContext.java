
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.LineCADTool;
import java.awt.event.InputEvent;

public final class LineCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public LineCADToolContext(LineCADTool owner)
    {
        super();

        _owner = owner;
        setState(Line.FirstPoint);
        Line.FirstPoint.Entry(this);
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

    public LineCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((LineCADToolState) _state);
    }

    protected LineCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private LineCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class LineCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected LineCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(LineCADToolContext context) {}
        protected void Exit(LineCADToolContext context) {}

        protected void addOption(LineCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(LineCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void addValue(LineCADToolContext context, double d)
        {
            Default(context);
        }

        protected void Default(LineCADToolContext context)
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

    /* package */ static abstract class Line
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
        /* package */ static Line_Default.Line_FirstPoint FirstPoint;
        /* package */ static Line_Default.Line_SecondPointOrAngle SecondPointOrAngle;
        /* package */ static Line_Default.Line_LenghtOrPoint LenghtOrPoint;
        private static Line_Default Default;

        static
        {
            FirstPoint = new Line_Default.Line_FirstPoint("Line.FirstPoint", 0);
            SecondPointOrAngle = new Line_Default.Line_SecondPointOrAngle("Line.SecondPointOrAngle", 1);
            LenghtOrPoint = new Line_Default.Line_LenghtOrPoint("Line.LenghtOrPoint", 2);
            Default = new Line_Default("Line.Default", -1);
        }

    }

    protected static class Line_Default
        extends LineCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected Line_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(LineCADToolContext context, String s)
        {
            LineCADTool ctxt = context.getOwner();

            if (s.equals("Cancelar"))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        Line.FirstPoint.getName());

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
                    context.setState(Line.FirstPoint);

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


        private static final class Line_FirstPoint
            extends Line_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Line_FirstPoint(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(LineCADToolContext context)
            {
                LineCADTool ctxt = context.getOwner();

                ctxt.setQuestion("LINEA" + "\n" +
		"Insertar primer punto");
                ctxt.setDescription(new String[]{"Cancelar"});
                return;
            }

            protected void addPoint(LineCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                LineCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar segundo punto o angulo");
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Line.SecondPointOrAngle);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Line_SecondPointOrAngle
            extends Line_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Line_SecondPointOrAngle(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(LineCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                LineCADTool ctxt = context.getOwner();

                LineCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar segundo punto o angulo");
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void addValue(LineCADToolContext context, double d)
            {
                LineCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar longitud o punto");
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addValue(d);
                }
                finally
                {
                    context.setState(Line.LenghtOrPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Line_LenghtOrPoint
            extends Line_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Line_LenghtOrPoint(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(LineCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                LineCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar segundo punto o angulo");
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Line.SecondPointOrAngle);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addValue(LineCADToolContext context, double d)
            {
                LineCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar segundo punto o angulo");
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addValue(d);
                }
                finally
                {
                    context.setState(Line.SecondPointOrAngle);
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
