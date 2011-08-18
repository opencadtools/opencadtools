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
 *
*/


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.ActivateFormsExtension;
import com.iver.cit.gvsig.OpenFormsExtension;
import com.iver.cit.gvsig.gui.cad.tools.EIELPointCADTool;
import com.iver.cit.gvsig.gui.cad.tools.smc.EIELPolylineCADToolContext.Polyline;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;

import java.awt.event.InputEvent;
import com.iver.andami.PluginServices;

/**
 * @author Vicente Caballero Navarro
 * @author Laboratorio de Bases de Datos. Universidad de A Coruña                                                                                           
 * @author Cartolab. Universidad de A Coruña
 */

public final class EIELPointCADToolContext
extends statemap.FSMContext
{
//	---------------------------------------------------------------
//	Member methods.


	public EIELPointCADToolContext(EIELPointCADTool owner)
	{
		super();

		_owner = owner;
		setState(Point.FirstPoint);
		Point.FirstPoint.Entry(this);
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

	public void removePoint(InputEvent event)
	{
		_transition = "removePoint";
		getState().removePoint(this, event);
		_transition = "";
		return;
	}

	public PointCADToolState getState()
	throws statemap.StateUndefinedException
	{
		if (_state == null)
		{
			throw(
					new statemap.StateUndefinedException());
		}

		return ((PointCADToolState) _state);
	}

	protected EIELPointCADTool getOwner()
	{
		return (_owner);
	}

//	---------------------------------------------------------------
//	Member data.


	transient private EIELPointCADTool _owner;

//	---------------------------------------------------------------
//	Inner classes.


	public static abstract class PointCADToolState
	extends statemap.State
	{
		//-----------------------------------------------------------
		// Member methods.
		//

		protected PointCADToolState(String name, int id)
		{
			super (name, id);
		}

		protected void Entry(EIELPointCADToolContext context) {}
		protected void Exit(EIELPointCADToolContext context) {}

		protected void addOption(EIELPointCADToolContext context, String s)
		{
			Default(context);
		}

		protected void addPoint(EIELPointCADToolContext context, double pointX, double pointY, InputEvent event)
		{
			Default(context);
		}

		protected void addValue(EIELPointCADToolContext context, double d)
		{
			Default(context);
		}

		protected void removePoint(EIELPointCADToolContext context, InputEvent event)
		{
			Default(context);
		}

		protected void Default(EIELPointCADToolContext context)
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

	/* package */ static abstract class Point
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
		/* package */ static Point_Default.Point_FirstPoint FirstPoint;
		/* package */ static Point_Default.Point_PointPainted PointPainted;
		/* package */ static Point_Default.Point_EditForm EditForm;
		private static Point_Default Default;

		static
		{
			FirstPoint = new Point_Default.Point_FirstPoint("Point.FirstPoint", 0);
			PointPainted = new Point_Default.Point_PointPainted("Point.PointPainted", 1);
			EditForm = new Point_Default.Point_EditForm("Point.EditForm", 2);
			Default = new Point_Default("Point.Default", -1);
		}

	}

	protected static class Point_Default
	extends PointCADToolState
	{
		//-----------------------------------------------------------
		// Member methods.
		//

		protected Point_Default(String name, int id)
		{
			super (name, id);
		}

		protected void addOption(EIELPointCADToolContext context, String s)
		{
			EIELPointCADTool ctxt = context.getOwner();

			if (s.equals(PluginServices.getText(this,"cancel")))
			{
				boolean loopbackFlag =
					context.getState().getName().equals(
							Point.FirstPoint.getName());

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
					context.setState(Point.FirstPoint);

					if (loopbackFlag == false)
					{
						(context.getState()).Entry(context);
					}

				}
			}
			else if (s.equals("espacio"))
			{
                
                    if (ActivateFormsExtension.getActivated()) {
                    	VectorialLayerEdited vle=ctxt.getVLE();
                    	OpenFormsExtension.openForm(vle);                   
                	}
                

				// No actions.
			}
			else
			{
				PointCADToolState endState = context.getState();

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

		protected void addValue(EIELPointCADToolContext context, double d)
		{
			EIELPointCADTool ctxt = context.getOwner();

			PointCADToolState endState = context.getState();

			context.clearState();
			try
			{
				ctxt.throwValueException(PluginServices.getText(this,"incorrect_value"), d);
			}
			finally
			{
				context.setState(endState);
			}
			return;
		}

		protected void addPoint(EIELPointCADToolContext context, double pointX, double pointY, InputEvent event)
		{
			EIELPointCADTool ctxt = context.getOwner();

			boolean loopbackFlag =
				context.getState().getName().equals(
						Point.FirstPoint.getName());

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
				context.setState(Point.FirstPoint);

				if (loopbackFlag == false)
				{
					(context.getState()).Entry(context);
				}

			}
			return;
		}

		protected void removePoint(EIELPointCADToolContext context, InputEvent event)
		{
			EIELPointCADTool ctxt = context.getOwner();

			boolean loopbackFlag =
				context.getState().getName().equals(
						Point.FirstPoint.getName());

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
				context.setState(Point.FirstPoint);

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


		private static final class Point_FirstPoint
		extends Point_Default
		{
			//-------------------------------------------------------
			// Member methods.
			//

			private Point_FirstPoint(String name, int id)
			{
				super (name, id);
			}

			protected void Entry(EIELPointCADToolContext context)
			{
				EIELPointCADTool ctxt = context.getOwner();

				ctxt.setQuestion(PluginServices.getText(this,"insert_point"));
				ctxt.setDescription(new String[]{"cancel"});
				return;
			}

			protected void addPoint(EIELPointCADToolContext context, double pointX, double pointY, InputEvent event)
			{
				EIELPointCADTool ctxt = context.getOwner();


				(context.getState()).Exit(context);
				context.clearState();
				try
				{
					ctxt.setQuestion(PluginServices.getText(this,"delete_openform"));
					ctxt.setDescription(new String[]{"cancel"});
					ctxt.addPoint(pointX, pointY, event);
				}
				finally
				{
					context.setState(Point.PointPainted);
					(context.getState()).Entry(context);
				}
				return;
			}

			//-------------------------------------------------------
			// Member data.
			//
		}

		private static final class Point_PointPainted
		extends Point_Default
		{
			//-------------------------------------------------------
			// Member methods.
			//

			private Point_PointPainted(String name, int id)
			{
				super (name, id);
			}

			protected void addOption(EIELPointCADToolContext context, String s)
			{
				EIELPointCADTool ctxt = context.getOwner();

				if (s.equals("espacio"))
				{

					(context.getState()).Exit(context);
					context.clearState();
					try {
//						ctxt.updateFormConstants();
//						ctxt.openForm();
						
						//TODO Next 2 lines added by NachoV to prepare to the next point (Not very nice behaviour)
						ctxt.refresh();
						ctxt.init();
					}
					finally {
						context.setState(Point.EditForm);
						(context.getState()).Entry(context);
						if (ActivateFormsExtension.getActivated()) {
	                    	VectorialLayerEdited vle=ctxt.getVLE();
	                    	OpenFormsExtension.openForm(vle);                   
	                	}
					}
				}
				else 
					
					if (s.equals("espacio")) {

					(context.getState()).Exit(context);
					context.clearState();
					try {
						ctxt.cancel();
					} finally {
						context.setState(Point.FirstPoint);
						(context.getState()).Entry(context);
					}
				} else {	
					super.addOption(context, s);
				}

				return;
			}

			protected void addPoint(EIELPointCADToolContext context, double pointX, double pointY, InputEvent event)
			{


				return;
			}

			protected void removePoint(EIELPointCADToolContext context, InputEvent event)
			{
				EIELPointCADTool ctxt = context.getOwner();


				(context.getState()).Exit(context);
				context.clearState();
				try
				{
					ctxt.setQuestion(PluginServices.getText(this,"insert_point"));
					ctxt.removePoint(event);
				}
				finally
				{
					context.setState(Point.FirstPoint);
					(context.getState()).Entry(context);
				}
				return;
			}

			//-------------------------------------------------------
			// Member data.
			//
		}

		private static final class Point_EditForm
		extends Point_Default
		{
			//-------------------------------------------------------
			// Member methods.
			//

			private Point_EditForm(String name, int id)
			{
				super (name, id);
			}

			protected void addOption(EIELPointCADToolContext context, String s)
			{
				EIELPointCADTool ctxt = context.getOwner();

				if (s.equals(PluginServices.getText(this,"accept_form")))
				{

					(context.getState()).Exit(context);
					context.clearState();
					try
					{
						ctxt.save();
						ctxt.setQuestion(PluginServices.getText(this,"insert_point"));
					}
					finally
					{
						context.setState(Point.FirstPoint);
						(context.getState()).Entry(context);
					}
				}
				else if (s.equals(PluginServices.getText(this,"cancel_form")))
				{

					(context.getState()).Exit(context);
					context.clearState();
					try
					{
						ctxt.initializeFormState();
						ctxt.setQuestion(PluginServices.getText(this,"delete_openform"));
					}
					finally
					{
						context.setState(Point.PointPainted);
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
