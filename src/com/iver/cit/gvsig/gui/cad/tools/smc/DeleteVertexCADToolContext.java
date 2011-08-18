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

import com.iver.cit.gvsig.gui.cad.tools.DeleteVertexCADTool;
import java.awt.event.InputEvent;
import com.iver.andami.PluginServices;


//TODO [NachoV] This is a very modified copy of EditVertexCADToolCOntext,
//            so we must to remove all Add, Edit code parts... on the states...

/**
 * @author Vicente Caballero Navarro
 * @author Francisco Puga <fpuga (at) cartolab.es>
 */
public final class DeleteVertexCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public DeleteVertexCADToolContext(DeleteVertexCADTool owner)
    {
        super();

        _owner = owner;
        setState(DeleteVertex.SelectVertexOrDelete);
        DeleteVertex.SelectVertexOrDelete.Entry(this);
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

    public DeleteVertexCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((DeleteVertexCADToolState) _state);
    }

    protected DeleteVertexCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private DeleteVertexCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class DeleteVertexCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected DeleteVertexCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(DeleteVertexCADToolContext context) {}
        protected void Exit(DeleteVertexCADToolContext context) {}

        protected void addOption(DeleteVertexCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(DeleteVertexCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void addValue(DeleteVertexCADToolContext context, double d)
        {
            Default(context);
        }

        protected void Default(DeleteVertexCADToolContext context)
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

    /* package */ static abstract class DeleteVertex
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
        /* package */ static DeleteVertex_Default.DeleteVertex_SelectVertexOrDelete SelectVertexOrDelete;
        /* package */ static DeleteVertex_Default.DeleteVertex_AddVertex AddVertex;
        private static DeleteVertex_Default Default;

        static
        {
            SelectVertexOrDelete = new DeleteVertex_Default.DeleteVertex_SelectVertexOrDelete("DeleteVertex.SelectVertexOrDelete", 0);
            AddVertex = new DeleteVertex_Default.DeleteVertex_AddVertex("DeleteVertex.AddVertex", 1);
            Default = new DeleteVertex_Default("DeleteVertex.Default", -1);
        }

    }

    protected static class DeleteVertex_Default
        extends DeleteVertexCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected DeleteVertex_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(DeleteVertexCADToolContext context, String s)
        {
            DeleteVertexCADTool ctxt = context.getOwner();

            if (s.equals(PluginServices.getText(this,"cancel")))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        DeleteVertex.SelectVertexOrDelete.getName());

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
                    context.setState(DeleteVertex.SelectVertexOrDelete);

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
                        DeleteVertex.SelectVertexOrDelete.getName());

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
                    context.setState(DeleteVertex.SelectVertexOrDelete);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }

            return;
        }

        protected void addValue(DeleteVertexCADToolContext context, double d)
        {
            DeleteVertexCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    DeleteVertex.SelectVertexOrDelete.getName());

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
                context.setState(DeleteVertex.SelectVertexOrDelete);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void addPoint(DeleteVertexCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            DeleteVertexCADTool ctxt = context.getOwner();

            boolean loopbackFlag =
                context.getState().getName().equals(
                    DeleteVertex.SelectVertexOrDelete.getName());

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
                context.setState(DeleteVertex.SelectVertexOrDelete);

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


        private static final class DeleteVertex_SelectVertexOrDelete
            extends DeleteVertex_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private DeleteVertex_SelectVertexOrDelete(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(DeleteVertexCADToolContext context)
            {
                DeleteVertexCADTool ctxt = context.getOwner();

                ctxt.selection();
                ctxt.setQuestion(PluginServices.getText(this,"next_previous_add_del_cancel"));
                ctxt.setDescription(new String[]{"next", "previous", "add", "del", "cancel"});
                return;
            }

//            protected void addOption(DeleteVertexCADToolContext context, String s)
//            {
//                DeleteVertexCADTool ctxt = context.getOwner();
//
//                if (s.equals("i") || s.equals("I") || s.equals(PluginServices.getText(this,"add")))
//                {
//
//                    (context.getState()).Exit(context);
//                    context.clearState();
//                    try
//                    {
//                        ctxt.setQuestion(PluginServices.getText(this,"add_vertex"));
//                        ctxt.setDescription(new String[]{"cancel"});
//                        ctxt.addOption(s);
//                    }
//                    finally
//                    {
//                        context.setState(DeleteVertex.AddVertex);
//                        (context.getState()).Entry(context);
//                    }
//                }
//                else if (!s.equals("i") && !s.equals("I") && !s.equals(PluginServices.getText(this,"add")))
//                {
//                    DeleteVertexCADToolState endState = context.getState();
//
//                    context.clearState();
//                    try
//                    {
//                        ctxt.setQuestion(PluginServices.getText(this,"next_previous_add_del_cancel"));
//                        ctxt.setDescription(new String[]{"next", "previous", "add", "del", "cancel"});
//                        ctxt.addOption(s);
//                    }
//                    finally
//                    {
//                        context.setState(endState);
//                    }
//                }                else
//                {
//                    super.addOption(context, s);
//                }
//
//                return;
//            }

            protected void addPoint(DeleteVertexCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                DeleteVertexCADTool ctxt = context.getOwner();

                DeleteVertexCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"select_from_point"));
                    ctxt.setDescription(new String[]{"next", "previous", "add", "del", "cancel"});
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

        private static final class DeleteVertex_AddVertex
            extends DeleteVertex_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private DeleteVertex_AddVertex(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(DeleteVertexCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                DeleteVertexCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion(PluginServices.getText(this,"select_from_point"));
                    ctxt.setDescription(new String[]{"next", "previous", "add", "del", "cancel"});
                    ctxt.addPoint(pointX, pointY, event);
                }
                finally
                {
                    context.setState(DeleteVertex.SelectVertexOrDelete);
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
