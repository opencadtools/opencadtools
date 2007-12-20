
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.PolylineCADTool;
import java.awt.event.InputEvent;
import com.iver.andami.PluginServices;

public final class PolylineCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public PolylineCADToolContext(PolylineCADTool owner)
    {
        super();

        _owner = owner;
        setState(Polyline.FirstPoint);
        Polyline.FirstPoint.Entry(this);
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

    public PolylineCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((PolylineCADToolState) _state);
    }

    protected PolylineCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private PolylineCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class PolylineCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected PolylineCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(PolylineCADToolContext context) {}
        protected void Exit(PolylineCADToolContext context) {}

        protected void addOption(PolylineCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(PolylineCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void addValue(PolylineCADToolContext context, double d)
        {
            Default(context);
        }

        protected void Default(PolylineCADToolContext context)
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

    /* package */ static abstract class Polyline
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
        /* package */ static Polyline_Default.Polyline_FirstPoint FirstPoint;
        /* package */ static Polyline_Default.Polyline_NextPointOrArcOrClose NextPointOrArcOrClose;
        /* package */ static Polyline_Default.Polyline_NextPointOrLineOrClose NextPointOrLineOrClose;
        private static Polyline_Default Default;

        static
        {
            FirstPoint = new Polyline_Default.Polyline_FirstPoint("Polyline.FirstPoint", 0);
            NextPointOrArcOrClose = new Polyline_Default.Polyline_NextPointOrArcOrClose("Polyline.NextPointOrArcOrClose", 1);
            NextPointOrLineOrClose = new Polyline_Default.Polyline_NextPointOrLineOrClose("Polyline.NextPointOrLineOrClose", 2);
            Default = new Polyline_Default("Polyline.Default", -1);
        }

    }

    protected static class Polyline_Default
        extends PolylineCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected Polyline_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(PolylineCADToolContext context, String s)
        {
            PolylineCADTool ctxt = context.getOwner();

            if (s.equals(PluginServices.getText(this,"cancel")))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        Polyline.FirstPoint.getName());

                if (loopbackFlag == false)
                {
                    (context.getState()).Exit(context);
                }

                context.clearState();
                try
                {
                    ctxt.cancel();
                }
                finally
                {
                    context.setState(Polyline.FirstPoint);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }
            else if (s.equals(""))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        Polyline.FirstPoint.getName());

                if (loopbackFlag == false)
                {
                    (context.getState()).Exit(context);
                }

                context.clearState();
                try
                {
                    ctxt.endGeometry();
                }
                finally
                {
                    context.setState(Polyline.FirstPoint);

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
                        Polyline.FirstPoint.getName());

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
                    context.setState(Polyline.FirstPoint);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }

            return;
        }

        protected void addValue(PolylineCADToolContext context, double d)
        {
            PolylineCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Polyline.FirstPoint.getName());

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
                context.setState(Polyline.FirstPoint);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void addPoint(PolylineCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            PolylineCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Polyline.FirstPoint.getName());

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
                context.setState(Polyline.FirstPoint);

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


        private static final class Polyline_FirstPoint
            extends Polyline_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Polyline_FirstPoint(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(PolylineCADToolContext context)
            {
                PolylineCADTool ctxt = context.getOwner();

                ctxt.setQuestion(PluginServices.getText(this,"insert_first_point"));
                ctxt.setDescription(new String[]{"cancel"});
                return;
            }

            protected void addPoint(PolylineCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                PolylineCADTool ctxt = context.getOwner();

                if (ctxt.isPolygonLayer())
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_next_point")+", "+
				    PluginServices.getText(this,"arc")+
				    "["+PluginServices.getText(this,"PolylineCADTool.arc")+"], "+
				    PluginServices.getText(this,"cad.or")+" "+
				    PluginServices.getText(this,"end")+
				   	"["+PluginServices.getText(this,"PolylineCADTool.end")+"]");
                        ctxt.setDescription(new String[]{"inter_arc", "terminate", "cancel"});
                        ctxt.addPoint(pointX, pointY, event);
                    }
                    finally
                    {
                        context.setState(Polyline.NextPointOrArcOrClose);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_next_point")+", "+
				    PluginServices.getText(this,"arc")+
				    "["+PluginServices.getText(this,"PolylineCADTool.arc")+"], "+
				    PluginServices.getText(this,"close_polyline")+
				    "["+PluginServices.getText(this,"PolylineCADTool.close_polyline")+"], "+
				    PluginServices.getText(this,"cad.or")+" "+
				    PluginServices.getText(this,"end")+
				   	"["+PluginServices.getText(this,"PolylineCADTool.end")+"]");
                        ctxt.setDescription(new String[]{"inter_arc", "close_polyline", "terminate", "cancel"});
                        ctxt.addPoint(pointX, pointY, event);
                    }
                    finally
                    {
                        context.setState(Polyline.NextPointOrArcOrClose);
                        (context.getState()).Entry(context);
                    }
                }

                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Polyline_NextPointOrArcOrClose
            extends Polyline_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Polyline_NextPointOrArcOrClose(String name, int id)
            {
                super (name, id);
            }

            protected void addOption(PolylineCADToolContext context, String s)
            {
                PolylineCADTool ctxt = context.getOwner();

                if (ctxt.isPolygonLayer() && (s.equalsIgnoreCase(PluginServices.getText(this,"PolylineCADTool.arc")) || s.equals(PluginServices.getText(this,"inter_arc"))))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_next_point")+", "+
				PluginServices.getText(this,"line")+
				"["+PluginServices.getText(this,"PolylineCADTool.line")+"], "+
				PluginServices.getText(this,"cad.or")+" "+
				PluginServices.getText(this,"end")+
				"["+PluginServices.getText(this,"PolylineCADTool.end")+"]");
                        ctxt.setDescription(new String[]{"inter_line", "terminate", "cancel"});
                        ctxt.addOption(s);
                    }
                    finally
                    {
                        context.setState(Polyline.NextPointOrLineOrClose);
                        (context.getState()).Entry(context);
                    }
                }
                else if (s.equalsIgnoreCase(PluginServices.getText(this,"PolylineCADTool.arc")) || s.equals(PluginServices.getText(this,"inter_arc")))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_next_point")+", "+
				PluginServices.getText(this,"line")+
				"["+PluginServices.getText(this,"PolylineCADTool.line")+"], "+
				PluginServices.getText(this,"close_polyline")+
				"["+PluginServices.getText(this,"PolylineCADTool.close_polyline")+"], "+
				PluginServices.getText(this,"cad.or")+" "+
				PluginServices.getText(this,"end")+
				"["+PluginServices.getText(this,"PolylineCADTool.end")+"]");
                        ctxt.setDescription(new String[]{"inter_line", "close_polyline", "terminate", "cancel"});
                        ctxt.addOption(s);
                    }
                    finally
                    {
                        context.setState(Polyline.NextPointOrLineOrClose);
                        (context.getState()).Entry(context);
                    }
                }
                else if (s.equalsIgnoreCase(PluginServices.getText(this,"PolylineCADTool.close_polyline")) || s.equals(PluginServices.getText(this,"close_polyline")))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.addOption(s);
                        ctxt.closeGeometry();
                        ctxt.endGeometry();
                        ctxt.end();
                    }
                    finally
                    {
                        context.setState(Polyline.FirstPoint);
                        (context.getState()).Entry(context);
                    }
                }
                else if ((s.equalsIgnoreCase(PluginServices.getText(this,"PolylineCADTool.end")) || s.equals(PluginServices.getText(this,"terminate"))) && ctxt.getLinesCount()!=0 )
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.addOption(s);
                        ctxt.endGeometry();
                        ctxt.end();
                    }
                    finally
                    {
                        context.setState(Polyline.FirstPoint);
                        (context.getState()).Entry(context);
                    }
                }
                else if (!s.equals(PluginServices.getText(this,"cancel")))
                {

                    // No actions.
                }                else
                {
                    super.addOption(context, s);
                }

                return;
            }

            protected void addPoint(PolylineCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                PolylineCADTool ctxt = context.getOwner();

                if (ctxt.isPolygonLayer())
                {
                    PolylineCADToolState endState = context.getState();

                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_next_point")+", "+
				PluginServices.getText(this,"arc")+
				"["+PluginServices.getText(this,"PolylineCADTool.arc")+"], "+
				PluginServices.getText(this,"cad.or")+" "+
				PluginServices.getText(this,"end")+
				"["+PluginServices.getText(this,"PolylineCADTool.end")+"]");
                        ctxt.setDescription(new String[]{"inter_arc", "terminate", "cancel"});
                        ctxt.addPoint(pointX, pointY, event);
                    }
                    finally
                    {
                        context.setState(endState);
                    }
                }
                else
                {
                    PolylineCADToolState endState = context.getState();

                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_next_point")+", "+
				PluginServices.getText(this,"arc")+
				"["+PluginServices.getText(this,"PolylineCADTool.arc")+"], "+
				PluginServices.getText(this,"close_polyline")+
				"["+PluginServices.getText(this,"PolylineCADTool.close_polyline")+"], "+
				PluginServices.getText(this,"cad.or")+" "+
				PluginServices.getText(this,"end")+
				"["+PluginServices.getText(this,"PolylineCADTool.end")+"]");
                        ctxt.setDescription(new String[]{"inter_arc", "close_polyline", "terminate", "cancel"});
                        ctxt.addPoint(pointX, pointY, event);
                    }
                    finally
                    {
                        context.setState(endState);
                    }
                }

                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Polyline_NextPointOrLineOrClose
            extends Polyline_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Polyline_NextPointOrLineOrClose(String name, int id)
            {
                super (name, id);
            }

            protected void addOption(PolylineCADToolContext context, String s)
            {
                PolylineCADTool ctxt = context.getOwner();

                if (ctxt.isPolygonLayer() && (s.equalsIgnoreCase(PluginServices.getText(this,"PolylineCADTool.line")) || s.equals(PluginServices.getText(this,"inter_line"))))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_next_point")+", "+
				PluginServices.getText(this,"arc")+
				"["+PluginServices.getText(this,"PolylineCADTool.arc")+"], "+
				PluginServices.getText(this,"cad.or")+" "+
				PluginServices.getText(this,"end")+
				"["+PluginServices.getText(this,"PolylineCADTool.end")+"]");
                        ctxt.setDescription(new String[]{"inter_arc", "terminate", "cancel"});
                        ctxt.addOption(s);
                    }
                    finally
                    {
                        context.setState(Polyline.NextPointOrArcOrClose);
                        (context.getState()).Entry(context);
                    }
                }
                else if (s.equalsIgnoreCase(PluginServices.getText(this,"PolylineCADTool.line")) || s.equals(PluginServices.getText(this,"inter_line")))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_next_point")+", "+
				PluginServices.getText(this,"arc")+
				"["+PluginServices.getText(this,"PolylineCADTool.arc")+"], "+
				PluginServices.getText(this,"close_polyline")+
				"["+PluginServices.getText(this,"PolylineCADTool.close_polyline")+"], "+
				PluginServices.getText(this,"cad.or")+" "+
				PluginServices.getText(this,"end")+
				"["+PluginServices.getText(this,"PolylineCADTool.end")+"]");
                        ctxt.setDescription(new String[]{"inter_arc", "close_polyline", "terminate", "cancel"});
                        ctxt.addOption(s);
                    }
                    finally
                    {
                        context.setState(Polyline.NextPointOrArcOrClose);
                        (context.getState()).Entry(context);
                    }
                }
                else if (s.equalsIgnoreCase(PluginServices.getText(this,"PolylineCADTool.close_polyline")) || s.equals(PluginServices.getText(this,"close_polyline")))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.addOption(s);
                        ctxt.closeGeometry();
                        ctxt.endGeometry();
                        ctxt.end();
                    }
                    finally
                    {
                        context.setState(Polyline.FirstPoint);
                        (context.getState()).Entry(context);
                    }
                }
                else if ((s.equalsIgnoreCase(PluginServices.getText(this,"PolylineCADTool.end")) || s.equals(PluginServices.getText(this,"terminate"))) && ctxt.getLinesCount()!=0)
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.addOption(s);
                        ctxt.endGeometry();
                        ctxt.end();
                    }
                    finally
                    {
                        context.setState(Polyline.FirstPoint);
                        (context.getState()).Entry(context);
                    }
                }
                else if (!s.equals(PluginServices.getText(this,"cancel")))
                {

                    // No actions.
                }                else
                {
                    super.addOption(context, s);
                }

                return;
            }

            protected void addPoint(PolylineCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                PolylineCADTool ctxt = context.getOwner();

                if (ctxt.isPolygonLayer())
                {
                    PolylineCADToolState endState = context.getState();

                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_next_point")+", "+
				PluginServices.getText(this,"line")+
				"["+PluginServices.getText(this,"PolylineCADTool.line")+"], "+
				PluginServices.getText(this,"cad.or")+" "+
				PluginServices.getText(this,"end")+
				"["+PluginServices.getText(this,"PolylineCADTool.end")+"]");
                        ctxt.setDescription(new String[]{"inter_line", "terminate", "cancel"});
                        ctxt.addPoint(pointX, pointY, event);
                    }
                    finally
                    {
                        context.setState(endState);
                    }
                }
                else
                {
                    PolylineCADToolState endState = context.getState();

                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_next_point")+", "+
				PluginServices.getText(this,"line")+
				"["+PluginServices.getText(this,"PolylineCADTool.line")+"], "+
				PluginServices.getText(this,"close_polyline")+
				"["+PluginServices.getText(this,"PolylineCADTool.close_polyline")+"], "+
				PluginServices.getText(this,"cad.or")+" "+
				PluginServices.getText(this,"end")+
				"["+PluginServices.getText(this,"PolylineCADTool.end")+"]");
                        ctxt.setDescription(new String[]{"inter_line", "close_polyline", "terminate", "cancel"});
                        ctxt.addPoint(pointX, pointY, event);
                    }
                    finally
                    {
                        context.setState(endState);
                    }
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
