
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.EllipseCADTool;
import com.iver.cit.gvsig.fmap.layers.FBitSet;

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
        setState(ExecuteMap.Initial);
        ExecuteMap.Initial.Entry(this);
    }

    public void addPoint(FBitSet sel, double pointX, double pointY)
    {
        _transition = "addPoint";
        getState().addPoint(this, sel, pointX, pointY);
        _transition = "";
        return;
    }

    public void addValue(FBitSet sel, double d)
    {
        _transition = "addValue";
        getState().addValue(this, sel, d);
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

        protected void addPoint(EllipseCADToolContext context, FBitSet sel, double pointX, double pointY)
        {
            Default(context);
        }

        protected void addValue(EllipseCADToolContext context, FBitSet sel, double d)
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
        extends EllipseCADToolState
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

            protected void Entry(EllipseCADToolContext context)
            {
                EllipseCADTool ctxt = context.getOwner();

                ctxt.init();
                ctxt.setQuestion("Insertar punto inicial de eje de elipse");
                return;
            }

            protected void addPoint(EllipseCADToolContext context, FBitSet sel, double pointX, double pointY)
            {
                EllipseCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar punto final de eje de elipse");
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

            protected void addPoint(EllipseCADToolContext context, FBitSet sel, double pointX, double pointY)
            {
                EllipseCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar distancia al otro eje");
                    ctxt.addPoint(sel, pointX, pointY);
                }
                finally
                {
                    context.setState(ExecuteMap.Second);
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

            protected void addPoint(EllipseCADToolContext context, FBitSet sel, double pointX, double pointY)
            {
                EllipseCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addPoint(sel, pointX, pointY);
                    ctxt.end();
                }
                finally
                {
                    context.setState(ExecuteMap.Third);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addValue(EllipseCADToolContext context, FBitSet sel, double d)
            {
                EllipseCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addValue(sel, d);
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
