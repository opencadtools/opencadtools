
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.PolygonCADTool;
import java.awt.event.InputEvent;

public final class PolygonCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public PolygonCADToolContext(PolygonCADTool owner)
    {
        super();

        _owner = owner;
        setState(Polygon.NumberOrCenterPoint);
        Polygon.NumberOrCenterPoint.Entry(this);
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

    public PolygonCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((PolygonCADToolState) _state);
    }

    protected PolygonCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private PolygonCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class PolygonCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected PolygonCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(PolygonCADToolContext context) {}
        protected void Exit(PolygonCADToolContext context) {}

        protected void addOption(PolygonCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(PolygonCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void addValue(PolygonCADToolContext context, double d)
        {
            Default(context);
        }

        protected void Default(PolygonCADToolContext context)
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

    /* package */ static abstract class Polygon
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
        /* package */ static Polygon_Default.Polygon_NumberOrCenterPoint NumberOrCenterPoint;
        /* package */ static Polygon_Default.Polygon_CenterPoint CenterPoint;
        /* package */ static Polygon_Default.Polygon_OptionOrRadiusOrPoint OptionOrRadiusOrPoint;
        /* package */ static Polygon_Default.Polygon_RadiusOrPoint RadiusOrPoint;
        private static Polygon_Default Default;

        static
        {
            NumberOrCenterPoint = new Polygon_Default.Polygon_NumberOrCenterPoint("Polygon.NumberOrCenterPoint", 0);
            CenterPoint = new Polygon_Default.Polygon_CenterPoint("Polygon.CenterPoint", 1);
            OptionOrRadiusOrPoint = new Polygon_Default.Polygon_OptionOrRadiusOrPoint("Polygon.OptionOrRadiusOrPoint", 2);
            RadiusOrPoint = new Polygon_Default.Polygon_RadiusOrPoint("Polygon.RadiusOrPoint", 3);
            Default = new Polygon_Default("Polygon.Default", -1);
        }

    }

    protected static class Polygon_Default
        extends PolygonCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected Polygon_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(PolygonCADToolContext context, String s)
        {
            PolygonCADTool ctxt = context.getOwner();

            if (s.equals("Cancelar"))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        Polygon.NumberOrCenterPoint.getName());

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
                    context.setState(Polygon.NumberOrCenterPoint);

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


        private static final class Polygon_NumberOrCenterPoint
            extends Polygon_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Polygon_NumberOrCenterPoint(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(PolygonCADToolContext context)
            {
                PolygonCADTool ctxt = context.getOwner();

                ctxt.setQuestion("POLIGONO" + "\n" +
		"Insertar numero de lados<5>");
                ctxt.setDescription(new String[]{"Cancelar"});
                return;
            }

            protected void addPoint(PolygonCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                PolygonCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Inscrito en el c?rculo[I] o Circunscrito[C]<C>");
                    ctxt.setDescription(new String[]{"Inscrito", "Circunscrito", "Cancelar"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Polygon.OptionOrRadiusOrPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addValue(PolygonCADToolContext context, double d)
            {
                PolygonCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar punto central del poligono");
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addValue(d);
                }
                finally
                {
                    context.setState(Polygon.CenterPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Polygon_CenterPoint
            extends Polygon_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Polygon_CenterPoint(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(PolygonCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                PolygonCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Inscrito en el c?rculo[I] o Circunscrito[C]<C>");
                    ctxt.setDescription(new String[]{"Inscrito", "Circunscrito", "Cancelar"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Polygon.OptionOrRadiusOrPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Polygon_OptionOrRadiusOrPoint
            extends Polygon_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Polygon_OptionOrRadiusOrPoint(String name, int id)
            {
                super (name, id);
            }

            protected void addOption(PolygonCADToolContext context, String s)
            {
                PolygonCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Precise r?dio(r)");
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addOption(s);
                }
                finally
                {
                    context.setState(Polygon.RadiusOrPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addPoint(PolygonCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                PolygonCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addPoint(pointX, pointY, event);
                    ctxt.end();
                }
                finally
                {
                    context.setState(Polygon.NumberOrCenterPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addValue(PolygonCADToolContext context, double d)
            {
                PolygonCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addValue(d);
                    ctxt.end();
                }
                finally
                {
                    context.setState(Polygon.NumberOrCenterPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Polygon_RadiusOrPoint
            extends Polygon_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Polygon_RadiusOrPoint(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(PolygonCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                PolygonCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addPoint(pointX, pointY, event);
                    ctxt.end();
                }
                finally
                {
                    context.setState(Polygon.NumberOrCenterPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addValue(PolygonCADToolContext context, double d)
            {
                PolygonCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addValue(d);
                    ctxt.end();
                }
                finally
                {
                    context.setState(Polygon.NumberOrCenterPoint);
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
