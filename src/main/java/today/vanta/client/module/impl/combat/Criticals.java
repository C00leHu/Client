package today.vanta.client.module.impl.combat;

import today.vanta.client.module.Category;
import today.vanta.client.module.Module;

public class Criticals extends Module {
    public Criticals() {
        super("Criticals", "Tries to make all landed hits critical.", Category.COMBAT);
    }
}