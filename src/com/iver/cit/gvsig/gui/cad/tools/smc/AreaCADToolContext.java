/*
 * Copyright 2008 Deputación Provincial de A Coruña
 * Copyright 2009 Deputación Provincial de Pontevedra
 * Copyright 2010 CartoLab, Universidad de A Coruña
 *
 * This file is part of openCADTools, developed by the Cartography
 * Engineering Laboratory of the University of A Coruña (CartoLab).
 * http://www.cartolab.es
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 */


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.AreaCADTool;

import java.awt.event.InputEvent;
import com.iver.andami.PluginServices;

/**
 * @author Isabel P?rez-Urria Lage [LBD]
 * @author Javier Estévez [Cartolab]
 */
public final class AreaCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

	private static Preferences prefs = Preferences.userRoot().node( "cadtooladapter" );

    public AreaCADToolContext(AreaCADTool owner)
    {
        super();

        _owner = owner;
        setState(Area.FirstPoint);
        Area.FirstPoint.Entry(this);
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

    public void removePoint(InputEvent event, int numPoints)
    {
        _transition = "removePoint";
        getState().removePoint(this, event, numPoints);
        _transition = "";
        return;
    }

    public AreaCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((AreaCADToolState) _state);
    }

    protected AreaCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private AreaCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class AreaCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected AreaCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(AreaCADToolContext context) {}
        protected void Exit(AreaCADToolContext context) {}

        protected abstract String[] getDescription();

        protected void addOption(AreaCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(AreaCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void addValue(AreaCADToolContext context, double d)
        {
            Default(context);
        }

        protected void removePoint(AreaCADToolContext context, InputEvent event, int numPoints)
        {
            Default(context);
        }

        protected void Default(AreaCADToolContext context)
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

    /* package */ static abstract class Area
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
        /* package */ static Area_Default.Area_FirstPoint FirstPoint;
        /* package */ static Area_Default.Area_SecondPoint SecondPoint;
        /* package */ static Area_Default.Area_ThirdPoint ThirdPoint;
        /* package */ static Area_Default.Area_NextPoint NextPoint;
        /* package */ static Area_Default.Area_NextGeometry NextGeometry;
        /* package */ static Area_Default.Area_HoleOrNextPolygon HoleOrNextPolygon;
        /* package */ static Area_Default.Area_EditForm EditForm;
        private static Area_Default Default;

        static
        {
            FirstPoint = new Area_Default.Area_FirstPoint("Area.FirstPoint", 0);
            SecondPoint = new Area_Default.Area_SecondPoint("Area.SecondPoint", 1);
            ThirdPoint = new Area_Default.Area_ThirdPoint("Area.ThirdPoint", 2);
            NextPoint = new Area_Default.Area_NextPoint("Area.NextPoint", 3);
            NextGeometry = new Area_Default.Area_NextGeometry("Area.NextGeometry", 4);
            HoleOrNextPolygon = new Area_Default.Area_HoleOrNextPolygon("Area.HoleOrNextPolygon", 5);
            EditForm = new Area_Default.Area_EditForm("Area.EditForm", 6);
            Default = new Area_Default("Area.Default", -1);
        }

    }

    protected static class Area_Default
        extends AreaCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected Area_Default(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(AreaCADToolContext context) {
        	context.getOwner().setDescription(getDescription());
        }

        protected void addOption(AreaCADToolContext context, String s)
        {
            AreaCADTool ctxt = context.getOwner();

            if (s.equals("espacio") || s.equals(PluginServices.getText(this, "terminate")))
            {
                AreaCADToolState endState  = context.getState();

                context.clearState();
                try
                {
                    ctxt.throwInvalidGeometryException(PluginServices.getText(this,"incorrect_geometry"));
                }
                finally
                {
                    context.setState(endState);
                }
            }
            else if (s.equals("C")||s.equals("c")||s.equals(PluginServices.getText(this,"cancel")))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        Area.FirstPoint.getName());

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
                    context.setState(Area.FirstPoint);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }
            else
            {
                AreaCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.throwOptionException(PluginServices.getText(this,"incorrect_option"), s);
                }
                finally
                {
                    context.setState(endState);
                }
            }

            return;
        }

        protected void addValue(AreaCADToolContext context, double d)
        {
            AreaCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Area.FirstPoint.getName());

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
                context.setState(Area.FirstPoint);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void addPoint(AreaCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            AreaCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Area.FirstPoint.getName());

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
                context.setState(Area.FirstPoint);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void removePoint(AreaCADToolContext context, InputEvent event, int numPoints)
        {
            AreaCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    Area.FirstPoint.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.clearState();
            try
            {
                ctxt.throwNoPointsException(PluginServices.getText(this,"no_points"));
            }
            finally
            {
                context.setState(Area.FirstPoint);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

		@Override
		protected String[] getDescription() {
			// TODO Auto-generated method stub
			return new String[]{"cancel"};
		}

    //-----------------------------------------------------------
    // Inner classse.
    //


        private static final class Area_FirstPoint
            extends Area_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Area_FirstPoint(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(AreaCADToolContext context)
            {
            	super.Entry(context);
                AreaCADTool ctxt = context.getOwner();

                ctxt.setQuestion(PluginServices.getText(this,"insert_first_point"));
                ctxt.setDescription(getDescription());
                return;
            }

            protected void addPoint(AreaCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                AreaCADTool ctxt = context.getOwner();

                if (ctxt.pointInsidePolygon(pointX,pointY))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_next_point"));
                        ctxt.addPoint(pointX, pointY, event);
                    }
                    finally
                    {
                        context.setState(Area.SecondPoint);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {
                    AreaCADToolState endState = context.getState();

                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_first_point"));
                        ctxt.throwInvalidGeometryException(PluginServices.getText(this,"multipolygon_forbidden"));
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

        private static final class Area_SecondPoint
            extends Area_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Area_SecondPoint(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(AreaCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                AreaCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_next_point"));
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Area.ThirdPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void removePoint(AreaCADToolContext context, InputEvent event, int numPoints)
            {
                AreaCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_first_point"));
                    ctxt.removePoint(event);
                }
                finally
                {
                    context.setState(Area.FirstPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Area_ThirdPoint
            extends Area_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Area_ThirdPoint(String name, int id)
            {
                super (name, id);
            }

//            protected String[] getDescription() {
//            	return new String[]{"terminate", "cancel"};
//            }

            protected void addPoint(AreaCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                AreaCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                	boolean deleteButton3 = prefs.getBoolean("isDeleteButton3", true);
                	if (deleteButton3) {
                		ctxt.setQuestion(PluginServices.getText(this,"insert_next_point_or_hole_del"));
                	} else {
                		ctxt.setQuestion(PluginServices.getText(this,"insert_next_point_or_hole"));
                	}
//                    ctxt.setDescription(new String[]{"terminate", "cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(Area.NextPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void removePoint(AreaCADToolContext context, InputEvent event, int numPoints)
            {
                AreaCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_next_point"));
                    ctxt.removePoint(event);
                }
                finally
                {
                    context.setState(Area.SecondPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Area_NextPoint
            extends Area_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Area_NextPoint(String name, int id)
            {
                super (name, id);
            }

            protected String[] getDescription() {
            	return new String[]{"terminate", "next", "cancel"};
            }

//            protected void Entry(AreaCADToolContext context)
//            {
//                AreaCADTool ctxt = context.getOwner();
//
//                //ctxt.clearCompoundGeom();
//                return;
//            }

            protected void addOption(AreaCADToolContext context, String s)
            {
                AreaCADTool ctxt = context.getOwner();

                if (s.equals("espacio") || s.equals(PluginServices.getText(this, "terminate"))) //NACHOV && ctxt.hasNextGeometry() && ctxt.checksOnInsertion(ctxt.getCurrentGeom()))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.saveTempGeometry();
                        //ctxt.digitalizeNextGeometry();
                        ctxt.setQuestion(PluginServices.getText(this, "insert_first_point"));
                    }
                    finally
                    {
                        context.setState(Area.NextGeometry);
                        (context.getState()).Entry(context);
                        ctxt.clear();
                    }
                }
//                else if (s.equals("espacio")) //NACHOV && (!ctxt.hasNextGeometry()) && ctxt.checksOnInsertion(ctxt.getCurrentGeom()))
//                {
//
//                    (context.getState()).Exit(context);
//                    context.clearState();
//                    try
//                    {
//                        ctxt.saveTempGeometry();
////                        ctxt.updateFormConstants();
////                        ctxt.openForm();
//                    }
//                    finally
//                    {
//                        context.setState(Area.EditForm);
//                        (context.getState()).Entry(context);
//                    }
//                }
//                else
//                if (s.equals("espacio") && ctxt.isErrorOnIntersection())
//                {
//
//                    (context.getState()).Exit(context);
//                    context.clearState();
//                    try
//                    {
//                        ctxt.throwInvalidGeometryException(PluginServices.getText(this,"incorrect_geometry"));
//                        ctxt.setErrorOnIntersection(false);
//                        ctxt.cancel();
//                    }
//                    finally
//                    {
//                        context.setState(Area.FirstPoint);
//                        (context.getState()).Entry(context);
//                    }
//                }
//                else if (s.equals("espacio"))
//                {
//
//                    (context.getState()).Exit(context);
//                    context.clearState();
//                    try
//                    {
//                        ctxt.cancel();
//                    }
//                    finally
//                    {
//                        context.setState(Area.FirstPoint);
//                        (context.getState()).Entry(context);
//                    }
//                }
                else if (s.equals("tab")|| s.equals(PluginServices.getText(this, "next")))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.saveTempGeometry();
                        ctxt.clearPoints();
                        ctxt.setQuestion(PluginServices.getText(this,"insert_hole_or_polygon"));
                    }
                    finally
                    {
                        context.setState(Area.HoleOrNextPolygon);
                        (context.getState()).Entry(context);
                    }
                }                else
                {
                    super.addOption(context, s);
                }

                return;
            }

            protected void addPoint(AreaCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                AreaCADTool ctxt = context.getOwner();

                AreaCADToolState endState = context.getState();

                context.clearState();
                try
                {
                	boolean deleteButton3 = prefs.getBoolean("isDeleteButton3", true);
                	if (deleteButton3) {
                		ctxt.setQuestion(PluginServices.getText(this,"insert_next_point_or_hole_del"));
                	} else {
                		ctxt.setQuestion(PluginServices.getText(this,"insert_next_point_or_hole"));
                	}
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void removePoint(AreaCADToolContext context, InputEvent event, int numPoints)
            {
                AreaCADTool ctxt = context.getOwner();

                if (numPoints>3)
                {
                    AreaCADToolState endState = context.getState();

                    context.clearState();
                    try
                    {
                        ctxt.removePoint(event);
                    }
                    finally
                    {
                        context.setState(endState);
                    }
                }
                else if (numPoints==3)
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.removePoint(event);
                    }
                    finally
                    {
                        context.setState(Area.ThirdPoint);
                        (context.getState()).Entry(context);
                    }
                }                else
                {
                    super.removePoint(context, event, numPoints);
                }

                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Area_NextGeometry
            extends Area_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Area_NextGeometry(String name, int id)
            {
                super (name, id);
            }

            protected void addOption(AreaCADToolContext context, String s)
            {
                AreaCADTool ctxt = context.getOwner();

                if (s.equals("C")||s.equals("c")||s.equals(PluginServices.getText(this,"cancel")))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.deleteFromVea();
                        ctxt.cancel();
                    }
                    finally
                    {
                        context.setState(Area.FirstPoint);
                        (context.getState()).Entry(context);
                    }
                }
                else if (s.equals("espacio") || s.equals(PluginServices.getText(this, "terminate")))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
//                        ctxt.updateFormConstants();
//                        ctxt.openForm();
                    }
                    finally
                    {
                        context.setState(Area.EditForm);
                        (context.getState()).Entry(context);
                    }
                }                else
                {
                    super.addOption(context, s);
                }

                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Area_HoleOrNextPolygon
            extends Area_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Area_HoleOrNextPolygon(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(AreaCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                AreaCADTool ctxt = context.getOwner();

                if (ctxt.pointInsidePolygon(pointX,pointY))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_next_point"));
                        ctxt.addPoint(pointX, pointY, event);
                        ctxt.setHole(true);
                    }
                    finally
                    {
                        context.setState(Area.SecondPoint);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {
                	(context.getState()).Exit(context);
//                    AreaCADToolState endState = context.getState();

                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_next_point"));
                        ctxt.addPoint(pointX, pointY, event);
//                        ctxt.throwInvalidGeometryException(PluginServices.getText(this,"multipolygon_forbidden"));
                    }
                    finally
                    {
                        context.setState(Area.SecondPoint);
                        (context.getState()).Entry(context);
                    }
                }

                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Area_EditForm
            extends Area_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Area_EditForm(String name, int id)
            {
                super (name, id);
            }

            protected void addOption(AreaCADToolContext context, String s)
            {
                AreaCADTool ctxt = context.getOwner();

                if (s.equals(PluginServices.getText(this,"accept_form"))) //NACHOV && ctxt.hasNextGeometry())
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.save();
                        //ctxt.saveNext();
                        ctxt.setQuestion(PluginServices.getText(this,"insert_first_point"));
                    }
                    finally
                    {
                        context.setState(Area.FirstPoint);
                        (context.getState()).Entry(context);
                    }
                }
                else if (s.equals(PluginServices.getText(this,"accept_form")))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.save();
                        ctxt.setQuestion(PluginServices.getText(this,"insert_first_point"));
                    }
                    finally
                    {
                        context.setState(Area.FirstPoint);
                        (context.getState()).Entry(context);
                    }
                }
                else if (s.equals(PluginServices.getText(this,"cancel_form")))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.cancelInsertion();
                        ctxt.initializeFormState();
                        ctxt.setQuestion(PluginServices.getText(this,"insert_next_point"));
                    }
                    finally
                    {
                        context.setState(Area.NextPoint);
                        (context.getState()).Entry(context);
                    }
                }                else
                {
                    super.addOption(context, s);
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
