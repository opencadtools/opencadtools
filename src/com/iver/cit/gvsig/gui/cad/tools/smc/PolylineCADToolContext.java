
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.PolylineCADTool;
import java.awt.event.InputEvent;

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

            if (s.equals("Cancelar"))
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
            }            else
            {
                super.addOption(context, s);
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

                ctxt.setQuestion("POLILINEA" + "\n" +
		"Insertar primer punto");
                ctxt.setDescription(new String[]{"Cancelar"});
                return;
            }

            protected void addPoint(PolylineCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                PolylineCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar siguiente punto, Arco[A] o Cerrar[C]");
                    ctxt.setDescription(new String[]{"Arco", "Cerrar", "Cancelar"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Polyline.NextPointOrArcOrClose);
                    (context.getState()).Entry(context);
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

                if (s.equals("A") ||  s.equals("a") || s.equals("Arco"))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("Insertar punto siguiente, Linea[N] o Cerrar[C]");
                        ctxt.setDescription(new String[]{"Linea", "Cerrar", "Cancelar"});
                        ctxt.addOption(s);
                    }
                    finally
                    {
                        context.setState(Polyline.NextPointOrLineOrClose);
                        (context.getState()).Entry(context);
                    }
                }
                else if (s.equals("C") ||  s.equals("c") || s.equals("Cerrar"))
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
                }                else
                {
                    super.addOption(context, s);
                }

                return;
            }

            protected void addPoint(PolylineCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                PolylineCADTool ctxt = context.getOwner();

                PolylineCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar siguiente punto, Arco[A] o Cerrar[C]");
                    ctxt.setDescription(new String[]{"Arco", "Cerrar", "Cancelar"});
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

                if (s.equals("N") ||  s.equals("n") || s.equals("Linea"))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion("Insertar siguiente punto, Arco[A] o Cerrar[C]");
                        ctxt.setDescription(new String[]{"Arco", "Cerrar", "Cancelar"});
                        ctxt.addOption(s);
                    }
                    finally
                    {
                        context.setState(Polyline.NextPointOrArcOrClose);
                        (context.getState()).Entry(context);
                    }
                }
                else if (s.equals("C") ||  s.equals("c") || s.equals("Cerrar"))
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
                }                else
                {
                    super.addOption(context, s);
                }

                return;
            }

            protected void addPoint(PolylineCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                PolylineCADTool ctxt = context.getOwner();

                PolylineCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion("Insertar punto siguiente, Linea[N] o Cerrar[C]");
                    ctxt.setDescription(new String[]{"Linea", "Cerrar", "Cancelar"});
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
