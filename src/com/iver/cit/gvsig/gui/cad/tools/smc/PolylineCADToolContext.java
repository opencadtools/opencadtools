
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.PolylineCADTool;
import com.iver.cit.gvsig.fmap.layers.FBitSet;

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

    public void addOption(FBitSet sel, String s)
    {
        _transition = "addOption";
        getState().addOption(this, sel, s);
        _transition = "";
        return;
    }

    public void addPoint(FBitSet sel, double pointX, double pointY)
    {
        _transition = "addPoint";
        getState().addPoint(this, sel, pointX, pointY);
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

        protected void addOption(PolylineCADToolContext context, FBitSet sel, String s)
        {
            Default(context);
        }

        protected void addPoint(PolylineCADToolContext context, FBitSet sel, double pointX, double pointY)
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

                ctxt.init();
                ctxt.setQuestion("Insertar primer punto");
                return;
            }

            protected void Exit(PolylineCADToolContext context)
            {
                PolylineCADTool ctxt = context.getOwner();

                ctxt.end();
                return;
            }

            protected void addPoint(PolylineCADToolContext context, FBitSet sel, double pointX, double pointY)
            {
                PolylineCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar siguiente punto, Arco[A] o Cerrar[C]");
                    ctxt.addPoint(sel, pointX, pointY);
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

            protected void addOption(PolylineCADToolContext context, FBitSet sel, String s)
            {
                PolylineCADTool ctxt = context.getOwner();

                if (s == "A" ||  s == "a")
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("Insertar punto siguiente, L?nea[N] o Cerrar[C]");
                        ctxt.addOption(sel, s);
                    }
                    finally
                    {
                        context.setState(ExecuteMap.Second);
                        (context.getState()).Entry(context);
                    }
                }
                else if (s == "C" ||  s == "c")
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.addOption(sel, s);
                        ctxt.end();
                    }
                    finally
                    {
                        context.setState(ExecuteMap.Third);
                        (context.getState()).Entry(context);
                    }
                }                else
                {
                    super.addOption(context, sel, s);
                }

                return;
            }

            protected void addPoint(PolylineCADToolContext context, FBitSet sel, double pointX, double pointY)
            {
                PolylineCADTool ctxt = context.getOwner();

                PolylineCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar siguiente punto, Arco[A] o Cerrar[C]");
                    ctxt.addPoint(sel, pointX, pointY);
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

            protected void addOption(PolylineCADToolContext context, FBitSet sel, String s)
            {
                PolylineCADTool ctxt = context.getOwner();

                if (s == "N" ||  s == "n")
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("Insertar siguiente punto, Arco[A] o Cerrar[C]");
                        ctxt.addOption(sel, s);
                    }
                    finally
                    {
                        context.setState(ExecuteMap.First);
                        (context.getState()).Entry(context);
                    }
                }
                else if (s == "C" ||  s == "c")
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.addOption(sel, s);
                        ctxt.end();
                    }
                    finally
                    {
                        context.setState(ExecuteMap.Third);
                        (context.getState()).Entry(context);
                    }
                }                else
                {
                    super.addOption(context, sel, s);
                }

                return;
            }

            protected void addPoint(PolylineCADToolContext context, FBitSet sel, double pointX, double pointY)
            {
                PolylineCADTool ctxt = context.getOwner();

                PolylineCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar punto siguiente, L?nea[N] o Cerrar[C]");
                    ctxt.addPoint(sel, pointX, pointY);
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
