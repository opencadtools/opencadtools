
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.MatrixCADTool;
import java.awt.event.InputEvent;
import com.iver.andami.PluginServices;

public final class MatrixCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public MatrixCADToolContext(MatrixCADTool owner)
    {
        super();

        _owner = owner;
        setState(Matrix.Start);
        Matrix.Start.Entry(this);
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

    public MatrixCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((MatrixCADToolState) _state);
    }

    protected MatrixCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private MatrixCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class MatrixCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected MatrixCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(MatrixCADToolContext context) {}
        protected void Exit(MatrixCADToolContext context) {}

        protected void addOption(MatrixCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(MatrixCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void addValue(MatrixCADToolContext context, double d)
        {
            Default(context);
        }

        protected void Default(MatrixCADToolContext context)
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

    /* package */ static abstract class Matrix
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
        /* package */ static Matrix_Default.Matrix_Start Start;
        /* package */ static Matrix_Default.Matrix_FirstPoint FirstPoint;
        /* package */ static Matrix_Default.Matrix_SecondPoint SecondPoint;
        private static Matrix_Default Default;

        static
        {
            Start = new Matrix_Default.Matrix_Start("Matrix.Start", 0);
            FirstPoint = new Matrix_Default.Matrix_FirstPoint("Matrix.FirstPoint", 1);
            SecondPoint = new Matrix_Default.Matrix_SecondPoint("Matrix.SecondPoint", 2);
            Default = new Matrix_Default("Matrix.Default", -1);
        }

    }

    protected static class Matrix_Default
        extends MatrixCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected Matrix_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(MatrixCADToolContext context, String s)
        {
            MatrixCADTool ctxt = context.getOwner();

            if (s.equals(PluginServices.getText(this,"cancel")))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        Matrix.Start.getName());

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
                    context.setState(Matrix.Start);

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
                        Matrix.Start.getName());

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
                    context.setState(Matrix.Start);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }

            return;
        }

        protected void addValue(MatrixCADToolContext context, double d)
        {
            MatrixCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Matrix.Start.getName());

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
                context.setState(Matrix.Start);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void addPoint(MatrixCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            MatrixCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Matrix.Start.getName());

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
                context.setState(Matrix.Start);

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


        private static final class Matrix_Start
            extends Matrix_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Matrix_Start(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(MatrixCADToolContext context)
            {
                MatrixCADTool ctxt = context.getOwner();

                ctxt.selection();
                ctxt.setQuestion(PluginServices.getText(this,"insert_first_point"));
                ctxt.setDescription(new String[]{"cancel"});
                return;
            }

            protected void addPoint(MatrixCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                MatrixCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_second_point"));
                    ctxt.setDescription(new String[]{"cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Matrix.SecondPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Matrix_FirstPoint
            extends Matrix_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Matrix_FirstPoint(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(MatrixCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                MatrixCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setDescription(new String[]{"cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Matrix.SecondPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Matrix_SecondPoint
            extends Matrix_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Matrix_SecondPoint(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(MatrixCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                MatrixCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setDescription(new String[]{"cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                    ctxt.endMatrix();
                }
                finally
                {
                    context.setState(Matrix.FirstPoint);
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
