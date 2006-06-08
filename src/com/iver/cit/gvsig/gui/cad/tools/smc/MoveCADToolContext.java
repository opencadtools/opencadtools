
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.MoveCADTool;
import java.awt.event.InputEvent;
import com.iver.andami.PluginServices;

public final class MoveCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public MoveCADToolContext(MoveCADTool owner)
    {
        super();

        _owner = owner;
        setState(Move.FirstPointToMove);
        Move.FirstPointToMove.Entry(this);
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

    public MoveCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((MoveCADToolState) _state);
    }

    protected MoveCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private MoveCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class MoveCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected MoveCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(MoveCADToolContext context) {}
        protected void Exit(MoveCADToolContext context) {}

        protected void addOption(MoveCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(MoveCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void addValue(MoveCADToolContext context, double d)
        {
            Default(context);
        }

        protected void Default(MoveCADToolContext context)
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

    /* package */ static abstract class Move
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
        /* package */ static Move_Default.Move_FirstPointToMove FirstPointToMove;
        /* package */ static Move_Default.Move_SecondPointToMove SecondPointToMove;
        private static Move_Default Default;

        static
        {
            FirstPointToMove = new Move_Default.Move_FirstPointToMove("Move.FirstPointToMove", 0);
            SecondPointToMove = new Move_Default.Move_SecondPointToMove("Move.SecondPointToMove", 1);
            Default = new Move_Default("Move.Default", -1);
        }

    }

    protected static class Move_Default
        extends MoveCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected Move_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(MoveCADToolContext context, String s)
        {
            MoveCADTool ctxt = context.getOwner();

            if (s.equals(PluginServices.getText(this,"cancel")))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        Move.FirstPointToMove.getName());

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
                    context.setState(Move.FirstPointToMove);

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
                        Move.FirstPointToMove.getName());

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
                    context.setState(Move.FirstPointToMove);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }

            return;
        }

        protected void addValue(MoveCADToolContext context, double d)
        {
            MoveCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Move.FirstPointToMove.getName());

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
                context.setState(Move.FirstPointToMove);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void addPoint(MoveCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            MoveCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Move.FirstPointToMove.getName());

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
                context.setState(Move.FirstPointToMove);

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


        private static final class Move_FirstPointToMove
            extends Move_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Move_FirstPointToMove(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(MoveCADToolContext context)
            {
                MoveCADTool ctxt = context.getOwner();

                ctxt.selection();
                ctxt.setQuestion(PluginServices.getText(this,"insert_point_move"));
                ctxt.setDescription(new String[]{"cancel"});
                return;
            }

            protected void addPoint(MoveCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                MoveCADTool ctxt = context.getOwner();


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
                    context.setState(Move.SecondPointToMove);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Move_SecondPointToMove
            extends Move_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Move_SecondPointToMove(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(MoveCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                MoveCADTool ctxt = context.getOwner();


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
                    context.setState(Move.FirstPointToMove);
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
