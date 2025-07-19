package chylex.hee.system.integration.handlers;

import baubles.api.expanded.BaubleExpandedSlots;
import chylex.hee.system.integration.IIntegrationHandler;

public class BaublesExpandedIntegration implements IIntegrationHandler {

    public static final String BAUBLESLOT = "charm_pouch";

    @Override
    public String getModId() {
        return "Baubles|Expanded";
    }

    @Override
    public void integrate() {
        BaubleExpandedSlots.tryRegisterType(BAUBLESLOT);
        BaubleExpandedSlots.tryAssignSlotOfType(BAUBLESLOT);
    }
}
