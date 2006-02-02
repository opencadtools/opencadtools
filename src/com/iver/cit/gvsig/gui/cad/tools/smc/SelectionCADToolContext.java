
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.SelectionCADTool;
import com.iver.cit.gvsig.fmap.layers.FBitSet;

public final class SelectionCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public SelectionCADToolContext(SelectionCADTool owner)
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

    public SelectionCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((SelectionCADToolState) _state);
    }

    protected SelectionCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private SelectionCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class SelectionCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected SelectionCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(SelectionCADToolContext context) {}
        protected void Exit(SelectionCADToolContext context) {}

        protected void addOption(SelectionCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(SelectionCADToolContext context, double pointX, double pointY)
        {
            Default(context);
        }

        protected void Default(SelectionCADToolContext context)
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
        private static ExecuteMap_Default Default;

        static
        {
            Initial = new ExecuteMap_Default.ExecuteMap_Initial("ExecuteMap.Initial", 0);
            First = new ExecuteMap_Default.ExecuteMap_First("ExecuteMap.First", 1);
            Second = new ExecuteMap_Default.ExecuteMap_Second("ExecuteMap.Second", 2);
            Third = new ExecuteMap_Default.ExecuteMap_Third("ExecuteMap.Third", 3);
            Default = new ExecuteMap_Default("ExecuteMap.Default", -1);
        }

    }

    protected static class ExecuteMap_Default
        extends SelectionCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected ExecuteMap_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(SelectionCADToolContext context, String s)
        {
            SelectionCADTool ctxt = context.getOwner();

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

            protected void Entry(SelectionCADToolContext context)
            {
                SelectionCADTool ctxt = context.getOwner();

                ctxt.setQuestion("SELECCION" + "\n" +
		"Precise punto del rect?ngulo de selecci?n");
                ctxt.setDescription(new String[]{"Cancelar"});
                return;
            }

            protected void addPoint(SelectionCADToolContext context, double pointX, double pointY)
            {
                SelectionCADTool ctxt = context.getOwner();

                if (!ctxt.isSelected(pointX,pointY))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("Precise segundo punto del rect?ngulo de seleccion");
                        ctxt.setDescription(new String[]{"Cancelar"});
                        ctxt.addPoint(pointX, pointY);
                    }
                    finally
                    {
                        context.setState(ExecuteMap.First);
                        (context.getState()).Entry(context);
                    }
                }
                else if (ctxt.isSelected(pointX,pointY))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("Precise punto destino");
                        ctxt.setDescription(new String[]{"Cancelar"});
                        ctxt.addPoint(pointX, pointY);
                    }
                    finally
                    {
                        context.setState(ExecuteMap.Second);
                        (context.getState()).Entry(context);
                    }
                }                else
                {
                    super.addPoint(context, pointX, pointY);
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

            protected void addPoint(SelectionCADToolContext context, double pointX, double pointY)
            {
                SelectionCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Precise punto de estiramiento");
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addPoint(pointX, pointY);
                    ctxt.end();
                }
                finally
                {
                    context.setState(ExecuteMap.Initial);
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

            protected void addPoint(SelectionCADToolContext context, double pointX, double pointY)
            {
                SelectionCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Precise punto destino");
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addPoint(pointX, pointY);
                    ctxt.end();
                    ctxt.refresh();
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

        //-------------------------------------------------------
        // Member data.
        //
        }

    //-----------------------------------------------------------
    // Member data.
    //
    }
}
