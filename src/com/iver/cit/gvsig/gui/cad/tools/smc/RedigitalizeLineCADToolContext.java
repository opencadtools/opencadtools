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
import com.iver.cit.gvsig.gui.cad.CADStatus;
import com.iver.cit.gvsig.gui.cad.tools.RedigitalizeLineCADTool;

/**
 * @author José Ignacio Lamas Fonte [LBD]
 * @author Nacho Varela [Cartolab]
 * @author Pablo Sanxiao [CartoLab]
 */

public final class RedigitalizeLineCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public RedigitalizeLineCADToolContext(RedigitalizeLineCADTool owner)
    {
        super();

        _owner = owner;
        setState(RedigitalizeLine.FirstPoint);
        RedigitalizeLine.FirstPoint.Entry(this);
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

    public void removePoint(InputEvent event, int numPoints)
    {
        _transition = "removePoint";
        getState().removePoint(this, event, numPoints);
        _transition = "";
        return;
    }

    public RedigitalizeLineCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((RedigitalizeLineCADToolState) _state);
    }

    protected RedigitalizeLineCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private RedigitalizeLineCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class RedigitalizeLineCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected RedigitalizeLineCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(RedigitalizeLineCADToolContext context) {}
        protected void Exit(RedigitalizeLineCADToolContext context) {}

        protected void addOption(RedigitalizeLineCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(RedigitalizeLineCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void removePoint(RedigitalizeLineCADToolContext context, InputEvent event, int numPoints)
        {
            Default(context);
        }

        protected void Default(RedigitalizeLineCADToolContext context)
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

    /* package */ static abstract class RedigitalizeLine
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
        /* package */ static RedigitalizeLine_Default.RedigitalizeLine_FirstPoint FirstPoint;
        /* package */ static RedigitalizeLine_Default.RedigitalizeLine_SecondPoint SecondPoint;
        /* package */ static RedigitalizeLine_Default.RedigitalizeLine_NextPoint NextPoint;
        private static RedigitalizeLine_Default Default;

        static
        {
            FirstPoint = new RedigitalizeLine_Default.RedigitalizeLine_FirstPoint("RedigitalizeLine.FirstPoint", 0);
            SecondPoint = new RedigitalizeLine_Default.RedigitalizeLine_SecondPoint("RedigitalizeLine.SecondPoint", 1);
            NextPoint = new RedigitalizeLine_Default.RedigitalizeLine_NextPoint("RedigitalizeLine.NextPoint", 2);
            Default = new RedigitalizeLine_Default("RedigitalizeLine.Default", -1);
        }

    }

    protected static class RedigitalizeLine_Default
        extends RedigitalizeLineCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected RedigitalizeLine_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addPoint(RedigitalizeLineCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            RedigitalizeLineCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    RedigitalizeLine.FirstPoint.getName());

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
                context.setState(RedigitalizeLine.FirstPoint);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void removePoint(RedigitalizeLineCADToolContext context, InputEvent event, int numPoints)
        {
            RedigitalizeLineCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    RedigitalizeLine.FirstPoint.getName());

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
                context.setState(RedigitalizeLine.FirstPoint);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void addOption(RedigitalizeLineCADToolContext context, String s)
        {
            RedigitalizeLineCADTool ctxt = context.getOwner();

            if (s.equals("C")||s.equals("c")||s.equals(PluginServices.getText(this,"cancel")))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        RedigitalizeLine.FirstPoint.getName());

                if (loopbackFlag == false)
                {
                    (context.getState()).Exit(context);
                }

                context.clearState();
                try
                {
                    ctxt.clear();
                }
                finally
                {
                    context.setState(RedigitalizeLine.FirstPoint);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }
            else
            {
                RedigitalizeLineCADToolState endState = context.getState();

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

    //-----------------------------------------------------------
    // Inner classse.
    //


        private static final class RedigitalizeLine_FirstPoint
            extends RedigitalizeLine_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private RedigitalizeLine_FirstPoint(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(RedigitalizeLineCADToolContext context)
            {
                RedigitalizeLineCADTool ctxt = context.getOwner();

                ctxt.setQuestion(PluginServices.getText(this,"redigitaliza_insert_first_point"));
                ctxt.setDescription(new String[]{"cancel"});
                return;
            }

            protected void addPoint(RedigitalizeLineCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                RedigitalizeLineCADTool ctxt = context.getOwner();

                if (ctxt.pointInsideFeature(pointX,pointY))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
			boolean deleteButton3 = CADStatus.getCADStatus()
				.isDeleteButtonActivated();
                    	if (deleteButton3) {
                    		ctxt.setQuestion(PluginServices.getText(this,"redigitaliza_insert_second_point_del"));
                    	} else {
                    		ctxt.setQuestion(PluginServices.getText(this,"redigitaliza_insert_second_point"));
                    	}
                    }
                    finally
                    {
                        context.setState(RedigitalizeLine.SecondPoint);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {
                    RedigitalizeLineCADToolState endState = context.getState();

                    context.clearState();
                    try
                    {
                        ctxt.throwPointException(PluginServices.getText(this,"redigitaliza_incorrect_point"), pointX, pointY);
                        ctxt.setQuestion(PluginServices.getText(this,"redigitaliza_insert_first_point"));
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

        private static final class RedigitalizeLine_SecondPoint
            extends RedigitalizeLine_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private RedigitalizeLine_SecondPoint(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(RedigitalizeLineCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                RedigitalizeLineCADTool ctxt = context.getOwner();

                if (ctxt.secondPointInsideFeature(pointX,pointY))
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
			boolean deleteButton3 = CADStatus.getCADStatus()
				.isDeleteButtonActivated();
                    	if (deleteButton3) {
                    		ctxt.setQuestion(PluginServices.getText(this,"redigitaliza_insert_other_point_del"));
                    	} else {
                    		ctxt.setQuestion(PluginServices.getText(this,"redigitaliza_insert_other_point"));
                    	}
                    }
                    finally
                    {
                        context.setState(RedigitalizeLine.NextPoint);
                        ctxt.setDescription(new String[]{"cancel", "terminate"});
                        (context.getState()).Entry(context);
                    }
                }
                else
                {
                    RedigitalizeLineCADToolState endState = context.getState();

                    context.clearState();
                    try
                    {
                        ctxt.throwPointException(PluginServices.getText(this,"redigitaliza_incorrect_point"), pointX, pointY);
			boolean deleteButton3 = CADStatus.getCADStatus()
				.isDeleteButtonActivated();
                    	if (deleteButton3) {
                    		ctxt.setQuestion(PluginServices.getText(this,"redigitaliza_insert_second_point_del"));
                    	} else {
                    		ctxt.setQuestion(PluginServices.getText(this,"redigitaliza_insert_second_point"));
                    	}
                    }
                    finally
                    {
                        context.setState(endState);
                    }
                }

                return;
            }

            protected void removePoint(RedigitalizeLineCADToolContext context, InputEvent event, int numPoints)
            {
                RedigitalizeLineCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"redigitaliza_insert_first_point"));
                    ctxt.removeFirstPoint(event);
                }
                finally
                {
                    context.setState(RedigitalizeLine.FirstPoint);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class RedigitalizeLine_NextPoint
            extends RedigitalizeLine_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private RedigitalizeLine_NextPoint(String name, int id)
            {
                super (name, id);
            }

            protected void addOption(RedigitalizeLineCADToolContext context, String s)
            {
                RedigitalizeLineCADTool ctxt = context.getOwner();

//               if ((((s.equals("g")||s.equals("G")))&& ctxt.checksOnEdition(ctxt.getGeometriaResultante(), ctxt.getCurrentGeoid())))
                if (s.equals("espacio") || s.equals(PluginServices.getText(this, "terminate"))) {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.saveChanges();
                        ctxt.setQuestion(PluginServices.getText(this,"redigitaliza_insert_first_point"));
                        ctxt.clear();
                    }
                    finally
                    {
                        context.setState(RedigitalizeLine.FirstPoint);
                        (context.getState()).Entry(context);
                    }
                }
 //               else if (s.equals("g")||s.equals("G"))
 //               {

                    // No actions.
  //              }
                else
                {
                    super.addOption(context, s);
                }

                return;
            }

            protected void addPoint(RedigitalizeLineCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                RedigitalizeLineCADTool ctxt = context.getOwner();

                RedigitalizeLineCADToolState endState = context.getState();

                context.clearState();
                try
                {
		    boolean deleteButton3 = CADStatus.getCADStatus()
			    .isDeleteButtonActivated();
                	if (deleteButton3) {
                		ctxt.setQuestion(PluginServices.getText(this,"redigitaliza_insert_other_point_del"));
                	} else {
                		ctxt.setQuestion(PluginServices.getText(this,"redigitaliza_insert_other_point"));
                	}
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void removePoint(RedigitalizeLineCADToolContext context, InputEvent event, int numPoints)
            {
                RedigitalizeLineCADTool ctxt = context.getOwner();

                if (numPoints>0)
                {
                    RedigitalizeLineCADToolState endState = context.getState();

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
                else if (numPoints==0)
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.removeSecondPoint(event);
			boolean deleteButton3 = CADStatus.getCADStatus()
				.isDeleteButtonActivated();
                    	if (deleteButton3) {
                    		ctxt.setQuestion(PluginServices.getText(this,"redigitaliza_insert_second_point_del"));
                    	} else {
                    		ctxt.setQuestion(PluginServices.getText(this,"redigitaliza_insert_second_point"));
                    	}
                    }
                    finally
                    {
                        context.setState(RedigitalizeLine.SecondPoint);
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

    //-----------------------------------------------------------
    // Member data.
    //
    }
}
