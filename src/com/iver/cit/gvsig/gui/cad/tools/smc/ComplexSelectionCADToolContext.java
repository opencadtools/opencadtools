
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.ComplexSelectionCADTool;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import java.awt.event.InputEvent;
import com.iver.andami.PluginServices;

public final class ComplexSelectionCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public ComplexSelectionCADToolContext(ComplexSelectionCADTool owner)
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

    public ComplexSelectionCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((ComplexSelectionCADToolState) _state);
    }

    protected ComplexSelectionCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private ComplexSelectionCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class ComplexSelectionCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected ComplexSelectionCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(ComplexSelectionCADToolContext context) {}
        protected void Exit(ComplexSelectionCADToolContext context) {}

        protected void addOption(ComplexSelectionCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(ComplexSelectionCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void Default(ComplexSelectionCADToolContext context)
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
        /* package */ static Selection_Default.Selection_SecondPointOutRectangle SecondPointOutRectangle;
        /* package */ static Selection_Default.Selection_SecondPointCircle SecondPointCircle;
        /* package */ static Selection_Default.Selection_NextPointPolygon NextPointPolygon;
        private static Selection_Default Default;

        static
        {
            FirstPoint = new Selection_Default.Selection_FirstPoint("Selection.FirstPoint", 0);
            SecondPoint = new Selection_Default.Selection_SecondPoint("Selection.SecondPoint", 1);
            WithSelectedFeatures = new Selection_Default.Selection_WithSelectedFeatures("Selection.WithSelectedFeatures", 2);
            WithHandlers = new Selection_Default.Selection_WithHandlers("Selection.WithHandlers", 3);
            SecondPointOutRectangle = new Selection_Default.Selection_SecondPointOutRectangle("Selection.SecondPointOutRectangle", 4);
            SecondPointCircle = new Selection_Default.Selection_SecondPointCircle("Selection.SecondPointCircle", 5);
            NextPointPolygon = new Selection_Default.Selection_NextPointPolygon("Selection.NextPointPolygon", 6);
            Default = new Selection_Default("Selection.Default", -1);
        }

    }

    protected static class Selection_Default
        extends ComplexSelectionCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected Selection_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(ComplexSelectionCADToolContext context, String s)
        {
            ComplexSelectionCADTool ctxt = context.getOwner();

            if (s.equals(PluginServices.getText(this,"cancel")))
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

            protected void Entry(ComplexSelectionCADToolContext context)
            {
                ComplexSelectionCADTool ctxt = context.getOwner();

                ctxt.setQuestion(PluginServices.getText(this,"insert_point_selection_or_types"));
                ctxt.setDescription(new String[]{"out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
                return;
            }

            protected void addOption(ComplexSelectionCADToolContext context, String s)
            {
                ComplexSelectionCADTool ctxt = context.getOwner();

                ComplexSelectionCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_point_selection_or_types"));
                    ctxt.setDescription(new String[]{"out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
                    ctxt.addOption(s);
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void addPoint(ComplexSelectionCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                ComplexSelectionCADTool ctxt = context.getOwner();

                if (ctxt.getType().equals(PluginServices.getText(this,"out_rectangle")))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_second_point_selection"));
                        ctxt.setDescription(new String[]{"out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
                        ctxt.addPoint(pointX, pointY, event);
                    }
                    finally
                    {
                        context.setState(Selection.SecondPointOutRectangle);
                        (context.getState()).Entry(context);
                    }
                }
                else if (ctxt.getType().equals(PluginServices.getText(this,"inside_circle")) || ctxt.getType().equals(PluginServices.getText(this,"cross_circle")) || ctxt.getType().equals(PluginServices.getText(this,"out_circle")))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_second_point_selection"));
                        ctxt.setDescription(new String[]{"out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
                        ctxt.addPoint(pointX, pointY, event);
                    }
                    finally
                    {
                        context.setState(Selection.SecondPointCircle);
                        (context.getState()).Entry(context);
                    }
                }
                else if (ctxt.getType().equals(PluginServices.getText(this,"inside_polygon")) || ctxt.getType().equals(PluginServices.getText(this,"cross_polygon")) || ctxt.getType().equals(PluginServices.getText(this,"out_polygon")))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_next_point_selection_or_end_polygon"));
                        ctxt.setDescription(new String[]{"end_polygon", "out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
                        ctxt.addPoint(pointX, pointY, event);
                    }
                    finally
                    {
                        context.setState(Selection.NextPointPolygon);
                        (context.getState()).Entry(context);
                    }
                }
                else if (ctxt.getType().equals(PluginServices.getText(this,"simple")) && ctxt.selectFeatures(pointX,pointY, event) && ctxt.getNextState().equals("Selection.SecondPoint"))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_second_point_selection"));
                        ctxt.setDescription(new String[]{"out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
                        ctxt.addPoint(pointX, pointY, event);
                    }
                    finally
                    {
                        context.setState(Selection.SecondPoint);
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

            protected void addOption(ComplexSelectionCADToolContext context, String s)
            {
                ComplexSelectionCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_point_selection_or_types"));
                    ctxt.setDescription(new String[]{"end_polygon", "out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
                    ctxt.setType(s);
                }
                finally
                {
                    context.setState(Selection.FirstPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addPoint(ComplexSelectionCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                ComplexSelectionCADTool ctxt = context.getOwner();

                if (ctxt.selectWithSecondPoint(pointX,pointY, event) > 0)
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"select_handlers"));
                        ctxt.setDescription(new String[]{"out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
                        ctxt.addPoint(pointX, pointY, event);
                        ctxt.end();
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
                        ctxt.setQuestion(PluginServices.getText(this,"insert_point_selection_or_types"));
                        ctxt.setDescription(new String[]{"out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
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

            protected void addOption(ComplexSelectionCADToolContext context, String s)
            {
                ComplexSelectionCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_point_selection_or_types"));
                    ctxt.setDescription(new String[]{"end_polygon", "out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
                    ctxt.setType(s);
                }
                finally
                {
                    context.setState(Selection.FirstPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addPoint(ComplexSelectionCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                ComplexSelectionCADTool ctxt = context.getOwner();

                if (ctxt.selectHandlers(pointX, pointY, event)>0)
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_destination_point"));
                        ctxt.setDescription(new String[]{"out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
                        ctxt.addPoint(pointX, pointY, event);
                    }
                    finally
                    {
                        context.setState(Selection.WithHandlers);
                        (context.getState()).Entry(context);
                    }
                }
                else if (ctxt.selectFeatures(pointX,pointY, event) && ctxt.getNextState().equals("Selection.WithSelectedFeatures"))
                {
                    ComplexSelectionCADToolState endState = context.getState();

                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"select_handlers"));
                        ctxt.setDescription(new String[]{"out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
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
                        ctxt.setQuestion(PluginServices.getText(this,"insert_point_selection_or_types"));
                        ctxt.setDescription(new String[]{"out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
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

            protected void addPoint(ComplexSelectionCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                ComplexSelectionCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"select_handlers"));
                    ctxt.setDescription(new String[]{"out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
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

        private static final class Selection_SecondPointOutRectangle
            extends Selection_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Selection_SecondPointOutRectangle(String name, int id)
            {
                super (name, id);
            }

            protected void addOption(ComplexSelectionCADToolContext context, String s)
            {
                ComplexSelectionCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_point_selection_or_types"));
                    ctxt.setDescription(new String[]{"end_polygon", "out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
                    ctxt.setType(s);
                }
                finally
                {
                    context.setState(Selection.FirstPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addPoint(ComplexSelectionCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                ComplexSelectionCADTool ctxt = context.getOwner();

                if (ctxt.selectWithSecondPointOutRectangle(pointX,pointY, event) > 0)
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"select_handlers"));
                        ctxt.setDescription(new String[]{"out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
                        ctxt.addPoint(pointX, pointY, event);
                        ctxt.end();
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
                        ctxt.setQuestion(PluginServices.getText(this,"insert_point_selection_or_types"));
                        ctxt.setDescription(new String[]{"out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
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

        private static final class Selection_SecondPointCircle
            extends Selection_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Selection_SecondPointCircle(String name, int id)
            {
                super (name, id);
            }

            protected void addOption(ComplexSelectionCADToolContext context, String s)
            {
                ComplexSelectionCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_point_selection_or_types"));
                    ctxt.setDescription(new String[]{"out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
                    ctxt.setType(s);
                }
                finally
                {
                    context.setState(Selection.FirstPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addPoint(ComplexSelectionCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                ComplexSelectionCADTool ctxt = context.getOwner();

                if (ctxt.selectWithCircle(pointX,pointY, event) > 0)
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"select_handlers"));
                        ctxt.setDescription(new String[]{"out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
                        ctxt.addPoint(pointX, pointY, event);
                        ctxt.end();
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
                        ctxt.setQuestion(PluginServices.getText(this,"insert_point_selection_or_types"));
                        ctxt.setDescription(new String[]{"out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
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

        private static final class Selection_NextPointPolygon
            extends Selection_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Selection_NextPointPolygon(String name, int id)
            {
                super (name, id);
            }

            protected void addOption(ComplexSelectionCADToolContext context, String s)
            {
                ComplexSelectionCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_point_selection_or_types"));
                    ctxt.setDescription(new String[]{"end_polygon", "out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
                    ctxt.addOption(s);
                }
                finally
                {
                    context.setState(Selection.FirstPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addPoint(ComplexSelectionCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                ComplexSelectionCADTool ctxt = context.getOwner();

                ComplexSelectionCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_next_point_selection_or_end_polygon"));
                    ctxt.setDescription(new String[]{"end_polygon", "out_rectangle", "inside_polygon", "cross_polygon", "out_polygon", "inside_circle", "cross_circle", "out_circle", "select_all", "cancel"});
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
