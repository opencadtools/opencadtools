/*
 * 
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
import com.iver.cit.gvsig.gui.cad.tools.InsertVertexCADTool;


//TODO [NachoV] This is a very modified copy of EditVertexCADToolCOntext, 
//            so we must to remove all Add, Edit code parts... on the states...

/**
 * @author Vicente Caballero Navarro
 * @author Nacho Uve [Cartolab]
 */
public final class InsertVertexCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public InsertVertexCADToolContext(InsertVertexCADTool owner)
    {
        super();

        _owner = owner;
        setState(InsertVertex.SelectVertexOrDelete);
        InsertVertex.SelectVertexOrDelete.Entry(this);
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

    public InsertVertexCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((InsertVertexCADToolState) _state);
    }

    protected InsertVertexCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private InsertVertexCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class InsertVertexCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected InsertVertexCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(InsertVertexCADToolContext context) {}
        protected void Exit(InsertVertexCADToolContext context) {}

        protected void addOption(InsertVertexCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(InsertVertexCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void addValue(InsertVertexCADToolContext context, double d)
        {
            Default(context);
        }

        protected void Default(InsertVertexCADToolContext context)
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

    /* package */ static abstract class InsertVertex
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
        /* package */ static InsertVertex_Default.InsertVertex_SelectVertexOrDelete SelectVertexOrDelete;
        private static InsertVertex_Default Default;

        static
        {
            SelectVertexOrDelete = new InsertVertex_Default.InsertVertex_SelectVertexOrDelete("InsertVertex.SelectVertexOrDelete", 0);
            Default = new InsertVertex_Default("InsertVertex.Default", -1);
        }

    }

    protected static class InsertVertex_Default
        extends InsertVertexCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected InsertVertex_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(InsertVertexCADToolContext context, String s)
        {
        	InsertVertexCADTool ctxt = context.getOwner();

        	boolean loopbackFlag =
        		context.getState().getName().equals(
        				InsertVertex.SelectVertexOrDelete.getName());

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
        		context.setState(InsertVertex.SelectVertexOrDelete);

        		if (loopbackFlag == false)
        		{
        			(context.getState()).Entry(context);
        		}

        	}

        	return;
        }

        protected void addValue(InsertVertexCADToolContext context, double d)
        {
            InsertVertexCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    InsertVertex.SelectVertexOrDelete.getName());

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
                context.setState(InsertVertex.SelectVertexOrDelete);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void addPoint(InsertVertexCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            InsertVertexCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    InsertVertex.SelectVertexOrDelete.getName());

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
                context.setState(InsertVertex.SelectVertexOrDelete);

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


        private static final class InsertVertex_SelectVertexOrDelete
            extends InsertVertex_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private InsertVertex_SelectVertexOrDelete(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(InsertVertexCADToolContext context)
            {
                InsertVertexCADTool ctxt = context.getOwner();

                ctxt.selection();
                ctxt.setQuestion(PluginServices.getText(this,"add_vertex"));
                ctxt.setDescription(new String[0]);
                return;
            }

            protected void addPoint(InsertVertexCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                InsertVertexCADTool ctxt = context.getOwner();

                InsertVertexCADToolState endState = context.getState();

                context.clearState();
                try
                {
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
