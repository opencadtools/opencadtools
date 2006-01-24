
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.CircleCADTool;
import com.iver.cit.gvsig.fmap.layers.FBitSet;

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
        setState(ExecuteMap.Initial);
        ExecuteMap.Initial.Entry(this);
    }

    public void addoption(FBitSet sel, String s)
    {
        _transition = "addoption";
        getState().addoption(this, sel, s);
        _transition = "";
        return;
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

        protected void addoption(CircleCADToolContext context, FBitSet sel, String s)
        {
            Default(context);
        }

        protected void addpoint(CircleCADToolContext context, FBitSet sel, double pointX, double pointY)
        {
            Default(context);
        }

        protected void addvalue(CircleCADToolContext context, FBitSet sel, double d)
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
        /* package */ static ExecuteMap_Default.ExecuteMap_Sixth Sixth;
        /* package */ static ExecuteMap_Default.ExecuteMap_Seventh Seventh;
        private static ExecuteMap_Default Default;

        static
        {
            Initial = new ExecuteMap_Default.ExecuteMap_Initial("ExecuteMap.Initial", 0);
            First = new ExecuteMap_Default.ExecuteMap_First("ExecuteMap.First", 1);
            Second = new ExecuteMap_Default.ExecuteMap_Second("ExecuteMap.Second", 2);
            Third = new ExecuteMap_Default.ExecuteMap_Third("ExecuteMap.Third", 3);
            Fourth = new ExecuteMap_Default.ExecuteMap_Fourth("ExecuteMap.Fourth", 4);
            Fiveth = new ExecuteMap_Default.ExecuteMap_Fiveth("ExecuteMap.Fiveth", 5);
            Sixth = new ExecuteMap_Default.ExecuteMap_Sixth("ExecuteMap.Sixth", 6);
            Seventh = new ExecuteMap_Default.ExecuteMap_Seventh("ExecuteMap.Seventh", 7);
            Default = new ExecuteMap_Default("ExecuteMap.Default", -1);
        }

    }

    protected static class ExecuteMap_Default
        extends CircleCADToolState
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

            protected void Entry(CircleCADToolContext context)
            {
                CircleCADTool ctxt = context.getOwner();

                ctxt.init();
                ctxt.setQuestion("Insertar punto central o [3P]:");
                return;
            }

            protected void addoption(CircleCADToolContext context, FBitSet sel, String s)
            {
                CircleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar primer punto");
                    ctxt.addoption(sel, s);
                }
                finally
                {
                    context.setState(ExecuteMap.Seventh);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addpoint(CircleCADToolContext context, FBitSet sel, double pointX, double pointY)
            {
                CircleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar radio o segundo punto");
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

            protected void addpoint(CircleCADToolContext context, FBitSet sel, double pointX, double pointY)
            {
                CircleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addpoint(sel, pointX, pointY);
                    ctxt.refresh();
                    ctxt.end();
                }
                finally
                {
                    context.setState(ExecuteMap.Sixth);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addvalue(CircleCADToolContext context, FBitSet sel, double d)
            {
                CircleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addvalue(sel, d);
                    ctxt.refresh();
                    ctxt.end();
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

            protected void addpoint(CircleCADToolContext context, FBitSet sel, double pointX, double pointY)
            {
                CircleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar tercer punto");
                    ctxt.addpoint(sel, pointX, pointY);
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

            protected void addpoint(CircleCADToolContext context, FBitSet sel, double pointX, double pointY)
            {
                CircleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addpoint(sel, pointX, pointY);
                    ctxt.refresh();
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

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ExecuteMap_Sixth
            extends ExecuteMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ExecuteMap_Sixth(String name, int id)
            {
                super (name, id);
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ExecuteMap_Seventh
            extends ExecuteMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ExecuteMap_Seventh(String name, int id)
            {
                super (name, id);
            }

            protected void addpoint(CircleCADToolContext context, FBitSet sel, double pointX, double pointY)
            {
                CircleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar segundo punto");
                    ctxt.addpoint(sel, pointX, pointY);
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

    //-----------------------------------------------------------
    // Member data.
    //
    }
}
