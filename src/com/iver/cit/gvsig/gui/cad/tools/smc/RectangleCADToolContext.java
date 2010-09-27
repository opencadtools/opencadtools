
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import java.awt.event.InputEvent;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.gui.cad.tools.RectangleCADTool;

public final class RectangleCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public RectangleCADToolContext(RectangleCADTool owner)
    {
        super();

        _owner = owner;
        setState(Rectangle.FirstPoint);
        Rectangle.FirstPoint.Entry(this);
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

    public RectangleCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((RectangleCADToolState) _state);
    }

    protected RectangleCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private RectangleCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class RectangleCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected RectangleCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(RectangleCADToolContext context) {}
        protected void Exit(RectangleCADToolContext context) {}

        protected void addOption(RectangleCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(RectangleCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void addValue(RectangleCADToolContext context, double d)
        {
            Default(context);
        }

        protected void Default(RectangleCADToolContext context)
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

    /* package */ static abstract class Rectangle
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
        /* package */ static Rectangle_Default.Rectangle_FirstPoint FirstPoint;
        /* package */ static Rectangle_Default.Rectangle_SecondPointOrSquare SecondPointOrSquare;
        /* package */ static Rectangle_Default.Rectangle_SecondPointSquare SecondPointSquare;
        private static Rectangle_Default Default;

        static
        {
            FirstPoint = new Rectangle_Default.Rectangle_FirstPoint("Rectangle.FirstPoint", 0);
            SecondPointOrSquare = new Rectangle_Default.Rectangle_SecondPointOrSquare("Rectangle.SecondPointOrSquare", 1);
            SecondPointSquare = new Rectangle_Default.Rectangle_SecondPointSquare("Rectangle.SecondPointSquare", 2);
            Default = new Rectangle_Default("Rectangle.Default", -1);
        }

    }

    protected static class Rectangle_Default
        extends RectangleCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected Rectangle_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(RectangleCADToolContext context, String s)
        {
            RectangleCADTool ctxt = context.getOwner();

            if (s.equals(PluginServices.getText(this,"cancel")))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        Rectangle.FirstPoint.getName());

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
                    context.setState(Rectangle.FirstPoint);

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
                        Rectangle.FirstPoint.getName());

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
                    context.setState(Rectangle.FirstPoint);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }

            return;
        }

        protected void addValue(RectangleCADToolContext context, double d)
        {
            RectangleCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Rectangle.FirstPoint.getName());

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
                context.setState(Rectangle.FirstPoint);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void addPoint(RectangleCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            RectangleCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Rectangle.FirstPoint.getName());

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
                context.setState(Rectangle.FirstPoint);

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


        private static final class Rectangle_FirstPoint
            extends Rectangle_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Rectangle_FirstPoint(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(RectangleCADToolContext context)
            {
                RectangleCADTool ctxt = context.getOwner();

                ctxt.setQuestion(PluginServices.getText(this,"insert_first_point_corner"));
                ctxt.setDescription(new String[]{"cancel"});
                return;
            }

            protected void addPoint(RectangleCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                RectangleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_point_corner")+" "+
					PluginServices.getText(this,"cad.or")+" "+
					PluginServices.getText(this,"square")+" "+
					"["+PluginServices.getText(this,"RectangleCADTool.square")+"]");
                    ctxt.setDescription(new String[]{"square", "cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Rectangle.SecondPointOrSquare);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Rectangle_SecondPointOrSquare
            extends Rectangle_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Rectangle_SecondPointOrSquare(String name, int id)
            {
                super (name, id);
            }

            protected void addOption(RectangleCADToolContext context, String s)
            {
                RectangleCADTool ctxt = context.getOwner();

                if (s.equalsIgnoreCase(PluginServices.getText(this,"RectangleCADTool.square")) || s.equals(PluginServices.getText(this,"square")))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_opposited_corner"));
                        ctxt.setDescription(new String[]{"cancel"});
                        ctxt.addOption(s);
                    }
                    finally
                    {
                        context.setState(Rectangle.SecondPointSquare);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {
                    super.addOption(context, s);
                }

                return;
            }

            protected void addPoint(RectangleCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                RectangleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addPoint(pointX, pointY, event);
                    ctxt.fireEndGeometry();
                }
                finally
                {
                    context.setState(Rectangle.FirstPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Rectangle_SecondPointSquare
            extends Rectangle_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Rectangle_SecondPointSquare(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(RectangleCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                RectangleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addPoint(pointX, pointY, event);
                    ctxt.fireEndGeometry();
                }
                finally
                {
                    context.setState(Rectangle.FirstPoint);
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
