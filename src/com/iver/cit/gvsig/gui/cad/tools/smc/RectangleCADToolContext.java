
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

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
        setState(ExecuteMap.Initial);
        ExecuteMap.Initial.Entry(this);
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

        protected void addPoint(RectangleCADToolContext context, double pointX, double pointY)
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

    /* package */ static abstract class ExecuteMap
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
        /* package */ static ExecuteMap_Default.ExecuteMap_Initial Initial;
        /* package */ static ExecuteMap_Default.ExecuteMap_First First;
        /* package */ static ExecuteMap_Default.ExecuteMap_Second Second;
        /* package */ static ExecuteMap_Default.ExecuteMap_Third Third;
        /* package */ static ExecuteMap_Default.ExecuteMap_Fourth Fourth;
        private static ExecuteMap_Default Default;

        static
        {
            Initial = new ExecuteMap_Default.ExecuteMap_Initial("ExecuteMap.Initial", 0);
            First = new ExecuteMap_Default.ExecuteMap_First("ExecuteMap.First", 1);
            Second = new ExecuteMap_Default.ExecuteMap_Second("ExecuteMap.Second", 2);
            Third = new ExecuteMap_Default.ExecuteMap_Third("ExecuteMap.Third", 3);
            Fourth = new ExecuteMap_Default.ExecuteMap_Fourth("ExecuteMap.Fourth", 4);
            Default = new ExecuteMap_Default("ExecuteMap.Default", -1);
        }

    }

    protected static class ExecuteMap_Default
        extends RectangleCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected ExecuteMap_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(RectangleCADToolContext context, String s)
        {
            RectangleCADTool ctxt = context.getOwner();

            if (s.equals("Cancelar"))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        ExecuteMap.Initial.getName());

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
                    context.setState(ExecuteMap.Initial);

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


        private static final class ExecuteMap_Initial
            extends ExecuteMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ExecuteMap_Initial(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(RectangleCADToolContext context)
            {
                RectangleCADTool ctxt = context.getOwner();

                ctxt.setQuestion("RECTANGULO" + "\n" +
		"Insertar primer punto de esquina");
                ctxt.setDescription(new String[]{"Cancelar"});
                return;
            }

            protected void addPoint(RectangleCADToolContext context, double pointX, double pointY)
            {
                RectangleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar punto de esquina opuesta o Cuadrado[C]");
                    ctxt.setDescription(new String[]{"Cuadrado", "Cancelar"});
                    ctxt.addPoint(pointX, pointY);
                }
                finally
                {
                    context.setState(ExecuteMap.First);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ExecuteMap_First
            extends ExecuteMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ExecuteMap_First(String name, int id)
            {
                super (name, id);
            }

            protected void addOption(RectangleCADToolContext context, String s)
            {
                RectangleCADTool ctxt = context.getOwner();

                if (s.equals("c") || s.equals("C") || s.equals("Cuadrado"))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("Insertar esquina opuesta");
                        ctxt.setDescription(new String[]{"Cancelar"});
                        ctxt.addOption(s);
                    }
                    finally
                    {
                        context.setState(ExecuteMap.Second);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {
                    super.addOption(context, s);
                }

                return;
            }

            protected void addPoint(RectangleCADToolContext context, double pointX, double pointY)
            {
                RectangleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addPoint(pointX, pointY);
                    ctxt.end();
                }
                finally
                {
                    context.setState(ExecuteMap.Third);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ExecuteMap_Second
            extends ExecuteMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ExecuteMap_Second(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(RectangleCADToolContext context, double pointX, double pointY)
            {
                RectangleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addPoint(pointX, pointY);
                    ctxt.end();
                }
                finally
                {
                    context.setState(ExecuteMap.Fourth);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ExecuteMap_Third
            extends ExecuteMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ExecuteMap_Third(String name, int id)
            {
                super (name, id);
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ExecuteMap_Fourth
            extends ExecuteMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ExecuteMap_Fourth(String name, int id)
            {
                super (name, id);
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
