package com.iver.cit.gvsig;

import com.iver.andami.plugins.Extension;

public abstract class BaseCADExtension extends Extension {

    @Override
    public boolean isVisible() {
	return EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE;
    }

}
