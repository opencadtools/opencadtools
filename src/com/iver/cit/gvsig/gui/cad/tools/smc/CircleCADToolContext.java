
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.CircleCADTool;

public final class CircleCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public CircleCADToolContext(CircleCADTool owner)
    {
        super();

        _owner = owner;
        setState(Circle.CenterPointOr3p);
        Circle.CenterPointOr3p.Entry(this);
    }

    public void addOption(String s)
    {
        _transition = "addOption";
        getState().addOption(this, s);
        _transition = "";
        return;
    }

    public void addPoint(double pointX, double pointY)
    {
        _transition = "addPoint";
        getState().addPoint(this, pointX, pointY);
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

    public CircleCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((CircleCADToolState) _state);
    }

    protected CircleCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private CircleCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class CircleCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected CircleCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(CircleCADToolContext context) {}
        protected void Exit(CircleCADToolContext context) {}

        protected void addOption(CircleCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(CircleCADToolContext context, double pointX, double pointY)
        {
            Default(context);
        }

        protected void addValue(CircleCADToolContext context, double d)
        {
            Default(context);
        }

        protected void Default(CircleCADToolContext context)
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

    /* package */ static abstract class Circle
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
        /* package */ static Circle_Default.Circle_CenterPointOr3p CenterPointOr3p;
        /* package */ static Circle_Default.Circle_PointOrRadius PointOrRadius;
        /* package */ static Circle_Default.Circle_SecondPoint SecondPoint;
        /* package */ static Circle_Default.Circle_ThirdPoint ThirdPoint;
        /* package */ static Circle_Default.Circle_FirstPoint FirstPoint;
        private static Circle_Default Default;

        static
        {
            CenterPointOr3p = new Circle_Default.Circle_CenterPointOr3p("Circle.CenterPointOr3p", 0);
            PointOrRadius = new Circle_Default.Circle_PointOrRadius("Circle.PointOrRadius", 1);
            SecondPoint = new Circle_Default.Circle_SecondPoint("Circle.SecondPoint", 2);
            ThirdPoint = new Circle_Default.Circle_ThirdPoint("Circle.ThirdPoint", 3);
            FirstPoint = new Circle_Default.Circle_FirstPoint("Circle.FirstPoint", 4);
            Default = new Circle_Default("Circle.Default", -1);
        }

    }

    protected static class Circle_Default
        extends CircleCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected Circle_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(CircleCADToolContext context, String s)
        {
            CircleCADTool ctxt = context.getOwner();

            if (s.equals("Cancelar"))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        Circle.CenterPointOr3p.getName());

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
                    context.setState(Circle.CenterPointOr3p);

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


        private static final class Circle_CenterPointOr3p
            extends Circle_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Circle_CenterPointOr3p(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(CircleCADToolContext context)
            {
                CircleCADTool ctxt = context.getOwner();

                ctxt.setQuestion("CIRCULO" + "\n" +
		"Insertar punto central o [3P]:");
                ctxt.setDescription(new String[]{"Cancelar", "3P"});
                return;
            }

            protected void addOption(CircleCADToolContext context, String s)
            {
                CircleCADTool ctxt = context.getOwner();

                if (s.equals("3p") || s.equals("3P"))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("Insertar primer punto");
                        ctxt.setDescription(new String[]{"Cancelar"});
                        ctxt.addOption(s);
                    }
                    finally
                    {
                        context.setState(Circle.FirstPoint);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {
                    super.addOption(context, s);
                }

                return;
            }

            protected void addPoint(CircleCADToolContext context, double pointX, double pointY)
            {
                CircleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar radio o segundo punto");
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addPoint(pointX, pointY);
                }
                finally
                {
                    context.setState(Circle.PointOrRadius);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Circle_PointOrRadius
            extends Circle_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Circle_PointOrRadius(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(CircleCADToolContext context, double pointX, double pointY)
            {
                CircleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addPoint(pointX, pointY);
                    ctxt.end();
                }
                finally
                {
                    context.setState(Circle.CenterPointOr3p);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addValue(CircleCADToolContext context, double d)
            {
                CircleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addValue(d);
                    ctxt.end();
                }
                finally
                {
                    context.setState(Circle.CenterPointOr3p);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Circle_SecondPoint
            extends Circle_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Circle_SecondPoint(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(CircleCADToolContext context, double pointX, double pointY)
            {
                CircleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar tercer punto");
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addPoint(pointX, pointY);
                }
                finally
                {
                    context.setState(Circle.ThirdPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Circle_ThirdPoint
            extends Circle_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Circle_ThirdPoint(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(CircleCADToolContext context, double pointX, double pointY)
            {
                CircleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addPoint(pointX, pointY);
                    ctxt.end();
                }
                finally
                {
                    context.setState(Circle.CenterPointOr3p);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Circle_FirstPoint
            extends Circle_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Circle_FirstPoint(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(CircleCADToolContext context, double pointX, double pointY)
            {
                CircleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar segundo punto");
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addPoint(pointX, pointY);
                }
                finally
                {
                    context.setState(Circle.SecondPoint);
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
