
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.RotateCADTool;

public final class RotateCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public RotateCADToolContext(RotateCADTool owner)
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

    public RotateCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((RotateCADToolState) _state);
    }

    protected RotateCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private RotateCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class RotateCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected RotateCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(RotateCADToolContext context) {}
        protected void Exit(RotateCADToolContext context) {}

        protected void addOption(RotateCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(RotateCADToolContext context, double pointX, double pointY)
        {
            Default(context);
        }

        protected void addValue(RotateCADToolContext context, double d)
        {
            Default(context);
        }

        protected void Default(RotateCADToolContext context)
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
        extends RotateCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected ExecuteMap_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(RotateCADToolContext context, String s)
        {
            RotateCADTool ctxt = context.getOwner();

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

            protected void Entry(RotateCADToolContext context)
            {
                RotateCADTool ctxt = context.getOwner();

                ctxt.selection();
                ctxt.setQuestion("ROTAR" + "\n" +
		"Precise punto base");
                ctxt.setDescription(new String[]{"Cancelar"});
                return;
            }

            protected void addPoint(RotateCADToolContext context, double pointX, double pointY)
            {
                RotateCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Precise angulo de rotacion");
                    ctxt.setDescription(new String[]{"Cancelar"});
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

            protected void addPoint(RotateCADToolContext context, double pointX, double pointY)
            {
                RotateCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addPoint(pointX, pointY);
                    ctxt.end();
                    ctxt.refresh();
                }
                finally
                {
                    context.setState(ExecuteMap.Second);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addValue(RotateCADToolContext context, double d)
            {
                RotateCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addValue(d);
                    ctxt.end();
                    ctxt.refresh();
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

        //-------------------------------------------------------
        // Member data.
        //
        }

    //-----------------------------------------------------------
    // Member data.
    //
    }
}
