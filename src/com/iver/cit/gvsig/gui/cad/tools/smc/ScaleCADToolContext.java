
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.ScaleCADTool;

public final class ScaleCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public ScaleCADToolContext(ScaleCADTool owner)
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

    public void addValue(double d)
    {
        _transition = "addValue";
        getState().addValue(this, d);
        _transition = "";
        return;
    }

    public ScaleCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((ScaleCADToolState) _state);
    }

    protected ScaleCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private ScaleCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class ScaleCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected ScaleCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(ScaleCADToolContext context) {}
        protected void Exit(ScaleCADToolContext context) {}

        protected void addOption(ScaleCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(ScaleCADToolContext context, double pointX, double pointY)
        {
            Default(context);
        }

        protected void addValue(ScaleCADToolContext context, double d)
        {
            Default(context);
        }

        protected void Default(ScaleCADToolContext context)
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
        /* package */ static ExecuteMap_Default.ExecuteMap_Fiveth Fiveth;
        /* package */ static ExecuteMap_Default.ExecuteMap_Last Last;
        private static ExecuteMap_Default Default;

        static
        {
            Initial = new ExecuteMap_Default.ExecuteMap_Initial("ExecuteMap.Initial", 0);
            First = new ExecuteMap_Default.ExecuteMap_First("ExecuteMap.First", 1);
            Second = new ExecuteMap_Default.ExecuteMap_Second("ExecuteMap.Second", 2);
            Third = new ExecuteMap_Default.ExecuteMap_Third("ExecuteMap.Third", 3);
            Fourth = new ExecuteMap_Default.ExecuteMap_Fourth("ExecuteMap.Fourth", 4);
            Fiveth = new ExecuteMap_Default.ExecuteMap_Fiveth("ExecuteMap.Fiveth", 5);
            Last = new ExecuteMap_Default.ExecuteMap_Last("ExecuteMap.Last", 6);
            Default = new ExecuteMap_Default("ExecuteMap.Default", -1);
        }

    }

    protected static class ExecuteMap_Default
        extends ScaleCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected ExecuteMap_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(ScaleCADToolContext context, String s)
        {
            ScaleCADTool ctxt = context.getOwner();

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

            protected void Entry(ScaleCADToolContext context)
            {
                ScaleCADTool ctxt = context.getOwner();

                ctxt.selection();
                ctxt.setQuestion("ESCALAR" + "\n" +
		"Precise punto base");
                ctxt.setDescription(new String[]{"Cancelar"});
                return;
            }

            protected void addPoint(ScaleCADToolContext context, double pointX, double pointY)
            {
                ScaleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Precise factor de escala<2> o Referencia[R]");
                    ctxt.setDescription(new String[]{"Referencia", "Cancelar"});
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

            protected void addOption(ScaleCADToolContext context, String s)
            {
                ScaleCADTool ctxt = context.getOwner();

                if (s.equals(null) || s.equals(""))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.addOption(s);
                        ctxt.end();
                        ctxt.refresh();
                    }
                    finally
                    {
                        context.setState(ExecuteMap.Last);
                        (context.getState()).Entry(context);
                    }
                }
                else if (s.equals("R") || s.equals("r") || s.equals("Referencia"))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("Precise punto origen recta referencia o Factor de escala[F]");
                        ctxt.setDescription(new String[]{"Factor escala", "Cancelar"});
                        ctxt.addOption(s);
                    }
                    finally
                    {
                        context.setState(ExecuteMap.Second);
                        (context.getState()).Entry(context);
                    }
                }                else
                {
                    super.addOption(context, s);
                }

                return;
            }

            protected void addPoint(ScaleCADToolContext context, double pointX, double pointY)
            {
                ScaleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addPoint(pointX, pointY);
                    ctxt.end();
                    ctxt.refresh();
                }
                finally
                {
                    context.setState(ExecuteMap.Last);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addValue(ScaleCADToolContext context, double d)
            {
                ScaleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addValue(d);
                    ctxt.end();
                    ctxt.refresh();
                }
                finally
                {
                    context.setState(ExecuteMap.Last);
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

            protected void addOption(ScaleCADToolContext context, String s)
            {
                ScaleCADTool ctxt = context.getOwner();

                if (s.equals("F") || s.equals("f") || s.equals("Factor escala"))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("Precise factor de escala<2> o Referencia[R]");
                        ctxt.setDescription(new String[]{"Referencia", "Cancelar"});
                        ctxt.addOption(s);
                    }
                    finally
                    {
                        context.setState(ExecuteMap.Initial);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {
                    super.addOption(context, s);
                }

                return;
            }

            protected void addPoint(ScaleCADToolContext context, double pointX, double pointY)
            {
                ScaleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Precise punto final recta referencia");
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addPoint(pointX, pointY);
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

            protected void addPoint(ScaleCADToolContext context, double pointX, double pointY)
            {
                ScaleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Precise punto origen recta escala");
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addPoint(pointX, pointY);
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

            protected void addPoint(ScaleCADToolContext context, double pointX, double pointY)
            {
                ScaleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Precise punto final recta escala");
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addPoint(pointX, pointY);
                }
                finally
                {
                    context.setState(ExecuteMap.Fiveth);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ExecuteMap_Fiveth
            extends ExecuteMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ExecuteMap_Fiveth(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(ScaleCADToolContext context, double pointX, double pointY)
            {
                ScaleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addPoint(pointX, pointY);
                    ctxt.end();
                    ctxt.refresh();
                }
                finally
                {
                    context.setState(ExecuteMap.Last);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ExecuteMap_Last
            extends ExecuteMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ExecuteMap_Last(String name, int id)
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
