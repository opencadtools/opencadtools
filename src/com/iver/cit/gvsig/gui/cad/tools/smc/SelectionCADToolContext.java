
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.SelectionCADTool;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import java.awt.event.InputEvent;

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
        setState(Selection.FirstPoint);
        Selection.FirstPoint.Entry(this);
    }

    public void addOption(String s)
    {
        _transition = "addOption";
        getState().addOption(this, s);
        _transition = "";
        return;
    }

    public void addPoint(double pointX, double pointY, InputEvent event)
    {
        _transition = "addPoint";
        getState().addPoint(this, pointX, pointY, event);
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

        protected void addPoint(SelectionCADToolContext context, double pointX, double pointY, InputEvent event)
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

    /* package */ static abstract class Selection
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
        /* package */ static Selection_Default.Selection_FirstPoint FirstPoint;
        /* package */ static Selection_Default.Selection_SecondPoint SecondPoint;
        /* package */ static Selection_Default.Selection_WithSelectedFeatures WithSelectedFeatures;
        /* package */ static Selection_Default.Selection_WithHandlers WithHandlers;
        private static Selection_Default Default;

        static
        {
            FirstPoint = new Selection_Default.Selection_FirstPoint("Selection.FirstPoint", 0);
            SecondPoint = new Selection_Default.Selection_SecondPoint("Selection.SecondPoint", 1);
            WithSelectedFeatures = new Selection_Default.Selection_WithSelectedFeatures("Selection.WithSelectedFeatures", 2);
            WithHandlers = new Selection_Default.Selection_WithHandlers("Selection.WithHandlers", 3);
            Default = new Selection_Default("Selection.Default", -1);
        }

    }

    protected static class Selection_Default
        extends SelectionCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected Selection_Default(String name, int id)
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
                        Selection.FirstPoint.getName());

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
                    context.setState(Selection.FirstPoint);

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


        private static final class Selection_FirstPoint
            extends Selection_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Selection_FirstPoint(String name, int id)
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

            protected void addPoint(SelectionCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                SelectionCADTool ctxt = context.getOwner();

                if (ctxt.selectFeatures(pointX,pointY) && ctxt.getNextState().equals("Selection.SecondPoint"))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("Precise segundo punto del rect?ngulo de seleccion");
                        ctxt.setDescription(new String[]{"Cancelar"});
                        ctxt.addPoint(pointX, pointY, event);
                    }
                    finally
                    {
                        context.setState(Selection.SecondPoint);
                        (context.getState()).Entry(context);
                    }
                }
                else if (ctxt.getNextState().equals("Selection.WithSelectedFeatures"))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("select_handlers");
                        ctxt.setDescription(new String[]{"Cancelar"});
                        ctxt.addPoint(pointX, pointY, event);
                    }
                    finally
                    {
                        context.setState(Selection.WithSelectedFeatures);
                        (context.getState()).Entry(context);
                    }
                }                else
                {
                    super.addPoint(context, pointX, pointY, event);
                }

                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Selection_SecondPoint
            extends Selection_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Selection_SecondPoint(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(SelectionCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                SelectionCADTool ctxt = context.getOwner();

                if (ctxt.selectWithSecondPoint(pointX,pointY) > 0)
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("select_handlers");
                        ctxt.setDescription(new String[]{"Cancelar"});
                        ctxt.addPoint(pointX, pointY, event);
                    }
                    finally
                    {
                        context.setState(Selection.WithSelectedFeatures);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("Precise punto del rect?ngulo de selecci?n");
                        ctxt.setDescription(new String[]{"Cancelar"});
                    }
                    finally
                    {
                        context.setState(Selection.FirstPoint);
                        (context.getState()).Entry(context);
                    }
                }

                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Selection_WithSelectedFeatures
            extends Selection_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Selection_WithSelectedFeatures(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(SelectionCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                SelectionCADTool ctxt = context.getOwner();

                if (ctxt.selectHandlers(pointX, pointY)>0)
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("Precise punto destino");
                        ctxt.setDescription(new String[]{"Cancelar"});
                        ctxt.addPoint(pointX, pointY, event);
                    }
                    finally
                    {
                        context.setState(Selection.WithHandlers);
                        (context.getState()).Entry(context);
                    }
                }
                else if (ctxt.selectFeatures(pointX,pointY) && ctxt.getNextState().equals("Selection.WithSelectedFeatures"))
                {
                    SelectionCADToolState endState = context.getState();

                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("select_handlers");
                        ctxt.setDescription(new String[]{"Cancelar"});
                        ctxt.addPoint(pointX, pointY, event);
                    }
                    finally
                    {
                        context.setState(endState);
                    }
                }
                else
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("Precise punto del rect?ngulo de selecci?n");
                        ctxt.setDescription(new String[]{"Cancelar"});
                        ctxt.addPoint(pointX, pointY, event);
                    }
                    finally
                    {
                        context.setState(Selection.FirstPoint);
                        (context.getState()).Entry(context);
                    }
                }

                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Selection_WithHandlers
            extends Selection_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Selection_WithHandlers(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(SelectionCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                SelectionCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("select_handlers");
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addPoint(pointX, pointY, event);
                    ctxt.refresh();
                }
                finally
                {
                    context.setState(Selection.WithSelectedFeatures);
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
