
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.LineCADTool;
import com.iver.cit.gvsig.fmap.layers.FBitSet;

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
        setState(ExecuteMap.Initial);
        ExecuteMap.Initial.Entry(this);
    }

    public void addpoint(FBitSet sel, double pointX, double pointY)
    {
        _transition = "addpoint";
        getState().addpoint(this, sel, pointX, pointY);
        _transition = "";
        return;
    }

    public void addvalue(FBitSet sel, double d)
    {
        _transition = "addvalue";
        getState().addvalue(this, sel, d);
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

        protected void addpoint(LineCADToolContext context, FBitSet sel, double pointX, double pointY)
        {
            Default(context);
        }

        protected void addvalue(LineCADToolContext context, FBitSet sel, double d)
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
        private static ExecuteMap_Default Default;

        static
        {
            Initial = new ExecuteMap_Default.ExecuteMap_Initial("ExecuteMap.Initial", 0);
            First = new ExecuteMap_Default.ExecuteMap_First("ExecuteMap.First", 1);
            Second = new ExecuteMap_Default.ExecuteMap_Second("ExecuteMap.Second", 2);
            Default = new ExecuteMap_Default("ExecuteMap.Default", -1);
        }

    }

    protected static class ExecuteMap_Default
        extends LineCADToolState
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

            protected void Entry(LineCADToolContext context)
            {
                LineCADTool ctxt = context.getOwner();

                ctxt.init();
                ctxt.setQuestion("Insertar primer punto");
                return;
            }

            protected void Exit(LineCADToolContext context)
            {
                LineCADTool ctxt = context.getOwner();

                ctxt.end();
                return;
            }

            protected void addpoint(LineCADToolContext context, FBitSet sel, double pointX, double pointY)
            {
                LineCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar segundo punto o angulo");
                    ctxt.addpoint(sel, pointX, pointY);
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

            protected void addpoint(LineCADToolContext context, FBitSet sel, double pointX, double pointY)
            {
                LineCADTool ctxt = context.getOwner();

                LineCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar segundo punto o angulo");
                    ctxt.addpoint(sel, pointX, pointY);
                    ctxt.refresh();
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void addvalue(LineCADToolContext context, FBitSet sel, double d)
            {
                LineCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar longitud o punto");
                    ctxt.addvalue(sel, d);
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

            protected void addpoint(LineCADToolContext context, FBitSet sel, double pointX, double pointY)
            {
                LineCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar segundo punto o angulo");
                    ctxt.addpoint(sel, pointX, pointY);
                    ctxt.refresh();
                }
                finally
                {
                    context.setState(ExecuteMap.First);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addvalue(LineCADToolContext context, FBitSet sel, double d)
            {
                LineCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar segundo punto o angulo");
                    ctxt.addvalue(sel, d);
                    ctxt.refresh();
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

    //-----------------------------------------------------------
    // Member data.
    //
    }
}
