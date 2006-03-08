
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.EditVertexCADTool;

public final class EditVertexCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public EditVertexCADToolContext(EditVertexCADTool owner)
    {
        super();

        _owner = owner;
        setState(EditVertex.SelectVertexOrDelete);
        EditVertex.SelectVertexOrDelete.Entry(this);
    }

    public void addOption(String s)
    {
        _transition = "addOption";
        getState().addOption(this, s);
        _transition = "";
        return;
    }

    public EditVertexCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((EditVertexCADToolState) _state);
    }

    protected EditVertexCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private EditVertexCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class EditVertexCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected EditVertexCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(EditVertexCADToolContext context) {}
        protected void Exit(EditVertexCADToolContext context) {}

        protected void addOption(EditVertexCADToolContext context, String s)
        {
            Default(context);
        }

        protected void Default(EditVertexCADToolContext context)
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

    /* package */ static abstract class EditVertex
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
        /* package */ static EditVertex_Default.EditVertex_SelectVertexOrDelete SelectVertexOrDelete;
        private static EditVertex_Default Default;

        static
        {
            SelectVertexOrDelete = new EditVertex_Default.EditVertex_SelectVertexOrDelete("EditVertex.SelectVertexOrDelete", 0);
            Default = new EditVertex_Default("EditVertex.Default", -1);
        }

    }

    protected static class EditVertex_Default
        extends EditVertexCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected EditVertex_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(EditVertexCADToolContext context, String s)
        {
            EditVertexCADTool ctxt = context.getOwner();

            if (s.equals("Cancelar"))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        EditVertex.SelectVertexOrDelete.getName());

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
                    context.setState(EditVertex.SelectVertexOrDelete);

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


        private static final class EditVertex_SelectVertexOrDelete
            extends EditVertex_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private EditVertex_SelectVertexOrDelete(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(EditVertexCADToolContext context)
            {
                EditVertexCADTool ctxt = context.getOwner();

                ctxt.selection();
                ctxt.setQuestion("EDITAR VERTICES" + "\n" +
		"Siguiente vertice, Anterior, Anyadir o Eliminar");
                ctxt.setDescription(new String[]{"Siguiente", "Anterior", "Anyadir", "Eliminar", "Cancelar"});
                return;
            }

            protected void addOption(EditVertexCADToolContext context, String s)
            {
                EditVertexCADTool ctxt = context.getOwner();

                EditVertexCADToolState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setQuestion("Siguiente vertice, aNyadir, Anterior o Eliminar");
                    ctxt.setDescription(new String[]{"Siguiente", "Anterior", "Anyadir", "Eliminar", "Cancelar"});
                    ctxt.addOption(s);
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
