
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.MultiPointCADTool;
import java.awt.event.InputEvent;
import com.iver.andami.PluginServices;

public final class MultiPointCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public MultiPointCADToolContext(MultiPointCADTool owner)
    {
        super();

        _owner = owner;
        setState(MultiPoint.InsertPoint);
        MultiPoint.InsertPoint.Entry(this);
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

    public MultiPointCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((MultiPointCADToolState) _state);
    }

    protected MultiPointCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private MultiPointCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class MultiPointCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected MultiPointCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(MultiPointCADToolContext context) {}
        protected void Exit(MultiPointCADToolContext context) {}

        protected void addOption(MultiPointCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(MultiPointCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void addValue(MultiPointCADToolContext context, double d)
        {
            Default(context);
        }

        protected void Default(MultiPointCADToolContext context)
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

    /* package */ static abstract class MultiPoint
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
        /* package */ static MultiPoint_Default.MultiPoint_InsertPoint InsertPoint;
        private static MultiPoint_Default Default;

        static
        {
            InsertPoint = new MultiPoint_Default.MultiPoint_InsertPoint("MultiPoint.InsertPoint", 0);
            Default = new MultiPoint_Default("MultiPoint.Default", -1);
        }

    }

    protected static class MultiPoint_Default
        extends MultiPointCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected MultiPoint_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(MultiPointCADToolContext context, String s)
        {
            MultiPointCADTool ctxt = context.getOwner();

            if (s.equals(PluginServices.getText(this,"cancel")))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        MultiPoint.InsertPoint.getName());

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
                    context.setState(MultiPoint.InsertPoint);

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
                        MultiPoint.InsertPoint.getName());

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
                    context.setState(MultiPoint.InsertPoint);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }

            return;
        }

        protected void addValue(MultiPointCADToolContext context, double d)
        {
            MultiPointCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    MultiPoint.InsertPoint.getName());

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
                context.setState(MultiPoint.InsertPoint);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void addPoint(MultiPointCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            MultiPointCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    MultiPoint.InsertPoint.getName());

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
                context.setState(MultiPoint.InsertPoint);

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


        private static final class MultiPoint_InsertPoint
            extends MultiPoint_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private MultiPoint_InsertPoint(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(MultiPointCADToolContext context)
            {
                MultiPointCADTool ctxt = context.getOwner();

                ctxt.setQuestion(PluginServices.getText(this,"insert_point"));
                ctxt.setDescription(new String[]{"cancel", });
                return;
            }

            protected void addOption(MultiPointCADToolContext context, String s)
            {
                MultiPointCADTool ctxt = context.getOwner();

                if (s.equals("e") || s.equals("E") || s.equals(PluginServices.getText(this,"end")))
                {
                    MultiPointCADToolState endState = context.getState();

                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_point"));
                        ctxt.setDescription(new String[]{"cancel"});
                        ctxt.addOption(s);
                        ctxt.endGeometry();
                    }
                    finally
                    {
                        context.setState(endState);
                    }
                }
                else
                {
                    super.addOption(context, s);
                }

                return;
            }

            protected void addPoint(MultiPointCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                MultiPointCADTool ctxt = context.getOwner();

                MultiPointCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_point_or_end"));
                    ctxt.setDescription(new String[]{"cancel", "end"});
                    ctxt.addPoint(pointX, pointY, event);
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

    //-----------------------------------------------------------
    // Member data.
    //
    }
}
