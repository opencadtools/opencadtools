
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.PolylineCADTool;

public final class PolylineCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public PolylineCADToolContext(PolylineCADTool owner)
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

    public PolylineCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((PolylineCADToolState) _state);
    }

    protected PolylineCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private PolylineCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class PolylineCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected PolylineCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(PolylineCADToolContext context) {}
        protected void Exit(PolylineCADToolContext context) {}

        protected void addOption(PolylineCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(PolylineCADToolContext context, double pointX, double pointY)
        {
            Default(context);
        }

        protected void Default(PolylineCADToolContext context)
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
        extends PolylineCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected ExecuteMap_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(PolylineCADToolContext context, String s)
        {
            PolylineCADTool ctxt = context.getOwner();

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
                    ctxt.cancel();
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

            protected void Entry(PolylineCADToolContext context)
            {
                PolylineCADTool ctxt = context.getOwner();

                ctxt.setQuestion("POLILINEA" + "\n" +
		"Insertar primer punto");
                ctxt.setDescription(new String[]{"Cancelar"});
                return;
            }

            protected void addPoint(PolylineCADToolContext context, double pointX, double pointY)
            {
                PolylineCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar siguiente punto, Arco[A] o Cerrar[C]");
                    ctxt.setDescription(new String[]{"Arco", "Cerrar", "Cancelar"});
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

            protected void addOption(PolylineCADToolContext context, String s)
            {
                PolylineCADTool ctxt = context.getOwner();

                if (s.equals("A") ||  s.equals("a") || s.equals("Arco"))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("Insertar punto siguiente, Linea[N] o Cerrar[C]");
                        ctxt.setDescription(new String[]{"Linea", "Cerrar", "Cancelar"});
                        ctxt.addOption(s);
                    }
                    finally
                    {
                        context.setState(ExecuteMap.Second);
                        (context.getState()).Entry(context);
                    }
                }
                else if (s.equals("C") ||  s.equals("c") || s.equals("Cerrar"))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.addOption(s);
                        ctxt.closeGeometry();
                        ctxt.endGeometry();
                        ctxt.end();
                    }
                    finally
                    {
                        context.setState(ExecuteMap.Third);
                        (context.getState()).Entry(context);
                    }
                }                else
                {
                    super.addOption(context, s);
                }

                return;
            }

            protected void addPoint(PolylineCADToolContext context, double pointX, double pointY)
            {
                PolylineCADTool ctxt = context.getOwner();

                PolylineCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar siguiente punto, Arco[A] o Cerrar[C]");
                    ctxt.setDescription(new String[]{"Arco", "Cerrar", "Cancelar"});
                    ctxt.addPoint(pointX, pointY);
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

            protected void addOption(PolylineCADToolContext context, String s)
            {
                PolylineCADTool ctxt = context.getOwner();

                if (s.equals("N") ||  s.equals("n") || s.equals("Linea"))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("Insertar siguiente punto, Arco[A] o Cerrar[C]");
                        ctxt.setDescription(new String[]{"Arco", "Cerrar", "Cancelar"});
                        ctxt.addOption(s);
                    }
                    finally
                    {
                        context.setState(ExecuteMap.First);
                        (context.getState()).Entry(context);
                    }
                }
                else if (s.equals("C") ||  s.equals("c") || s.equals("Cerrar"))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.addOption(s);
                        ctxt.closeGeometry();
                        ctxt.endGeometry();
                        ctxt.end();
                    }
                    finally
                    {
                        context.setState(ExecuteMap.Third);
                        (context.getState()).Entry(context);
                    }
                }                else
                {
                    super.addOption(context, s);
                }

                return;
            }

            protected void addPoint(PolylineCADToolContext context, double pointX, double pointY)
            {
                PolylineCADTool ctxt = context.getOwner();

                PolylineCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar punto siguiente, Linea[N] o Cerrar[C]");
                    ctxt.setDescription(new String[]{"Linea", "Cerrar", "Cancelar"});
                    ctxt.addPoint(pointX, pointY);
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
