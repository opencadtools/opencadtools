
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import java.awt.event.InputEvent;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.gui.cad.tools.CopyCADTool;

public final class CopyCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public CopyCADToolContext(CopyCADTool owner)
    {
        super();

        _owner = owner;
        setState(Copy.FirstPointToMove);
        Copy.FirstPointToMove.Entry(this);
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

    public void addValue(double d)
    {
        _transition = "addValue";
        getState().addValue(this, d);
        _transition = "";
        return;
    }

    public CopyCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((CopyCADToolState) _state);
    }

    protected CopyCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private CopyCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class CopyCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected CopyCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(CopyCADToolContext context) {}
        protected void Exit(CopyCADToolContext context) {}

        protected void addOption(CopyCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(CopyCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void addValue(CopyCADToolContext context, double d)
        {
            Default(context);
        }

        protected void Default(CopyCADToolContext context)
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

    /* package */ static abstract class Copy
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
        /* package */ static Copy_Default.Copy_FirstPointToMove FirstPointToMove;
        /* package */ static Copy_Default.Copy_SecondPointToMove SecondPointToMove;
        private static Copy_Default Default;

        static
        {
            FirstPointToMove = new Copy_Default.Copy_FirstPointToMove("Copy.FirstPointToMove", 0);
            SecondPointToMove = new Copy_Default.Copy_SecondPointToMove("Copy.SecondPointToMove", 1);
            Default = new Copy_Default("Copy.Default", -1);
        }

    }

    protected static class Copy_Default
        extends CopyCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected Copy_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(CopyCADToolContext context, String s)
        {
            CopyCADTool ctxt = context.getOwner();

            if (s.equals(PluginServices.getText(this,"cancel")))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        Copy.FirstPointToMove.getName());

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
                    context.setState(Copy.FirstPointToMove);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }
            else
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        Copy.FirstPointToMove.getName());

                if (loopbackFlag == false)
                {
                    (context.getState()).Exit(context);
                }

                context.clearState();
                try
                {
                    ctxt.throwOptionException(PluginServices.getText(this,"incorrect_option"), s);
                }
                finally
                {
                    context.setState(Copy.FirstPointToMove);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }

            return;
        }

        protected void addValue(CopyCADToolContext context, double d)
        {
            CopyCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Copy.FirstPointToMove.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.clearState();
            try
            {
                ctxt.throwValueException(PluginServices.getText(this,"incorrect_value"), d);
            }
            finally
            {
                context.setState(Copy.FirstPointToMove);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void addPoint(CopyCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            CopyCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Copy.FirstPointToMove.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.clearState();
            try
            {
                ctxt.throwPointException(PluginServices.getText(this,"incorrect_point"), pointX, pointY);
            }
            finally
            {
                context.setState(Copy.FirstPointToMove);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

    //-----------------------------------------------------------
    // Inner classse.
    //


        private static final class Copy_FirstPointToMove
            extends Copy_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Copy_FirstPointToMove(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(CopyCADToolContext context)
            {
                CopyCADTool ctxt = context.getOwner();

                ctxt.selection();
                ctxt.setQuestion(PluginServices.getText(this,"insert_point_move"));
                ctxt.setDescription(new String[]{"cancel"});
                return;
            }

            protected void addPoint(CopyCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                CopyCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_second_point_move"));
                    ctxt.setDescription(new String[]{"cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Copy.SecondPointToMove);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Copy_SecondPointToMove
            extends Copy_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Copy_SecondPointToMove(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(CopyCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                CopyCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setDescription(new String[]{"cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                    ctxt.end();
                    ctxt.refresh();
                }
                finally
                {
                    context.setState(Copy.FirstPointToMove);
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
