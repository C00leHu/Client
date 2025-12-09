package today.vanta.client.module.impl.movement;

import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.MovementUtil;
import net.minecraft.block.Blocks;

public class Speed extends Module {

    private final StringSetting mode = StringSetting.builder()
            .name("Mode")
            .value("Bhop")
            .values("Bhop", "Strafe", "GroundStrafe", "HypixelLowHop", "SpeedTwo")
            .build();

    private final NumberSetting speedValue = new NumberSetting("Speed", 0.3, 0.0, 0.5);
    private final BooleanSetting noScaffold = new BooleanSetting("NoScaffold", true);

    private boolean jumpingThisTick = false;
    private boolean lastTickJumped = false;
    private boolean groundBoostReady = false;
    private int airTicks = 0;
    private double lastAirTicks = 0.0;
    private float yawForStrafe = 0.0f;

    public Speed() {
        super("Speed", "Makes you go faster.", Category.MOVEMENT);
        displayNames = new String[]{"Speed", "FastMove", "Fast Move"};
    }

    @EventListen
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.onGround) {
            if (airTicks > 0) lastAirTicks = airTicks;
            airTicks = 0;
        } else {
            airTicks++;
        }

        switch (mode.getValue()) {
            case "Bhop":
                if (MovementUtil.isMoving()) {
                    if (mc.thePlayer.onGround) {
                        jumpingThisTick = true;
                        mc.thePlayer.jump();
                        jumpingThisTick = false;
                        MovementUtil.strafe((float) speedValue.getValue() + MovementUtil.getSpeed());
                    } else {
                        MovementUtil.strafe((float) speedValue.getValue() * 0.1f + MovementUtil.getSpeed());
                    }
                }
                break;

            case "Strafe":
                if (MovementUtil.isMoving()) {
                    if (mc.thePlayer.onGround) {
                        jumpingThisTick = true;
                        mc.thePlayer.jump();
                        jumpingThisTick = false;
                    }
                    MovementUtil.strafe();
                }
                break;

            case "GroundStrafe":
                if (mc.thePlayer.onGround && MovementUtil.isMoving()) {
                    jumpingThisTick = true;
                    mc.thePlayer.jump();
                    jumpingThisTick = false;
                }
                break;

            case "HypixelLowHop":
            case "SpeedTwo":
                handleLowHop();
                break;
        }
    }

    private void handleLowHop() {
        // basic velocity boost logic (simplified)
        if (mc.thePlayer.onGround) {
            mc.thePlayer.jump();
            MovementUtil.strafe((float) speedValue.getValue() + MovementUtil.getSpeed());
        } else {
            // mid-air strafe/velocity
            MovementUtil.strafe(MovementUtil.getSpeed());
        }
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSprint.pressed = false;
        mc.gameSettings.keyBindJump.pressed = false;
    }

    @Override
    public String getSuffix() {
        return mode.getValue();
    }
}
