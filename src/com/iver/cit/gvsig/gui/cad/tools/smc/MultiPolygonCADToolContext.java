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

import java.awt.event.InputEvent;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.gui.cad.tools.MultiPolygonCADTool;

/**
 * @author Isabel Pérez-Urria Lage [LBD]
 * @author Nacho Varela [Cartolab]
 * @author Pablo Sanxiao [CartoLab]
 */
public final class MultiPolygonCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public MultiPolygonCADToolContext(MultiPolygonCADTool owner)
    {
        super();

        _owner = owner;
        switch (_owner.getPointsCount()) {
        case 0 :
        	setState(MultiArea.FirstPoint);
        	MultiArea.FirstPoint.Entry(this);
        	break;
        case 1 :
        	setState(MultiArea.SecondPoint);
        	MultiArea.SecondPoint.Entry(this);
        	break;
        case 2 :
        	setState(MultiArea.ThirdPoint);
        	MultiArea.ThirdPoint.Entry(this);
        	break;
        default :
        	setState(MultiArea.NextPoint);
    		MultiArea.NextPoint.Entry(this);
    		break;
        }
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

    public MultiAreaCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((MultiAreaCADToolState) _state);
    }

    protected MultiPolygonCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private MultiPolygonCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class MultiAreaCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected MultiAreaCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(MultiPolygonCADToolContext context) {
        	context.getOwner().setDescription(getDescription());
        }
        protected void Exit(MultiPolygonCADToolContext context) {}

        protected abstract String[] getDescription();

        protected void addOption(MultiPolygonCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(MultiPolygonCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void addValue(MultiPolygonCADToolContext context, double d)
        {
            Default(context);
        }

        protected void removePoint(MultiPolygonCADToolContext context, InputEvent event, int numPoints)
        {
            Default(context);
        }

        protected void Default(MultiPolygonCADToolContext context)
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

    /* package */ static abstract class MultiArea
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
        /* package */ static MultiArea_Default.MultiArea_FirstPoint FirstPoint;
        /* package */ static MultiArea_Default.MultiArea_SecondPoint SecondPoint;
        /* package */ static MultiArea_Default.MultiArea_ThirdPoint ThirdPoint;
        /* package */ static MultiArea_Default.MultiArea_NextPoint NextPoint;
        /* package */ static MultiArea_Default.MultiArea_HoleOrNextPolygon HoleOrNextPolygon;
        /* package */ static MultiArea_Default.MultiArea_EditForm EditForm;
        private static MultiArea_Default Default;

        static
        {
            FirstPoint = new MultiArea_Default.MultiArea_FirstPoint("MultiArea.FirstPoint", 0);
            SecondPoint = new MultiArea_Default.MultiArea_SecondPoint("MultiArea.SecondPoint", 1);
            ThirdPoint = new MultiArea_Default.MultiArea_ThirdPoint("MultiArea.ThirdPoint", 2);
            NextPoint = new MultiArea_Default.MultiArea_NextPoint("MultiArea.NextPoint", 3);
            HoleOrNextPolygon = new MultiArea_Default.MultiArea_HoleOrNextPolygon("MultiArea.HoleOrNextPolygon", 4);
            EditForm = new MultiArea_Default.MultiArea_EditForm("MultiArea.EditForm", 5);
            Default = new MultiArea_Default("MultiArea.Default", -1);
        }

    }

    protected static class MultiArea_Default
        extends MultiAreaCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected MultiArea_Default(String name, int id)
        {
            super (name, id);
        }

        protected String[] getDescription() {
        	return new String[]{"cancel"};
        }

        protected void addOption(MultiPolygonCADToolContext context, String s)
        {
            MultiPolygonCADTool ctxt = context.getOwner();

            if (s.equals("espacio") || s.equals(PluginServices.getText(this, "terminate")))
            {
                MultiAreaCADToolState endState = context.getState();

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
                        MultiArea.FirstPoint.getName());

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
                    context.setState(MultiArea.FirstPoint);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }
            else
            {
                MultiAreaCADToolState endState = context.getState();

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

        protected void addValue(MultiPolygonCADToolContext context, double d)
        {
            MultiPolygonCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    MultiArea.FirstPoint.getName());

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
                context.setState(MultiArea.FirstPoint);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void addPoint(MultiPolygonCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            MultiPolygonCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    MultiArea.FirstPoint.getName());

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
                context.setState(MultiArea.FirstPoint);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void removePoint(MultiPolygonCADToolContext context, InputEvent event, int numPoints)
        {
            MultiPolygonCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    MultiArea.FirstPoint.getName());

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
                context.setState(MultiArea.FirstPoint);

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


        private static final class MultiArea_FirstPoint
            extends MultiArea_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private MultiArea_FirstPoint(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(MultiPolygonCADToolContext context)
            {
                MultiPolygonCADTool ctxt = context.getOwner();

                ctxt.setQuestion(PluginServices.getText(this,"insert_first_point"));
                ctxt.setDescription(getDescription());
                return;
            }

            protected void addPoint(MultiPolygonCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                MultiPolygonCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_next_point"));
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(MultiArea.SecondPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class MultiArea_SecondPoint
            extends MultiArea_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private MultiArea_SecondPoint(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(MultiPolygonCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                MultiPolygonCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_next_point"));
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(MultiArea.ThirdPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void removePoint(MultiPolygonCADToolContext context, InputEvent event, int numPoints)
            {
                MultiPolygonCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_first_point"));
                    ctxt.removePoint(event);
                }
                finally
                {
                    context.setState(MultiArea.FirstPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class MultiArea_ThirdPoint
            extends MultiArea_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private MultiArea_ThirdPoint(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(MultiPolygonCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                MultiPolygonCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_next_point_or_hole_or_polygon"));
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally 
                {
                    context.setState(MultiArea.NextPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void removePoint(MultiPolygonCADToolContext context, InputEvent event, int numPoints)
            {
                MultiPolygonCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_first_point"));
                    ctxt.removePoint(event);
                }
                finally
                {
                    context.setState(MultiArea.SecondPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class MultiArea_NextPoint
            extends MultiArea_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private MultiArea_NextPoint(String name, int id)
            {
                super (name, id);
            }

            protected String[] getDescription() {
            	return new String[]{"terminate", "next", "cancel"};
            }

            protected void addOption(MultiPolygonCADToolContext context, String s)
            {
                MultiPolygonCADTool ctxt = context.getOwner();

                if (s.equals("espacio")|| s.equals(PluginServices.getText(this, "terminate")))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.saveTempGeometry();
//                        ctxt.openForm();
                        ctxt.setQuestion(PluginServices.getText(this,"insert_first_point"));
                    }
                    finally
                    {
                        context.setState(MultiArea.FirstPoint);
                        (context.getState()).Entry(context);
//                        if (ActivateFormsExtension.getActivated()) {
//                        	VectorialLayerEdited vle=ctxt.getVLE();
//                        	OpenFormsExtension.openForm(vle);
//                    	}
                        ctxt.clear();
                    }
                }
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
                        context.setState(MultiArea.HoleOrNextPolygon);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {
                    super.addOption(context, s);
                }

                return;
            }

            protected void addPoint(MultiPolygonCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                MultiPolygonCADTool ctxt = context.getOwner();

                MultiAreaCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"insert_next_point_or_hole_or_polygon"));
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void removePoint(MultiPolygonCADToolContext context, InputEvent event, int numPoints)
            {
                MultiPolygonCADTool ctxt = context.getOwner();

                if (numPoints>3)
                {
                    MultiAreaCADToolState endState = context.getState();

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
                        context.setState(MultiArea.ThirdPoint);
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

        private static final class MultiArea_HoleOrNextPolygon
            extends MultiArea_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private MultiArea_HoleOrNextPolygon(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(MultiPolygonCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                MultiPolygonCADTool ctxt = context.getOwner();

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
                        context.setState(MultiArea.SecondPoint);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setQuestion(PluginServices.getText(this,"insert_next_point"));
                        ctxt.addPoint(pointX, pointY, event);
                        ctxt.setHole(false);
                    }
                    finally
                    {
                        context.setState(MultiArea.SecondPoint);
                        (context.getState()).Entry(context);
                    }
                }

                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class MultiArea_EditForm
            extends MultiArea_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private MultiArea_EditForm(String name, int id)
            {
                super (name, id);
            }

            protected void addOption(MultiPolygonCADToolContext context, String s)
            {
                MultiPolygonCADTool ctxt = context.getOwner();

                if (s.equals(PluginServices.getText(this,"accept_form")))
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
                        context.setState(MultiArea.FirstPoint);
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
                        context.setState(MultiArea.NextPoint);
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
