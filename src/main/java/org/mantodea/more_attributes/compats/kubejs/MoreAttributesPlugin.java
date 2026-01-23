package org.mantodea.more_attributes.compats.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import org.mantodea.more_attributes.compats.kubejs.bindings.MoreAttributesBinding;

public class MoreAttributesPlugin extends KubeJSPlugin {
    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("MoreAttributes", MoreAttributesBinding.class);
    }
}
