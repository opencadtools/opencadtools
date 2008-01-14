
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.ScaleCADTool;
import java.awt.event.InputEvent;
import com.iver.andami.PluginServices;

public final class ScaleCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public ScaleCADToolContext(ScaleCADTool owner)
    {
        super();

        _owner = owner;
        setState(Scale.PointMain);
        Scale.PointMain.Entry(this);
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

    public ScaleCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((ScaleCADToolState) _state);
    }

    protected ScaleCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private ScaleCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class ScaleCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected ScaleCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(ScaleCADToolContext context) {}
        protected void Exit(ScaleCADToolContext context) {}

        protected void addOption(ScaleCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(ScaleCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void addValue(ScaleCADToolContext context, double d)
        {
            Default(context);
        }

        protected void Default(ScaleCADToolContext context)
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

    /* package */ static abstract class Scale
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
        /* package */ static Scale_Default.Scale_PointMain PointMain;
        /* package */ static Scale_Default.Scale_ScaleFactorOrReference ScaleFactorOrReference;
        /* package */ static Scale_Default.Scale_PointOriginOrScaleFactor PointOriginOrScaleFactor;
        /* package */ static Scale_Default.Scale_EndPointReference EndPointReference;
        /* package */ static Scale_Default.Scale_OriginPointScale OriginPointScale;
        /* package */ static Scale_Default.Scale_EndPointScale EndPointScale;
        private static Scale_Default Default;

        static
        {
            PointMain = new Scale_Default.Scale_PointMain("Scale.PointMain", 0);
            ScaleFactorOrReference = new Scale_Default.Scale_ScaleFactorOrReference("Scale.ScaleFactorOrReference", 1);
            PointOriginOrScaleFactor = new Scale_Default.Scale_PointOriginOrScaleFactor("Scale.PointOriginOrScaleFactor", 2);
            EndPointReference = new Scale_Default.Scale_EndPointReference("Scale.EndPointReference", 3);
            OriginPointScale = new Scale_Default.Scale_OriginPointScale("Scale.OriginPointScale", 4);
            EndPointScale = new Scale_Default.Scale_EndPointScale("Scale.EndPointScale", 5);
            Default = new Scale_Default("Scale.Default", -1);
        }

    }

    protected static class Scale_Default
        extends ScaleCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected Scale_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(ScaleCADToolContext context, String s)
        {
            ScaleCADTool ctxt = context.getOwner();

            if (s.equals(PluginServices.getText(this,"cancel")))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        Scale.PointMain.getName());

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
                    context.setState(Scale.PointMain);

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
                        Scale.PointMain.getName());

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
                    context.setState(Scale.PointMain);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }

            return;
        }

        protected void addValue(ScaleCADToolContext context, double d)
        {
            ScaleCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Scale.PointMain.getName());

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
                context.setState(Scale.PointMain);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void addPoint(ScaleCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            ScaleCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Scale.PointMain.getName());

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
                context.setState(Scale.PointMain);

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


        private static final class Scale_PointMain
            extends Scale_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Scale_PointMain(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(ScaleCADToolContext context)
            {
                ScaleCADTool ctxt = context.getOwner();

                ctxt.selection();
                ctxt.setQuestion(PluginServices.getText(this,"insert_basis_point"));
                ctxt.setDescription(new String[]{"cancel"});
                return;
            }

            protected void addPoint(ScaleCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                ScaleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_factor")+ " "+
					PluginServices.getText(this,"cad.or")+" "+
					PluginServices.getText(this,"reference")+
					PluginServices.getText(this,"ScaleCADTool.reference"));
                    ctxt.setDescription(new String[]{"reference", "cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Scale.ScaleFactorOrReference);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Scale_ScaleFactorOrReference
            extends Scale_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Scale_ScaleFactorOrReference(String name, int id)
            {
                super (name, id);
            }

            protected void addOption(ScaleCADToolContext context, String s)
            {
                ScaleCADTool ctxt = context.getOwner();

                if (s.equals(null) || s.equals(""))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.addOption(s);
                        ctxt.end();
                        ctxt.refresh();
                    }
                    finally
                    {
                        context.setState(Scale.PointMain);
                        (context.getState()).Entry(context);
                    }
                }
                else if (s.equalsIgnoreCase(PluginServices.getText(this,"ScaleCADTool.reference")) || s.equals(PluginServices.getText(this,"reference")))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_reference_point")+ " "+
				PluginServices.getText(this,"cad.or")+" "+
				PluginServices.getText(this,"factor")+
				"["+PluginServices.getText(this,"ScaleCADTool.factor")+"]");
                        ctxt.setDescription(new String[]{"factor", "cancel"});
                    }
                    finally
                    {
                        context.setState(Scale.PointOriginOrScaleFactor);
                        (context.getState()).Entry(context);
                    }
                }                else
                {
                    super.addOption(context, s);
                }

                return;
            }

            protected void addPoint(ScaleCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                ScaleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addPoint(pointX, pointY, event);
                    ctxt.end();
                    ctxt.refresh();
                }
                finally
                {
                    context.setState(Scale.PointMain);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void addValue(ScaleCADToolContext context, double d)
            {
                ScaleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addValue(d);
                    ctxt.end();
                    ctxt.refresh();
                }
                finally
                {
                    context.setState(Scale.PointMain);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Scale_PointOriginOrScaleFactor
            extends Scale_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Scale_PointOriginOrScaleFactor(String name, int id)
            {
                super (name, id);
            }

            protected void addOption(ScaleCADToolContext context, String s)
            {
                ScaleCADTool ctxt = context.getOwner();

                if (s.equalsIgnoreCase(PluginServices.getText(this,"ScaleCADTool.factor")) || s.equals(PluginServices.getText(this,"factor")))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_factor")+ " "+
				PluginServices.getText(this,"cad.or")+" "+
				PluginServices.getText(this,"reference")+
				"["+PluginServices.getText(this,"ScaleCADTool.reference")+"]");
                        ctxt.setDescription(new String[]{"reference", "cancel"});
                        ctxt.addOption(s);
                    }
                    finally
                    {
                        context.setState(Scale.PointMain);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {
                    super.addOption(context, s);
                }

                return;
            }

            protected void addPoint(ScaleCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                ScaleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_last_point_reference"));
                    ctxt.setDescription(new String[]{"cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Scale.EndPointReference);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Scale_EndPointReference
            extends Scale_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Scale_EndPointReference(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(ScaleCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                ScaleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_first_point_scale"));
                    ctxt.setDescription(new String[]{"cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Scale.OriginPointScale);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Scale_OriginPointScale
            extends Scale_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Scale_OriginPointScale(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(ScaleCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                ScaleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_last_point_scale"));
                    ctxt.setDescription(new String[]{"cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Scale.EndPointScale);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Scale_EndPointScale
            extends Scale_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Scale_EndPointScale(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(ScaleCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                ScaleCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addPoint(pointX, pointY, event);
                    ctxt.end();
                    ctxt.refresh();
                }
                finally
                {
                    context.setState(Scale.PointMain);
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
